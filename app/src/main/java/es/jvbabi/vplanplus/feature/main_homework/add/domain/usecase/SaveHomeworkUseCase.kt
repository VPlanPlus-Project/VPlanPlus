package es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase

import android.net.Uri
import androidx.core.net.toFile
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.util.sha256
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

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
    ): Boolean {
        if (tasks.isEmpty()) return false
        val profile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return false

        val dueTo = ZonedDateTime.of(until, LocalTime.of(0, 0, 0), ZoneId.of("UTC"))
        var homeworkId: Int? = null
        val taskMap: MutableMap<String, Int?> = mutableMapOf()
        val createdAt = ZonedDateTime.now()

        if (storeInCloud && profile.vppId != null) {
            val ids = homeworkRepository.addHomeworkCloud(profile.vppId, isPublic = shareWithClass, vpId = defaultLesson?.vpId, dueTo = dueTo, tasks = tasks).value ?: return false
            homeworkId = ids.id
            ids.tasks.forEach { (id, contentSHA256) ->
                taskMap += tasks.first { it.sha256() == contentSHA256 } to id
            }
        } else {
            tasks.forEach { taskMap += it to null }
        }

        homeworkId = homeworkRepository.addHomeworkDb(
            homeworkId = homeworkId,
            isPublic = shareWithClass,
            createdAt = createdAt,
            dueTo = dueTo,
            defaultLessonVpId = defaultLesson?.vpId,
            vppId = if (storeInCloud) profile.vppId else null,
            clazzProfile = profile
        )

        taskMap.forEach { task ->
            homeworkRepository.addTaskDb(
                homeworkId = homeworkId,
                content = task.key,
                taskId = task.value
            )
        }

        documentUris.forEach { (uri, type) ->
            val content = fileRepository.readBytes(uri.toFile()) ?: return false
            val name = uri.toFile().name
            var documentId: Int? = null
            if (storeInCloud && profile.vppId != null) {
                documentId = homeworkRepository.addDocumentCloud(
                    vppId = profile.vppId,
                    name = name,
                    homeworkId = homeworkId,
                    content = content,
                    type = type,
                    onUploading = { sent, _ -> onDocumentUploadProgress(uri, sent.toFloat() / content.size) }
                ).value ?: return false
            }
            documentId = homeworkRepository.addDocumentDb(
                documentId = documentId,
                homeworkId = homeworkId,
                name = name,
                type = type
            )
            fileRepository.writeBytes("homework_documents", "$documentId.${type.extension}", content)
        }

        return true
    }
}