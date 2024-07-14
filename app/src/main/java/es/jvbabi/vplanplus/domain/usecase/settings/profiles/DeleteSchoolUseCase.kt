package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class DeleteSchoolUseCase(
    private val schoolRepository: SchoolRepository,
    private val profileRepository: ProfileRepository,
    private val keyValueRepository: KeyValueRepository,
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(
        schoolId: Int
    ) {
        val currentProfile = profileRepository.getProfileById(
            UUID.fromString(keyValueRepository.get(Keys.ACTIVE_PROFILE))
        ).first()!!
        if (currentProfile.getSchool().id == schoolId) {
            keyValueRepository.set(Keys.ACTIVE_PROFILE,
                profileRepository
                    .getProfiles()
                    .first()
                    .firstOrNull { it.getSchool().id != schoolId }
                    ?.id.toString()
            )
        }
        val profiles = profileRepository.getProfilesBySchool(schoolId).first()
        profiles.forEach { profile ->
            notificationRepository.deleteChannel("PROFILE_${profile.id.toString().lowercase()}")
        }
        schoolRepository.deleteSchool(schoolId)
    }
}