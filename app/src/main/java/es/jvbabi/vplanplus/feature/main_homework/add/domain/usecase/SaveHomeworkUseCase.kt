package es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase

import android.net.Uri
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
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
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val getClassByProfileUseCase: GetClassByProfileUseCase
) {
    suspend operator fun invoke(
        until: LocalDate,
        shareWithClass: Boolean,
        storeInCloud: Boolean,
        defaultLesson: DefaultLesson?,
        tasks: List<String>,
        documentUris: List<Uri>
    ): HomeworkModificationResult {
        val identity = getCurrentIdentityUseCase().first() ?: return HomeworkModificationResult.FAILED
        val `class` = getClassByProfileUseCase(identity.profile!!)!!

        val dueTo = ZonedDateTime.of(until, LocalTime.of(0, 0, 0), ZoneId.of("UTC"))

        return homeworkRepository.insertHomework(
            profile = identity.profile,
            `class` = `class`,
            until = dueTo,
            defaultLessonVpId = defaultLesson?.vpId,
            tasks = tasks.map { NewTaskRecord(it.trim()) }.filterNot { it.content.isBlank() },
            storeInCloud = storeInCloud,
            shareWithClass = shareWithClass,
            createdAt = ZonedDateTime.now(),
            isHidden = false,
            documentUris = documentUris
        )
    }
}