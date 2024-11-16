package es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import kotlinx.coroutines.flow.first

class UpdateAssessmentsEnabledUseCase(
    private val profileRepository: ProfileRepository,
    private val examRepository: ExamRepository
) {
    suspend operator fun invoke(profile: ClassProfile, enabled: Boolean) {
        profileRepository.setAssessmentEnabled(profile, enabled)
        if (!enabled) examRepository
            .getExams(profile = profile)
            .first()
            .onEach { examRepository.deleteExamById(examId = it.id, profile = profile, onlyLocal = true) }
    }
}