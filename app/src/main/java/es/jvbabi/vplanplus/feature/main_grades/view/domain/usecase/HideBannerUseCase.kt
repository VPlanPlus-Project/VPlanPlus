package es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class HideBannerUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.SHOW_BS_BANNER, false.toString())
    }
}