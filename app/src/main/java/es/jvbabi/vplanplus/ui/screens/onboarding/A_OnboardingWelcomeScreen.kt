package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.CloseOnboardingDialog
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen

@Composable
fun OnboardingWelcomeScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value
    
    LaunchedEffect(key1 = "A", block = {
        viewModel.reset()
    })

    LaunchedEffect(key1 = state.stage, block = {
        if (state.stage == Stage.SCHOOL_ID) navController.navigate(Screen.OnboardingSchoolIdScreen.route)
    })

    Welcome(
        onButtonClick = { viewModel.nextStageSchoolId() },
        showCloseDialog = state.showCloseDialog,
        hideCloseDialog = { viewModel.hideCloseDialog() }
    )

    BackHandler(enabled = !state.showCloseDialog) {
        viewModel.showCloseDialog()
    }
}

@Composable
fun Welcome(
    onButtonClick: () -> Unit,
    showCloseDialog: Boolean,
    hideCloseDialog: () -> Unit
) {
    OnboardingScreen(
        title = stringResource(id = R.string.app_name),
        text = { Text(text = stringResource(id = R.string.onboarding_welcomeText)) },
        buttonText = stringResource(id = R.string.lets_go),
        isLoading = false,
        enabled = true,
        onButtonClick = { onButtonClick() },
        content = {},
        footer = {})

    if (showCloseDialog) CloseOnboardingDialog(onNo = { hideCloseDialog() })
}

@Preview(showBackground = true)
@Composable
private fun OnboardingWelcomeScreenPreview() {
    Welcome(
        onButtonClick = {},
        showCloseDialog = true,
        hideCloseDialog = {}
    )
}