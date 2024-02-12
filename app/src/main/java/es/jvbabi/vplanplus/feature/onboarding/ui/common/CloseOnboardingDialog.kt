package es.jvbabi.vplanplus.feature.onboarding.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.YesNoDialog

@Composable
fun CloseOnboardingDialog(
    onYes: () -> Unit,
    onNo: () -> Unit,
) {
    YesNoDialog(
        icon = Icons.AutoMirrored.Default.Logout,
        title = stringResource(id = R.string.onboarding_cancelOnboardingTitle),
        message = stringResource(id = R.string.onboarding_cancelOnboardingText),
        onYes = { onYes() },
        onNo = { onNo() }
    )
}

@Preview
@Composable
private fun CloseOnboardingDialogPreview() {
    CloseOnboardingDialog({}, {})
}