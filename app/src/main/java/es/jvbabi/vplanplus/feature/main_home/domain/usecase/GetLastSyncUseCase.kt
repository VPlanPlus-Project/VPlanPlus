package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.map

class GetLastSyncUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() =
        keyValueRepository
            .getFlow(Keys.LAST_SYNC_TS)
            .map { ZonedDateTimeConverter().timestampToZonedDateTime(it?.toLongOrNull() ?: 0) }
}