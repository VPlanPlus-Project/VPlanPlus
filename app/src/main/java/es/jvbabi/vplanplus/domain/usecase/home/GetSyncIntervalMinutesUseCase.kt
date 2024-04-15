package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class GetSyncIntervalMinutesUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() =
        keyValueRepository
            .getOrDefault(Keys.SETTINGS_SYNC_INTERVAL, Keys.SETTINGS_SYNC_INTERVAL_DEFAULT.toString())
            .toLong()
}