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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import es.jvbabi.vplanplus.R
import kotlinx.coroutines.launch

@Composable
fun OnboardingFirstProfileScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.onboarding_firstProfileTitle),
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = stringResource(id = R.string.onboarding_firstProfileText),
                )

            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.onLogin()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = state.username.isNotEmpty() && state.password.isNotEmpty() && !state.isLoading
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = stringResource(id = R.string.next))
                }
            }
        }
    }
}

@Preview
@Composable
fun OnboardingFirstProfileScreenPreview() {
    OnboardingFirstProfileScreen(rememberNavController(), viewModel())
}