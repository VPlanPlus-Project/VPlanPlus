package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase

data class BookRoomUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val getRoomByNameUseCase: GetRoomByNameUseCase,
    val getLessonTimesUseCase: GetLessonTimesUseCase,
    val showRoomBookingDisclaimerBannerUseCase: IsShowRoomBookingDisclaimerBannerUseCase,
    val hideRoomBookingDisclaimerBannerUseCase: HideRoomBookingDisclaimerBannerUseCase,
    val bookRoomUseCase: BookRoomUseCase
)