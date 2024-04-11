package es.jvbabi.vplanplus.ui.screens.home.search.room.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.InfoCard

@Composable
fun DisclaimerBanner(visible: Boolean, onClose: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(tween(300)),
        exit = shrinkVertically(tween(300))
    ) {
        InfoCard(
            imageVector = Icons.Default.Info,
            title = stringResource(id = R.string.disclaimer),
            text = stringResource(id = R.string.searchAvailableRoom_disclaimerText),
            buttonText1 = stringResource(id = R.string.ok),
            buttonAction1 = onClose
        )
    }
}

@Preview
@Composable
fun PreviewDisclaimerBanner() {
    DisclaimerBanner(visible = true, onClose = {})
}