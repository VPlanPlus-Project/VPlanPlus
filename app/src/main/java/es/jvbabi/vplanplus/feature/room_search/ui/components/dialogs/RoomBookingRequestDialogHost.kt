package es.jvbabi.vplanplus.feature.room_search.ui.components.dialogs

import androidx.compose.runtime.Composable
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomAbility
import es.jvbabi.vplanplus.feature.room_search.ui.NewRoomBookingRequest

@Composable
fun RoomBookingRequestDialogHost(
    bookingAbility: BookRoomAbility,
    group: Group? = null,
    bookingRequest: NewRoomBookingRequest,
    onConfirmBooking: () -> Unit,
    onCancelBooking: () -> Unit,
) {
    when (bookingAbility) {
        BookRoomAbility.NO_VPP_ID -> CannotBookRoomNotVerifiedDialog(onCancelBooking)
        BookRoomAbility.WRONG_TYPE -> CannotBookRoomWrongTypeDialog(onCancelBooking)
        BookRoomAbility.CAN_BOOK -> BookRoomDialog(bookingRequest.room, group!!, bookingRequest.start, bookingRequest.end, onCancelBooking, onConfirmBooking)
    }
}