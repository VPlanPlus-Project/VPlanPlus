package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.profile.GetLessonTimesForClassUseCase

data class BookRoomUseCases(
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val getRoomByNameUseCase: GetRoomByNameUseCase,
    val getLessonTimesForClassUseCase: GetLessonTimesForClassUseCase,
    val showRoomBookingDisclaimerBannerUseCase: IsShowRoomBookingDisclaimerBannerUseCase,
    val hideRoomBookingDisclaimerBannerUseCase: HideRoomBookingDisclaimerBannerUseCase,
    val bookRoomUseCase: BookRoomUseCase
)