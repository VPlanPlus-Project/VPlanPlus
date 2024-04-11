package es.jvbabi.vplanplus.domain.usecase.find_room

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class HideRoomBookingDisclaimerBannerUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() = keyValueRepository.set(Keys.SHOW_ROOM_BOOKING_DISCLAIMER_BANNER, "false")
}