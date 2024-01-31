package es.jvbabi.vplanplus.ui.screens.home.search.room.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.ui.common.InfoDialog
import es.jvbabi.vplanplus.ui.preview.Classes
import es.jvbabi.vplanplus.ui.preview.Room
import es.jvbabi.vplanplus.ui.preview.School
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun BookingDetailDialog(
    booking: RoomBooking,
    onCloseBookingDetailDialog: () -> Unit
) {
    val from = DateTimeFormatter.ofPattern("HH:mm").format(booking.from)
    val to = DateTimeFormatter.ofPattern("HH:mm").format(booking.to.plusMinutes(1))
    InfoDialog(
        icon = Icons.Default.MeetingRoom,
        title = stringResource(id = R.string.searchAvailableRoom_bookingDetailTitle, booking.room.name),
        message = stringResource(id = R.string.searchAvailableRoom_bookingDetailText, booking.bookedBy?.name?: stringResource(
            id = R.string.unknownVppId
        ), booking.`class`.name, from, to),
        onOk = { onCloseBookingDetailDialog() }
    )
}

@Preview
@Composable
private fun BookingDetailDialogPreview() {
    val school = School.generateRandomSchools(1).first()
    BookingDetailDialog(
        booking = RoomBooking(
            bookedBy = null,
            `class` = Classes.generateClass(school),
            room = Room.generateRoom(school),
            from = LocalDateTime.now(),
            to = LocalDateTime.now().plusHours(1),
        ),
        onCloseBookingDetailDialog = {}
    )
}