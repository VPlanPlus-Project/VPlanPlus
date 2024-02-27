package es.jvbabi.vplanplus.feature.homework.add.domain.usecase

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime

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
    ) {
        val identity = getCurrentIdentityUseCase().first() ?: return
        val `class` = getClassByProfileUseCase(identity.profile!!)!!

        homeworkRepository.insertHomework(
            createdBy = identity.vppId,
            `class` = `class`,
            until = until,
            defaultLessonVpId = defaultLesson.vpId,
            tasks = tasks,
            allowCloudUpdate = true,
            shareWithClass = shareWithClass,
            createdAt = LocalDateTime.now()
        )
    }
}