package es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class HasProfileLocalAssessmentsUseCase(
    private val examRepository: ExamRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() = getCurrentProfileUseCase().flatMapLatest { profile ->
        if (profile !is ClassProfile) return@flatMapLatest flowOf(false)
        examRepository.getExams(profile = profile).map { assessments -> assessments.any { it is Exam.Local } }
    }
}