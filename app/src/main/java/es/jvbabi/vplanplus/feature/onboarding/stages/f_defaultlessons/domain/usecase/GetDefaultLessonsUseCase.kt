package es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingDefaultLesson
import kotlinx.serialization.json.Json

class GetDefaultLessonsUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(): List<OnboardingDefaultLesson> {
        val className = keyValueRepository.get("onboarding.profile")!!
        val defaultLessons = Json.decodeFromString<List<OnboardingDefaultLesson>>(keyValueRepository.get("onboarding.default_lessons")!!)
        return defaultLessons.filter { it.clazz == className }
    }
}