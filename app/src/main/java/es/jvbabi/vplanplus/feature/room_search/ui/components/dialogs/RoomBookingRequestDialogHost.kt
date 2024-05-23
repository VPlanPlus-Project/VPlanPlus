package es.jvbabi.vplanplus.feature.room_search.ui.components.dialogs

import androidx.compose.runtime.Composable
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomAbility
import es.jvbabi.vplanplus.feature.room_search.ui.NewRoomBookingRequest

@Composable
fun RoomBookingRequestDialogHost(
    bookingAbility: BookRoomAbility,
    classes: Classes? = null,
    bookingRequest: NewRoomBookingRequest,
    onConfirmBooking: () -> Unit,
    onCancelBooking: () -> Unit,
) {
    when (bookingAbility) {
        BookRoomAbility.NO_VPP_ID -> CannotBookRoomNotVerifiedDialog(onCancelBooking)
        BookRoomAbility.WRONG_TYPE -> CannotBookRoomWrongTypeDialog(onCancelBooking)
        BookRoomAbility.CAN_BOOK -> BookRoomDialog(bookingRequest.room, classes!!, bookingRequest.start, bookingRequest.end, onCancelBooking, onConfirmBooking)
    }
}