package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase


data class RoomSearchUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val getRoomMapUseCase: GetRoomMapUseCase,
    val getLessonTimesUseCases: GetClassLessonTimesUseCase,
    val canBookRoomUseCase: CanBookRoomUseCase,
    val bookRoomUseCase: BookRoomUseCase,
    val cancelBookingUseCase: CancelBookingUseCase
)
