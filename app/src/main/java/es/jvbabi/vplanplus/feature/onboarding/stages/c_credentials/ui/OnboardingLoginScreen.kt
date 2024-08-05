package es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingLoginScreen(
    navController: NavHostController,
    viewModel: OnboardingLoginViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LoginScreen(
        state = state,
        doAction = viewModel::doAction,
        onProceed = {
            viewModel.doAction(OnProceed {
                navController.navigate(Screen.OnboardingNewProfileScreen())
            })
        },
        goBack = { navController.navigateUp() }
    )
}

@Composable
fun LoginScreen(
    state: OnboardingLoginState,
    doAction: (UiAction) -> Unit,
    onProceed: () -> Unit = {},
    goBack: () -> Unit = {}
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_credentialsTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_credentialsText)) },
        buttonText = stringResource(id = R.string.next),
        isLoading = state.isLoading,
        enabled = state.canProceed,
        onButtonClick = onProceed,
        content = {
            TextField(
                value = state.username,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                label = { Text(text = stringResource(id = R.string.username)) },
                onValueChange = { doAction(InputUsername(it)) },
                trailingIcon = {
                    IconButton(onClick = { if (state.username == "schueler") doAction(InputUsername("lehrer")) else doAction(InputUsername("schueler")) }) {
                        Icon(imageVector = Icons.Default.SwapHoriz, null) // TODO: Add content description
                    }
                }
            )

            TextField(
                value = state.password,
                onValueChange = { doAction(InputPassword(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                label = { Text(stringResource(id = R.string.password)) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, "")
                    }
                }
            )

            when (state.error) {
                LoginError.BAD_CREDENTIALS -> {
                    Text(
                        text = stringResource(id = R.string.onboarding_credentialsUnauthorized),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                LoginError.NETWORK_ERROR -> {
                    Text(
                        text = stringResource(id = R.string.noInternet),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                LoginError.UNSUPPORTED_SCHOOL -> {
                    Text(
                        text = stringResource(id = R.string.onboarding_credentialsUnsupportedSchool),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                null -> {}
            }

            HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

            val schoolIdAnnotatedString = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    append(stringResource(id = R.string.onboarding_credentialsLoginAt, state.sp24SchoolId))
                    append(" ")
                    append(DOT)
                    append(" ")
                }
                withLink(LinkAnnotation.Clickable(
                    linkInteractionListener = { goBack() },
                    tag = "schoolId"
                )) {
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(stringResource(id = R.string.onboarding_credentialsWrongSchoolIdLink))
                    }
                }
            }

            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = schoolIdAnnotatedString
            )
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun OnboardingLoginScreenPreview() {
    LoginScreen(
        state = OnboardingLoginState(
            sp24SchoolId = 10000000
        ),
        doAction = {},
    )
}