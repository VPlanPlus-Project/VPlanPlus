package es.jvbabi.vplanplus.domain.usecase.find_room

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.HideRoomBookingDisclaimerBannerUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.IsShowRoomBookingDisclaimerBannerUseCase

data class FindRoomUseCases(
    val getRoomMapUseCase: GetRoomMapUseCase,
    val canBookRoomUseCase: CanBookRoomUseCase,
    val bookRoomUseCase: BookRoomUseCase,
    val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    val cancelBooking: CancelBookingUseCase,

    val isShowRoomBookingDisclaimerBannerUseCase: IsShowRoomBookingDisclaimerBannerUseCase,
    val hideRoomBookingDisclaimerBannerUseCase: HideRoomBookingDisclaimerBannerUseCase
)