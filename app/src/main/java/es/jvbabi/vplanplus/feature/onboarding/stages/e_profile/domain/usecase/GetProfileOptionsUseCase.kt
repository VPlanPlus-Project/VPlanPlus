package es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.domain.usecase

import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingInitClass
import kotlinx.serialization.json.Json

class GetProfileOptionsUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(): ProfileOptions {
        val profileType = ProfileType.entries[keyValueRepository.get("onboarding.profile_type")!!.toInt()]
        return ProfileOptions(profileType, when (profileType) {
            ProfileType.STUDENT -> {
                val options = Json.decodeFromString<List<OnboardingInitClass>>(keyValueRepository.get("onboarding.classes")!!).sortedBy { it.name.first().isDigit().not().toString() + it.name.takeWhile { n -> n.isDigit() }.padStart(10, '0') + it.name.dropWhile { n -> n.isDigit() }}
                options.associate { it.name to it.users }
            }
            ProfileType.TEACHER -> {
                val options =  Json.decodeFromString<List<String>>(keyValueRepository.get("onboarding.teachers")!!).sortedBy { it }
                options.associateWith { 0 }
            }
            ProfileType.ROOM -> {
                val options =  Json.decodeFromString<List<String>>(keyValueRepository.get("onboarding.rooms")!!).sortedBy { it }
                options.associateWith { 0 }
            }
        })
    }
}

data class ProfileOptions(
    val profileType: ProfileType,
    val options: Map<String, Int>
)