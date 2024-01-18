package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.onboarding.ProfileCreationStage
import es.jvbabi.vplanplus.domain.usecase.onboarding.ProfileCreationStatus
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingSetupScreen(
    navHostController: NavHostController, viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value

    if (!state.isLoading) {
        navHostController.navigate(Screen.HomeScreen.route)
    }

    SetupScreen(state.creationStatus)
}

@Composable
fun SetupScreen(creationStatus: ProfileCreationStatus) {
    if (creationStatus.progress != null) LinearProgressIndicator(progress = { creationStatus.progress.toFloat() })
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = when (creationStatus.profileCreationStage) {
                    ProfileCreationStage.INSERT_CLASSES -> stringResource(R.string.onboarding_setupInsertClasses)
                    ProfileCreationStage.INSERT_TEACHERS -> stringResource(R.string.onboarding_setupInsertTeachers)
                    ProfileCreationStage.INSERT_ROOMS -> stringResource(R.string.onboarding_setupInsertRooms)
                    ProfileCreationStage.INSERT_HOLIDAYS -> stringResource(R.string.onboarding_setupInsertHolidays)
                    ProfileCreationStage.INITIAL_SYNC -> stringResource(R.string.onboarding_setupInitialSync)
                    else -> ""
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SetupScreenPreview() {
    SetupScreen(
        ProfileCreationStatus(
            ProfileCreationStage.INSERT_CLASSES,
            0.5
        )
    )
}