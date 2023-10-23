package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.ui.screens.Screen
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
        navController.navigate(Screen.HomeScreen.route) {
            popUpTo(0)
        }
    }
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
                    text = stringResource(id = R.string.onboarding_credentialsTitle),
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = stringResource(id = R.string.onboarding_credentialsText)
                )
                TextField(
                    value = state.username,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    label = { Text(text = stringResource(id = R.string.username)) },
                    onValueChange = { viewModel.onUsernameInput(it) }
                )

                TextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordInput(it) },
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

                        IconButton(onClick = { viewModel.onPasswordVisibilityToggle() }) {
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
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                                .padding(6.dp)
                        )
                    }
                    else Text(text = stringResource(id = R.string.next))
                }
            }
        }
    }
}

@Composable
@Preview
fun OnboardingLoginScreenPreview() {
    OnboardingLoginScreen(rememberNavController(), hiltViewModel())
}