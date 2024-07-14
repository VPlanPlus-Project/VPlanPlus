package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class CancelBookingUseCase(
    private val vppIdRepository: VppIdRepository
) {
    suspend operator fun invoke(booking: RoomBooking): CancelBookingResult {
        return when (vppIdRepository.cancelRoomBooking(booking)) {
            null -> CancelBookingResult.NO_INTERNET
            true -> CancelBookingResult.SUCCESS
            false -> CancelBookingResult.ERROR
        }
    }
}

enum class CancelBookingResult {
    SUCCESS,
    ERROR,
    NO_INTERNET
}