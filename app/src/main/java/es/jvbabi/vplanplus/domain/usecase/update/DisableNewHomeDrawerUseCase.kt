package es.jvbabi.vplanplus.domain.usecase.update

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class DisableNewHomeDrawerUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.SHOW_NEW_HOME_DRAWER, "false")
    }
}