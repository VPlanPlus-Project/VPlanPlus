package es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingDefaultLesson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SetDefaultLessonsUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(defaultLessons: Map<OnboardingDefaultLesson, Boolean>) {
        val json = Json { allowStructuredMapKeys = true }
        keyValueRepository.set("onboarding.profile_default_lessons", json.encodeToString(defaultLessons))
    }
}