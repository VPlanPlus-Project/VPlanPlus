package es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase

import android.net.Uri
import es.jvbabi.vplanplus.data.repository.FileRepository
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.util.sha256
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

class SaveHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val fileRepository: FileRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke(
        until: LocalDate,
        shareWithClass: Boolean,
        storeInCloud: Boolean,
        defaultLesson: DefaultLesson?,
        tasks: List<String>,
        documentUris: Map<Uri, HomeworkDocumentType>,
        onDocumentUploadProgress: (Uri, Float) -> Unit
    ): HomeworkModificationResult {
        if (tasks.isEmpty()) return HomeworkModificationResult.FAILED
        val profile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return HomeworkModificationResult.FAILED

        val dueTo = ZonedDateTime.of(until, LocalTime.of(0, 0, 0), ZoneId.of("UTC"))
        var homeworkId: Int? = null
        val taskMap: MutableMap<String, Int?> = mutableMapOf()
        val createdAt = ZonedDateTime.now()

        if (storeInCloud && profile.vppId != null) {
            val ids = homeworkRepository.uploadHomework(profile.vppId, isPublic = shareWithClass, vpId = defaultLesson?.vpId, dueTo = dueTo, tasks = tasks).value ?: return HomeworkModificationResult.FAILED
            homeworkId = ids.id
            ids.tasks.forEach { (id, contentHash) ->
                taskMap += tasks.first { it.sha256() == contentHash } to id
            }
        } else {
            tasks.forEach { taskMap += it to null }
        }

        homeworkId = homeworkRepository.addHomeworkToDb(
            homeworkId = homeworkId,
            isPublic = shareWithClass,
            createdAt = createdAt,
            dueTo = dueTo,
            isHidden = false,
            defaultLessonVpId = defaultLesson?.vpId,
            vppId = if (storeInCloud) profile.vppId else null,
            clazzProfile = profile
        )

        taskMap.forEach { task ->
            homeworkRepository.addHomeworkTaskToDb(
                homeworkId = homeworkId,
                content = task.key,
                taskId = task.value
            )
        }

        documentUris.forEach { (uri, type) ->
            val content = fileRepository.readBytes(uri) ?: return HomeworkModificationResult.FAILED
            val name = UUID.randomUUID().toString()
            var documentId: Int? = null
            if (storeInCloud && profile.vppId != null) {
                documentId = homeworkRepository.uploadDocument(
                    vppId = profile.vppId,
                    name = name,
                    content = content,
                    type = type,
                    onUploading = { sent, _ -> onDocumentUploadProgress(uri, sent.toFloat() / content.size) }
                ).value ?: return HomeworkModificationResult.FAILED
            }
            documentId = homeworkRepository.addDocumentToDb(
                documentId = documentId,
                homeworkId = homeworkId,
                name = name,
                type = type
            )
            fileRepository.writeBytes("homework_documents", documentId.toString(), content)
        }

        return if (storeInCloud) HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE else HomeworkModificationResult.SUCCESS_OFFLINE
    }
}