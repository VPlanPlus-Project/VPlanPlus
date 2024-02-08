package es.jvbabi.vplanplus.domain.usecase.find_room

import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import io.ktor.http.HttpStatusCode

class CancelBookingUseCase(
    private val vppIdRepository: VppIdRepository
) {
    suspend operator fun invoke(booking: RoomBooking): CancelBookingResult {
        return when (vppIdRepository.cancelRoomBooking(booking)) {
            null -> CancelBookingResult.NO_INTERNET
            HttpStatusCode.OK -> CancelBookingResult.SUCCESS
            HttpStatusCode.NotFound -> CancelBookingResult.BOOKING_NOT_FOUND
            else -> CancelBookingResult.ERROR
        }
    }
}

enum class CancelBookingResult {
    SUCCESS,
    ERROR,
    BOOKING_NOT_FOUND,
    NO_INTERNET
}