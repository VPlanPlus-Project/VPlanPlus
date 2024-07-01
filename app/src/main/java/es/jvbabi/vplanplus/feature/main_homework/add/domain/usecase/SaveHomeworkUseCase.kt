package es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase

import android.net.Uri
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.Document
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.NewTaskRecord
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class SaveHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
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
        val profile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return HomeworkModificationResult.FAILED

        val dueTo = ZonedDateTime.of(until, LocalTime.of(0, 0, 0), ZoneId.of("UTC"))

        return homeworkRepository.insertHomework(
            profile = profile,
            until = dueTo,
            defaultLessonVpId = defaultLesson?.vpId,
            tasks = tasks.map { NewTaskRecord(it.trim()) }.filterNot { it.content.isBlank() },
            storeInCloud = storeInCloud,
            shareWithClass = shareWithClass,
            createdAt = ZonedDateTime.now(),
            isHidden = false,
            documentUris = documentUris.entries.map { (uri, type) ->
                Document(
                    uri = uri,
                    extension = when (type) {
                        HomeworkDocumentType.JPG -> "jpg"
                        HomeworkDocumentType.PDF -> "pdf"
                    },
                )
            },
            onDocumentUploadProgressChanges = onDocumentUploadProgress
        )
    }
}