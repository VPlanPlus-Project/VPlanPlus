package es.jvbabi.vplanplus.domain.usecase.sync

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first

class UpdateFirebaseTokenUseCase(
    private val profileRepository: ProfileRepository,
    private val firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository
) {
    suspend operator fun invoke(newToken: String): Boolean {
        firebaseCloudMessagingManagerRepository.resetToken(newToken)
        val vppIds = profileRepository
            .getProfiles().first()
            .filterIsInstance<ClassProfile>()
            .mapNotNull { it.vppId }
            .distinctBy { it.id }

        val groups = profileRepository
            .getProfiles().first()
            .filterIsInstance<ClassProfile>()
            .map { it.group }
            .distinctBy { it.groupId }

        if (groups.any { group ->
                !firebaseCloudMessagingManagerRepository.addTokenGroup(group, newToken) }
        ) return false

        return vppIds.all { vppId ->
            firebaseCloudMessagingManagerRepository.addTokenUser(vppId, newToken)
        }
    }
}