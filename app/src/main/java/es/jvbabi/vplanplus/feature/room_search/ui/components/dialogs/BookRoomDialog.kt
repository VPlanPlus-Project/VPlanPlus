package es.jvbabi.vplanplus.feature.room_search.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.ui.common.ComposableDialog
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun BookRoomDialog(
    room: Room,
    `class`: Classes,
    start: ZonedDateTime,
    end: ZonedDateTime,
    onCancelBooking: () -> Unit,
    onConfirmBooking: () -> Unit,
) {
    ComposableDialog(
        icon = Icons.Default.MeetingRoom,
        title = stringResource(
            id = R.string.searchAvailableRoom_bookTitle,
            room.name
        ),
        content = {
            Column {
                Text(
                    text = stringResource(
                        id = R.string.searchAvailableRoom_bookText,
                        start.toZonedLocalDateTime().format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        ),
                        end.toZonedLocalDateTime().format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        ),
                        `class`.name
                    )
                )
            }
        },
        onDismiss = onCancelBooking,
        onCancel = onCancelBooking,
        onOk = onConfirmBooking,
    )
}