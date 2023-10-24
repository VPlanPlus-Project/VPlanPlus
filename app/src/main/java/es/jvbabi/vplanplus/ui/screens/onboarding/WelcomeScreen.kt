package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen

@Composable
fun OnboardingWelcomeScreen(
    navController: NavHostController,
) {
    Welcome(
        onButtonClick = { navController.navigate(Screen.OnboardingSchoolIdScreen.route) })
}

@Composable
fun Welcome(
    onButtonClick: () -> Unit
) {
    OnboardingScreen(
        title = stringResource(id = R.string.app_name),
        text = stringResource(id = R.string.onboarding_welcomeText),
        buttonText = stringResource(id = R.string.lets_go),
        isLoading = false,
        enabled = true,
        onButtonClick = { onButtonClick() }) {
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingWelcomeScreenPreview() {
    Welcome {}
}