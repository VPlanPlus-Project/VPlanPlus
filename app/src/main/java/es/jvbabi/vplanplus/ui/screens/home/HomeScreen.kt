package es.jvbabi.vplanplus.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    LaunchedEffect("Init") {
        viewModel.init()
    }

    if (state.initDone && !state.activeProfileFound) {
        navHostController.navigate(Screen.OnboardingWelcomeScreen.route) {
            popUpTo(0)
        }
    } else {
        HomeScreenContent(state = state)
    }
}

@Composable
fun HomeScreenContent(
    state: HomeState,
) {
    if (!state.initDone) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreenContent(HomeState(initDone = true, activeProfileFound = true))
}