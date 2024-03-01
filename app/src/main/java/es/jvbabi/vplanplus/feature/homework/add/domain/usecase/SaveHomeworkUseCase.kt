package es.jvbabi.vplanplus.feature.homework.add.domain.usecase

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.NewTaskRecord
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
        defaultLesson: DefaultLesson,
        tasks: List<String>,
    ): HomeworkModificationResult {
        val identity = getCurrentIdentityUseCase().first() ?: return HomeworkModificationResult.FAILED
        val `class` = getClassByProfileUseCase(identity.profile!!)!!

        val dueTo = ZonedDateTime.of(until, LocalTime.of(0, 0, 0), ZoneId.of("UTC"))

        return homeworkRepository.insertHomework(
            createdBy = identity.vppId,
            `class` = `class`,
            until = dueTo,
            defaultLessonVpId = defaultLesson.vpId,
            tasks = tasks.map { NewTaskRecord(
                it.trim()
            ) },
            allowCloudUpdate = true,
            shareWithClass = shareWithClass,
            createdAt = ZonedDateTime.now(),
            isHidden = false
        )
    }
}