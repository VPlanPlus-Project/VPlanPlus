package es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase

import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository

class GetPreferredSchoolDownloadModeUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(): SchoolDownloadMode {
        return keyValueRepository.get("onboarding.download_mode")?.let {
            SchoolDownloadMode.valueOf(it)
        } ?: SchoolDownloadMode.INDIWARE_WOCHENPLAN_6
    }
}