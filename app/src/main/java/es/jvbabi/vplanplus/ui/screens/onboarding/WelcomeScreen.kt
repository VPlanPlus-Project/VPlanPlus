package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingWelcomeScreen(
    navController: NavHostController,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(text = stringResource(id = R.string.onboarding_welcomeText))
            }
            Button(onClick = { navController.navigate(Screen.OnboardingSchoolIdScreen.route) }, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.lets_go))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingWelcomeScreenPreview() {
    OnboardingWelcomeScreen(navController = rememberNavController())
}