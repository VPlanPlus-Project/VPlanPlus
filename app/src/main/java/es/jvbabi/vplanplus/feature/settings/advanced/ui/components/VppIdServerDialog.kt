package es.jvbabi.vplanplus.feature.settings.advanced.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.SelectDialog

/**
 * The first server in the list is the default server.
 */
val servers = listOf(
    VppIdServer(apiHost = "https://vplan.plus", authHost = "https://auth.vplan.plus"),
    VppIdServer(apiHost = "https://development.vplan.plus", authHost = "https://development.auth.vplan.plus"),
    VppIdServer(apiHost = "https://vpp.magnix.cluster.jvbabi.es", authHost = "https://vppauth.magnix.cluster.jvbabi.es"),
    VppIdServer(apiHost = "https://vpp-api.localhost.cluster.jvbabi.es", "https://vpp-ui.localhost.cluster.jvbabi.es"),
    VppIdServer(apiHost = "https://julius.familie-babies.de")
)

@Composable
fun VppIdServerDialog(
    selectedServer: VppIdServer,
    onSetServer: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    SelectDialog(
        painter = painterResource(id = R.drawable.database),
        title = stringResource(id = R.string.advancedSettings_dialogServerTitle),
        message = stringResource(id = R.string.advancedSettings_dialogServerMessage),
        items = servers,
        itemToComposable = {
            Text(text = it.apiHost, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = it.uiHost, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        value = selectedServer,
        onDismiss = onDismiss,
        onOk = { onSetServer(it?.apiHost) }
    )
}

data class VppIdServer(
    val apiHost: String,
    val uiHost: String = apiHost,
    val authHost: String = apiHost,
) : Comparable<VppIdServer> {
    override fun compareTo(other: VppIdServer): Int {
        return apiHost.compareTo(other.apiHost)
    }
}

@Preview
@Composable
fun VppIdServerDialogPreview() {
    VppIdServerDialog(
        selectedServer = servers.first(),
        onSetServer = {},
        onDismiss = {}
    )
}