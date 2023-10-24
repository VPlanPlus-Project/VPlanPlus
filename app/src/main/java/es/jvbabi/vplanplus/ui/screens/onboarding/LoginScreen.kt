package es.jvbabi.vplanplus.ui.screens.onboarding

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen
import kotlinx.coroutines.launch

@Composable
fun OnboardingLoginScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value
    val coroutineScope = rememberCoroutineScope()

    if (state.loginSuccessful) {
        viewModel.newScreen()
        Log.d("OnboardingLoginScreen", "Login successful")
        navController.navigate(Screen.OnboardingFirstProfileScreen.route) {
            popUpTo(0)
        }
    }

    LoginScreen(
        state = state,
        onUsernameInput = { viewModel.onUsernameInput(it) },
        onPasswordInput = { viewModel.onPasswordInput(it) },
        onPasswordVisibilityToggle = { viewModel.onPasswordVisibilityToggle() },
        onLogin = {
            coroutineScope.launch {
                viewModel.onLogin()
            }
        }
    )
}

@Composable
fun LoginScreen(
    state: OnboardingState,
    onUsernameInput: (String) -> Unit,
    onPasswordInput: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onLogin: () -> Unit,
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_credentialsTitle),
        text = stringResource(id = R.string.onboarding_credentialsText),
        buttonText = stringResource(id = R.string.next),
        isLoading = state.isLoading,
        enabled = state.username.isNotEmpty() && state.password.isNotEmpty() && !state.isLoading,
        onButtonClick = { onLogin() }) {
        TextField(
            value = state.username,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            label = { Text(text = stringResource(id = R.string.username)) },
            onValueChange = { onUsernameInput(it) },
        )

        TextField(
            value = state.password,
            onValueChange = { onPasswordInput(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            label = { Text("Password") },
            singleLine = true,
            placeholder = { Text("Password") },
            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (state.passwordVisible)
                    Icons.Default.Visibility
                else Icons.Default.VisibilityOff

                IconButton(onClick = { onPasswordVisibilityToggle() }) {
                    Icon(imageVector = image, "")
                }
            }
        )

        when (state.currentResponseType) {
            Response.WRONG_CREDENTIALS -> {
                Text(
                    text = stringResource(id = R.string.onboarding_credentialsUnauthorized),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Response.NO_INTERNET -> {
                Text(
                    text = stringResource(id = R.string.noInternet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Response.OTHER -> {
                Text(
                    text = stringResource(id = R.string.unknownError),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            else -> {}
        }
    }

}

@Composable
@Preview(showBackground = true)
fun OnboardingLoginScreenPreview() {
    LoginScreen(
        state = OnboardingState(),
        onUsernameInput = {},
        onPasswordInput = {},
        onPasswordVisibilityToggle = {},
        onLogin = {}
    )
}