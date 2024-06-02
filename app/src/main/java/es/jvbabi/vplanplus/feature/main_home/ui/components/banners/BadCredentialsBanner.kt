package es.jvbabi.vplanplus.feature.main_home.ui.components.banners

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.InfoCard

@Composable
@Preview
fun BadCredentialsBanner(
    modifier: Modifier = Modifier,
    onFixCredentialsClicked: () -> Unit = {},
    expand: Boolean = true
) {
    AnimatedVisibility(
        visible = expand,
        modifier = modifier,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        InfoCard(
            imageVector = Icons.Default.Key,
            title = stringResource(id = R.string.home_invalidCredentialsTitle),
            text = stringResource(id = R.string.home_invalidCredentialsText),
            backgroundColor = MaterialTheme.colorScheme.errorContainer,
            textColor = MaterialTheme.colorScheme.onErrorContainer,
            buttonText1 = stringResource(id = R.string.home_invalidCredentialsUpdate),
            buttonAction1 = onFixCredentialsClicked
        )
    }
}