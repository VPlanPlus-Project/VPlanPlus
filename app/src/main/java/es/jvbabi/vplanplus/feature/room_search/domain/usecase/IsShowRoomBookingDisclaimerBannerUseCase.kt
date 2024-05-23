package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.map

class IsShowRoomBookingDisclaimerBannerUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = keyValueRepository.getFlowOrDefault(Keys.SHOW_ROOM_BOOKING_DISCLAIMER_BANNER, "true").map { it.toBoolean() }
}