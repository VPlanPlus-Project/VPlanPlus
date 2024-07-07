package es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_DEFAULT_NOTIFICATION_ID_NEW_HOMEWORK
import es.jvbabi.vplanplus.domain.repository.OpenScreenTask
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils.relativeDateStringResource
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class UpdateHomeworkUseCase(
    private val profileRepository: ProfileRepository,
    private val vppIdRepository: VppIdRepository,
    private val homeworkRepository: HomeworkRepository,
    private val fileRepository: FileRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository
) {

    var isUpdateRunning: Boolean = false
        private set

    /**
     * Updates the homework in the database.
     */
    suspend operator fun invoke(allowNotifications: Boolean = true): Boolean {
        if (isUpdateRunning) return true
        isUpdateRunning = true
        val vppIds = vppIdRepository.getActiveVppIds().first()
        val profiles = profileRepository
            .getProfiles()
            .first()
            .filterIsInstance<ClassProfile>()
            .map { it.group to it.vppId }
            .distinct()

        Log.d("UpdateHomeworkUseCase", "Updating homework for ${profiles.size} profiles")

        val homework = mutableListOf<AddHomeworkItem>()
        val existing = homeworkRepository.getAll().first().filterIsInstance<CloudHomework>()

        profiles.forEach { (group, vppId) ->
            val new = (homeworkRepository.downloadHomework(vppId, group) ?: return false).filter { it.id !in homework.map { hw -> hw.homework.id } }
            Log.d("UpdateHomeworkUseCase", "New homework for group ${group.name} (${group.groupId}): ${new.size}")
            homework.addAll(new.map { homework ->
                if (!homework.isPublic && vppId != null) AddHomeworkItem.PrivateHomework(vppId, homework)
                else AddHomeworkItem.PublicHomework(homework)
            })
        }

        homework.forEach { item ->
            val existingItem = existing.find { it.id == item.homework.id }
            homeworkRepository.addHomeworkDb(
                homeworkId = item.homework.id.toInt(),
                isHidden = item.homework.isHidden, // todo
                createdAt = item.homework.createdAt,
                vppId = item.homework.createdBy,
                isPublic = item.homework.isPublic,
                dueTo = item.homework.until,
                defaultLessonVpId = item.homework.defaultLesson?.vpId,
            )

            item.homework.tasks.forEach { task ->
                homeworkRepository.addTaskDb(
                    homeworkId = item.homework.id.toInt(),
                    content = task.content,
                    isDone = task.isDone,
                    taskId = task.id,
                )
            }
            val tasksToDelete = existingItem?.tasks.orEmpty().filter { task -> item.homework.tasks.none { it.id == task.id } }
            tasksToDelete.forEach { task ->
                homeworkRepository.deleteTaskDb(task)
            }

            item.homework.documents.forEach forEachDocument@{ document ->
                if (!fileRepository.exists("homework_documents", document.documentId.toString())) {
                    Log.d("UpdateHomeworkUseCase", "Downloading document ${document.documentId}")
                    val content =
                        when (item) {
                            is AddHomeworkItem.PublicHomework -> homeworkRepository.downloadHomeworkDocument(null, item.homework.group, item.homework.id.toInt(), document.documentId)
                            is AddHomeworkItem.PrivateHomework -> homeworkRepository.downloadHomeworkDocument(item.vppId, item.homework.group, item.homework.id.toInt(), document.documentId)
                        } ?: return@forEachDocument
                    fileRepository.writeBytes("homework_documents", document.documentId.toString(), content)
                    homeworkRepository.addDocumentDb(
                        documentId = document.documentId,
                        homeworkId = item.homework.id.toInt(),
                        type = document.type,
                        name = document.name ?: "Untitled"
                    )
                }
            }

            val documentsToDelete = existingItem?.documents.orEmpty().filter { document -> item.homework.documents.none { it.documentId == document.documentId } }
            documentsToDelete.forEach { document ->
                homeworkRepository.deleteDocumentDb(document)
                fileRepository.deleteFile("homework_documents", document.documentId.toString())
            }
        }

        val homeworkToDelete = existing.filter { it.id !in homework.map { hw -> hw.homework.id } }
        Log.d("UpdateHomeworkUseCase", "Deleting ${homeworkToDelete.size} homework items")
        homeworkToDelete.forEach { homeworkToDeleteItem ->
            homeworkToDeleteItem.tasks.forEach { task ->
                homeworkRepository.deleteTaskDb(task)
            }
            homeworkToDeleteItem.documents.forEach { document ->
                homeworkRepository.deleteDocumentDb(document)
                fileRepository.deleteFile("homework_documents", document.documentId.toString())
            }
            homeworkRepository.deleteHomeworkDb(homeworkToDeleteItem)
        }

        Log.d("UpdateHomeworkUseCase", "Homework updated")

        if (!allowNotifications) return true

        val notificationNewHomeworkItems = homework
            .filter { it.homework.id !in existing.map { hw -> hw.id } } // is new
            .filter { it.homework.createdBy.id !in vppIds.map { vppId -> vppId.id } } // is not created by current user

        if (notificationNewHomeworkItems.isEmpty()) return true
        if (notificationNewHomeworkItems.size == 1) { // detailed notification
            val notificationHomework = notificationNewHomeworkItems.first().homework

            val tasksString = stringRepository.getPlural(R.plurals.notification_homeworkNewHomeworkOneContentTasks, notificationHomework.tasks.size, notificationHomework.tasks.size)
            val relativeDueDateResource = relativeDateStringResource(LocalDate.now(), notificationHomework.until.toLocalDate())
            val dueToString = if (relativeDueDateResource != null) stringRepository.getString(relativeDueDateResource) else notificationHomework.until.toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
            val messageContent = stringRepository.getString(R.string.notification_homeworkNewHomeworkOneContent, notificationHomework.createdBy.name, tasksString, dueToString)
            val message = if (notificationHomework.defaultLesson != null) stringRepository.getString(R.string.notification_homeworkNewHomeworkOneContentPrefix, notificationHomework.defaultLesson.subject, messageContent) else messageContent

            Log.d("UpdateHomeworkUseCase", "Sending notification for new homework: $message")

            notificationRepository.sendNotification(
                channelId = NotificationRepository.CHANNEL_ID_HOMEWORK,
                id = CHANNEL_DEFAULT_NOTIFICATION_ID_NEW_HOMEWORK,
                icon = R.drawable.vpp,
                title = stringRepository.getString(R.string.notification_homeworkNewHomeworkOneTitle),
                message = message,
                onClickTask = OpenScreenTask(Screen.HomeworkDetailScreen.route + "/${notificationHomework.id}")
            )
            return true
        }

        val message = stringRepository.getString(R.string.notification_homeworkNewHomeworkMultipleContent, notificationNewHomeworkItems.size)
        Log.d("UpdateHomeworkUseCase", "Sending notification for new homework: $message")

        notificationRepository.sendNotification(
            channelId = NotificationRepository.CHANNEL_ID_HOMEWORK,
            id = CHANNEL_DEFAULT_NOTIFICATION_ID_NEW_HOMEWORK,
            icon = R.drawable.vpp,
            title = stringRepository.getString(R.string.notification_homeworkNewHomeworkMultipleTitle),
            message = message,
            onClickTask = OpenScreenTask(Screen.HomeworkScreen.route)
        )

        return true
    }
}

private sealed class AddHomeworkItem(
    val homework: CloudHomework
) {
    class PublicHomework(homework: CloudHomework) : AddHomeworkItem(homework)
    class PrivateHomework(
        val vppId: VppId,
        homework: CloudHomework
    ) : AddHomeworkItem(homework)
}