package es.jvbabi.vplanplus.domain.usecase.find_room

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase

data class FindRoomUseCases(
    val getRoomMapUseCase: GetRoomMapUseCase,
    val canBookRoomUseCase: CanBookRoomUseCase,
    val bookRoomUseCase: BookRoomUseCase,
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val cancelBooking: CancelBookingUseCase
)