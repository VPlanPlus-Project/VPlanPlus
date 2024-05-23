package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase

data class BookRoomUseCases(
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val getRoomByNameUseCase: GetRoomByNameUseCase,
    val getLessonTimesUseCase: GetLessonTimesUseCase,
    val showRoomBookingDisclaimerBannerUseCase: IsShowRoomBookingDisclaimerBannerUseCase,
    val hideRoomBookingDisclaimerBannerUseCase: HideRoomBookingDisclaimerBannerUseCase,
    val bookRoomUseCase: BookRoomUseCase
)