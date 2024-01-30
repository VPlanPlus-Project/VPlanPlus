package es.jvbabi.vplanplus.domain.usecase.find_room

data class FindRoomUseCases(
    val getRoomMapUseCase: GetRoomMapUseCase,
    val canBookRoomUseCase: CanBookRoomUseCase,
    val bookRoomUseCase: BookRoomUseCase
)