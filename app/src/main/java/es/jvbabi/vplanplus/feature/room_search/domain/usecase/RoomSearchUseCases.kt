package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase


data class RoomSearchUseCases(
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val getRoomMapUseCase: GetRoomMapUseCase,
    val getLessonTimesUseCases: GetClassLessonTimesUseCase
)
