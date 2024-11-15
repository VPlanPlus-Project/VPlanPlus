package es.jvbabi.vplanplus.domain.usecase.update

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.first

class MigrateHomeworkNotificationSettingsToDailyUseCase(
    private val profileRepository: ProfileRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
){
    suspend operator fun invoke() {
        val profile = getCurrentProfileUseCase().first() as? ClassProfile ?: return
        profileRepository.setDailyNotificationEnabled(profile, true)
    }
}