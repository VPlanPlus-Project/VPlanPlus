package es.jvbabi.vplanplus.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    if (!state.hasSchool) {
        navHostController.navigate(Screen.OnboardingWelcomeScreen.route) {
            popUpTo(0)
        }
    }
}