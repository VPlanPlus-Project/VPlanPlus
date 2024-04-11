package es.jvbabi.vplanplus.ui.screens.home.search.room.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.ui.common.ComposableDialog
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.Room
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Dialog to show the details of a booking
 * @param booking the booking to show
 * @param userIsAuthor if the user is the author of the booking
 * @param onCloseBookingDetailDialog callback to close the dialog
 */
@Composable
fun BookingDetailDialog(
    booking: RoomBooking,
    userIsAuthor: Boolean,
    onCloseBookingDetailDialog: () -> Unit,
    onCancelBooking: () -> Unit,
) {
    val from = DateTimeFormatter.ofPattern("HH:mm").format(booking.from.toZonedLocalDateTime())
    val to = DateTimeFormatter.ofPattern("HH:mm").format(booking.to.toZonedLocalDateTime())
    ComposableDialog(
        icon = Icons.Default.MeetingRoom,
        title = stringResource(
            id = R.string.searchAvailableRoom_bookingDetailTitle,
            booking.room.name
        ),
        content = {
            Column {
                Text(
                    text = stringResource(
                        id = R.string.searchAvailableRoom_bookingDetailText,
                        booking.bookedBy?.name ?: stringResource(
                            id = R.string.unknownVppId
                        ),
                        booking.`class`.name,
                        from,
                        to
                    )
                )
                if (userIsAuthor) {
                    AssistChip(
                        onClick = { onCancelBooking() },
                        label = {
                            Text(
                                text = stringResource(
                                    id = R.string.searchAvailableRoom_bookingDetailCancel
                                )
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = null)
                        }
                    )
                }
            }
        },
        onOk = { onCloseBookingDetailDialog() },
    )
}

@Preview
@Composable
private fun BookingDetailDialogPreview() {
    val school = School.generateRandomSchools(1).first()
    BookingDetailDialog(
        booking = RoomBooking(
            id = 0,
            bookedBy = null,
            `class` = ClassesPreview.generateClass(school),
            room = Room.generateRoom(school),
            from = ZonedDateTime.now(),
            to = ZonedDateTime.now().plusHours(1),
        ),
        true,
        onCloseBookingDetailDialog = {},
        onCancelBooking = {},
    )
}