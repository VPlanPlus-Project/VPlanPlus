package es.jvbabi.vplanplus.feature.room_search.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoNotDisturbAlt
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.InfoDialog

@Composable
fun CannotBookRoomNotVerifiedDialog(
    onDismiss: () -> Unit = {}
) {
    InfoDialog(
        icon = Icons.Default.DoNotDisturbAlt,
        title = stringResource(id = R.string.searchAvailableRoom_cannotBookRoomNotVerifiedTitle),
        message = stringResource(id = R.string.searchAvailableRoom_cannotBookRoomNotVerifiedText),
        onOk = onDismiss
    )
}

@Preview
@Composable
private fun CannotBookRoomNotVerifiedPreview() {
    CannotBookRoomNotVerifiedDialog()
}

@Composable
fun CannotBookRoomWrongTypeDialog(
    onDismiss: () -> Unit = {}
) {
    InfoDialog(
        icon = Icons.Default.SwitchAccount,
        title = stringResource(id = R.string.searchAvailableRoom_cannotBookRoomWrongTypeTitle),
        message = stringResource(id = R.string.searchAvailableRoom_cannotBookRoomWrongTypeText),
        onOk = onDismiss
    )
}

@Preview
@Composable
private fun CannotBookRoomWrongTypePreview() {
    CannotBookRoomWrongTypeDialog()
}