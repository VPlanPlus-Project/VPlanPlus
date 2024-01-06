package es.jvbabi.vplanplus.ui.screens.onboarding.common

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.YesNoDialog

@Composable
fun CloseOnboardingDialog(
    onNo: () -> Unit = {},
) {
    val context = LocalContext.current
    YesNoDialog(
        icon = Icons.AutoMirrored.Default.Logout,
        title = stringResource(id = R.string.onboarding_cancelOnboardingTitle),
        message = stringResource(id = R.string.onboarding_cancelOnboardingText),
        onYes = { context as Activity; context.finish() },
        onNo = onNo,
    )
}

@Preview
@Composable
private fun CloseOnboardingDialogPreview() {
    CloseOnboardingDialog()
}