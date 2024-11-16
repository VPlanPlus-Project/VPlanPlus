package es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_DEFAULT_NOTIFICATION_ID_NEW_HOMEWORK
import es.jvbabi.vplanplus.domain.repository.OpenScreenTask
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskDone
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.ui.NotificationDestination
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils.relativeDateStringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class UpdateHomeworkUseCase(
    private val profileRepository: ProfileRepository,
    private val homeworkRepository: HomeworkRepository,
    private val fileRepository: FileRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository
) {

    private var isUpdateRunning: Boolean = false

    fun isUpdateRunning() = flow {
        while (true) {
            emit(isUpdateRunning)
            delay(200)
        }
    }.distinctUntilChanged()

    /**
     * Updates the homework in the database.
     */
    suspend operator fun invoke(allowNotifications: Boolean = true): Boolean {
        val stopUpdate: (withSuccess: Boolean) -> Boolean = { withSuccess ->
            isUpdateRunning = false
            withSuccess
        }
        val updateExisting: suspend () -> List<HomeworkCore.CloudHomework> = { homeworkRepository.getAll().first().filterIsInstance<HomeworkCore.CloudHomework>() }

        if (isUpdateRunning) return true
        isUpdateRunning = true

        val initialExisting = updateExisting()
        var existingHomework = updateExisting()
        val downloadedHomeworkItems = mutableListOf<HomeworkCore.CloudHomework>()

        profileRepository
            .getProfiles().first()
            .filterIsInstance<ClassProfile>()
            .filter { it.isHomeworkEnabled }
            .distinctBy { it.id.toString() + it.vppId?.id } // only unique profile/vpp.ID combinations (including profiles without vpp.ID)
            .sortedBy { it.group.groupId.toString() + if (it.vppId == null) "z" else "a" } // rank profiles with vpp.ID higher
            .forEach { profile ->
                val downloadedHomework = homeworkRepository.downloadHomework(profile.vppId, profile.group) ?: return false
                downloadedHomework.forEach { downloadedHomeworkItem ->
                    val existingItem = existingHomework.find { it.id == downloadedHomeworkItem.id }
                    if (existingItem == null) {
                        val homeworkId = homeworkRepository.addHomeworkDb(
                            homeworkId = downloadedHomeworkItem.id,
                            isPublic = downloadedHomeworkItem.isPublic,
                            dueTo = downloadedHomeworkItem.until,
                            clazzProfile = profile,
                            createdAt = downloadedHomeworkItem.createdAt,
                            vppId = downloadedHomeworkItem.createdBy,
                            defaultLessonVpId = downloadedHomeworkItem.defaultLesson?.vpId
                        )
                        if (downloadedHomeworkItem.shouldBeHidden(profile)) {
                            val hw = homeworkRepository.getHomeworkById(homeworkId).first() as HomeworkCore.CloudHomework
                            homeworkRepository.changeHomeworkVisibilityDb(hw, profile, true)
                        }
                    }
                    downloadedHomeworkItems.add(downloadedHomeworkItem)

                    downloadedHomeworkItem.tasks.forEach forEachDownloadedTask@{ task ->
                        homeworkRepository.addTaskDb(
                            homeworkId = downloadedHomeworkItem.id,
                            content = task.content,
                            taskId = task.id,
                        )
                        if (task is HomeworkTaskDone) homeworkRepository.changeTaskStateDb(profile, task.id, task.isDone)
                    }

                    downloadedHomeworkItem.documents.forEach { document ->
                        homeworkRepository.addDocumentDb(
                            documentId = document.documentId,
                            homeworkId = downloadedHomeworkItem.id,
                            type = document.type,
                            name = document.name ?: "Untitled",
                            size = document.size
                        )
                    }

                    val tasksToDelete = existingItem?.tasks.orEmpty().filter { task -> downloadedHomeworkItem.tasks.none { it.id == task.id } }
                    tasksToDelete.forEach { task ->
                        homeworkRepository.deleteTaskDb(task)
                    }

                    val documentsToDelete = existingItem?.documents.orEmpty().filter { document -> downloadedHomeworkItem.documents.none { it.documentId == document.documentId } }
                    documentsToDelete.forEach { document ->
                        homeworkRepository.deleteDocumentDb(document)
                        fileRepository.deleteFile("homework_documents", "${document.documentId}.${document.type.extension}")
                    }
                }
                existingHomework = updateExisting()
            }


        val homeworkToDelete = initialExisting.filter { existing -> downloadedHomeworkItems.none { it.id == existing.id } }
        Log.d("UpdateHomeworkUseCase", "Deleting ${homeworkToDelete.size} homework items")
        homeworkToDelete.forEach { homeworkToDeleteItem ->
            homeworkToDeleteItem.tasks.forEach { task ->
                homeworkRepository.deleteTaskDb(task)
            }
            homeworkToDeleteItem.documents.forEach { document ->
                homeworkRepository.deleteDocumentDb(document)
                fileRepository.deleteFile("homework_documents", "${document.documentId}.${document.type.extension}")
            }
            homeworkRepository.deleteHomeworkDb(homeworkToDeleteItem)
        }

        Log.d("UpdateHomeworkUseCase", "Homework updated")

        if (!allowNotifications) return stopUpdate(true)

        profileRepository
            .getProfiles().first()
            .filterIsInstance<ClassProfile>()
            .filter { it.notificationsEnabled && it.notificationSettings.onNewHomeworkNotificationSetting.isEnabled() }
            .forEach forEachProfile@{ profile ->
                val notificationNewHomeworkItems = downloadedHomeworkItems
                    .filter {
                        it.id !in initialExisting.map { existing -> existing.id } && // is new
                                it.group.groupId == profile.group.groupId && // is in same group
                                it.createdBy.id != profile.vppId?.id && // is not created by current user
                                profile.isDefaultLessonEnabled(it.defaultLesson?.vpId) && // is not created by current user
                                !it.shouldBeHidden(profile) // is not hidden
                    }
                    .ifEmpty { return@forEachProfile }

                if (notificationNewHomeworkItems.size == 1) { // detailed notification
                    val notificationHomework = notificationNewHomeworkItems.first()

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
                        subtitle = profile.displayName,
                        message = message,
                        onClickTask = OpenScreenTask(
                            destination = Json.encodeToString(NotificationDestination(
                                profileId = profileRepository.getProfiles().first().firstOrNull { it is ClassProfile && it.group == notificationHomework.group }?.id?.toString(),
                                screen = "homework/item",
                                payload = Json.encodeToString(Screen.HomeworkDetailScreen(notificationHomework.id))
                            )
                            )
                        )
                    )
                }
                val message = stringRepository.getString(R.string.notification_homeworkNewHomeworkMultipleContent, notificationNewHomeworkItems.size)
                Log.d("UpdateHomeworkUseCase", "Sending notification for new homework: $message")

                notificationRepository.sendNotification(
                    channelId = NotificationRepository.CHANNEL_ID_HOMEWORK,
                    id = CHANNEL_DEFAULT_NOTIFICATION_ID_NEW_HOMEWORK,
                    icon = R.drawable.vpp,
                    title = stringRepository.getString(R.string.notification_homeworkNewHomeworkMultipleTitle),
                    subtitle = profile.displayName,
                    message = message,
                    onClickTask = OpenScreenTask(
                        destination = Json.encodeToString(
                            NotificationDestination(
                                screen = "homework",
                            )
                        ),
                    )
                )
        }

        return stopUpdate(true)
    }
}

private fun HomeworkCore.CloudHomework.shouldBeHidden(profile: ClassProfile): Boolean =
    !profile.isDefaultLessonEnabled(defaultLesson?.vpId) || until.toLocalDate().isBefore(LocalDate.now().minusDays(3))