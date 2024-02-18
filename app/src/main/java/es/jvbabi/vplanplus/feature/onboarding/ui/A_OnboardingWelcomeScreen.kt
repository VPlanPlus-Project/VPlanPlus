package es.jvbabi.vplanplus.feature.onboarding.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.feature.onboarding.ui.common.CloseOnboardingDialog
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen

@Composable
fun OnboardingWelcomeScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    
    LaunchedEffect(key1 = "A", block = {
        viewModel.reset()
    })

    LaunchedEffect(key1 = state.stage, block = {
        if (state.stage == Stage.SCHOOL_ID) navController.navigate(Screen.OnboardingSchoolIdScreen.route) {
            if (state.onboardingCause == OnboardingCause.NEW_PROFILE) popUpTo(Screen.SettingsProfileScreen.route) {
                inclusive = true
            }
        }
    })

    Welcome(
        onButtonClick = { viewModel.nextStageSchoolId() },
        showCloseDialog = state.showCloseDialog,
        hideCloseDialog = { viewModel.hideCloseDialog() },
        closeOnboarding = {
            if (state.onboardingCause == OnboardingCause.FIRST_START) {
                (context as Activity).finish()
            } else {
                navController.navigateUp()
            }
        }
    )

    BackHandler(enabled = !state.showCloseDialog) {
        viewModel.showCloseDialog()
    }
}

@Composable
fun Welcome(
    onButtonClick: () -> Unit,
    showCloseDialog: Boolean,
    closeOnboarding: () -> Unit,
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

    if (showCloseDialog) {
        CloseOnboardingDialog(
            onYes = { closeOnboarding() },
            onNo = { hideCloseDialog() }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingWelcomeScreenPreview() {
    Welcome(
        onButtonClick = {},
        showCloseDialog = true,
        closeOnboarding = {},
        hideCloseDialog = {}
    )
}