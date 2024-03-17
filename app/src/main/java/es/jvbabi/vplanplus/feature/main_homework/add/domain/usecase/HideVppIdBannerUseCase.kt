package es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class HideVppIdBannerUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.SHOW_VPPID_BANNER_HOMEWORK, "false")
    }
}