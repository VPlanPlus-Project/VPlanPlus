package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils.toLocalUnixTimestamp
import java.time.LocalDateTime

@Composable
fun OnboardingSetupScreen(
    navHostController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value
    val start by rememberSaveable {
        mutableStateOf(LocalDateTime.now())
    }

    if (!state.isLoading) {
        navHostController.navigate(Screen.HomeScreen.route)
    }

    SetupScreen(start, state.time)
}

@Composable
fun SetupScreen(start: LocalDateTime, now: LocalDateTime) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(id = R.string.onboarding_setupTakingLong),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                color = if (now.toLocalUnixTimestamp() - start.toLocalUnixTimestamp() >= 3) MaterialTheme.colorScheme.onSurface else Color.Transparent
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetupScreenPreview() {
    SetupScreen(
        LocalDateTime.now().minusSeconds(5),
        LocalDateTime.now()
    )
}