package es.jvbabi.vplanplus.feature.settings.advanced.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.SelectDialog

@Composable
fun VppIdServerDialog(
    selectedServer: String,
    onSetServer: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val items = listOf(
        "https://vplan.plus",
        "https://vppid-development.test.jvbabi.es"
    )
    SelectDialog(
        painter = painterResource(id = R.drawable.database),
        title = stringResource(id = R.string.advancedSettings_dialogServerTitle),
        message = stringResource(id = R.string.advancedSettings_dialogServerMessage),
        items = items,
        value = selectedServer,
        onDismiss = onDismiss,
        onOk = onSetServer
    )
}

@Preview
@Composable
fun VppIdServerDialogPreview() {
    VppIdServerDialog(
        selectedServer = "https://vplan.plus",
        onSetServer = {},
        onDismiss = {}
    )
}