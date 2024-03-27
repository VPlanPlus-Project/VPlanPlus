package es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import java.time.LocalDate

class SetInfoExpandedUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(to: Boolean) {
        if (to) keyValueRepository.delete(Keys.INFO_CLOSED_FOR_DATE)
        else keyValueRepository.set(Keys.INFO_CLOSED_FOR_DATE, LocalDate.now().toString())
    }
}