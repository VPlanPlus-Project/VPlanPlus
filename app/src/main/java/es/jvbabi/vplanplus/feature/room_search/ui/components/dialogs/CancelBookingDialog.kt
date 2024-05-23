package es.jvbabi.vplanplus.feature.room_search.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.YesNoDialog

@Composable
fun CancelBookingDialog(
    onYes: () -> Unit,
    onNo: () -> Unit,
) {
    YesNoDialog(
        icon = Icons.Default.BookmarkRemove,
        title = stringResource(id = R.string.searchAvailableRoom_cancelBookingDialogTitle),
        message = stringResource(id = R.string.searchAvailableRoom_cancelBookingDialogText),
        onYes = onYes,
        onNo = onNo,
    )
}

@Composable
@Preview
private fun CancelBookingDialogPreview() {
    CancelBookingDialog(
        onYes = {},
        onNo = {},
    )
}