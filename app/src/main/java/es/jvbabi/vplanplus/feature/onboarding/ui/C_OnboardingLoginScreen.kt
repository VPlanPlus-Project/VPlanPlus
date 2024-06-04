package es.jvbabi.vplanplus.feature.onboarding.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.feature.onboarding.ui.common.CloseOnboardingDialog
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import io.ktor.http.HttpStatusCode

@Composable
fun OnboardingLoginScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = state.stage, block = {
        if (state.stage == Stage.SCHOOL_ID) {
            viewModel.newScreen()
            navController.navigateUp()
        }
        if (state.stage == Stage.PROFILE_TYPE) {
            viewModel.newScreen()
            navController.navigate(Screen.OnboardingFirstProfileScreen.route)
        }
    })

    LoginScreen(
        state = state,
        onUsernameInput = { viewModel.onUsernameInput(it) },
        onPasswordInput = { viewModel.onPasswordInput(it) },
        onUsernameToggle = { viewModel.toggleUserName() },
        onPasswordVisibilityToggle = { viewModel.onPasswordVisibilityToggle() },
        onLogin = {
            viewModel.nextStageProfileType()
        },
        showCloseDialog = state.showCloseDialog,
        hideCloseDialog = { viewModel.hideCloseDialog() },
        goBack = {
            viewModel.goBackToSchoolId()
        },
        closeOnboarding = {
            if (state.onboardingCause == OnboardingCause.FIRST_START) {
                (context as Activity).finish()
            } else {
                navController.navigateUp()
            }
        }
    )

    BackHandler(enabled = true) {
        viewModel.showCloseDialog()
    }
}

@Composable
fun LoginScreen(
    state: OnboardingState,
    onUsernameInput: (String) -> Unit,
    onPasswordInput: (String) -> Unit,
    onUsernameToggle: () -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onLogin: () -> Unit,
    showCloseDialog: Boolean,
    closeOnboarding: () -> Unit,
    hideCloseDialog: () -> Unit,
    goBack: () -> Unit,
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_credentialsTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_credentialsText)) },
        buttonText = stringResource(id = R.string.next),
        isLoading = state.isLoading,
        enabled = state.username.isNotEmpty() && state.password.isNotEmpty() && !state.isLoading,
        onButtonClick = { onLogin() },
        content = {
            TextField(
                value = state.username,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                label = { Text(text = stringResource(id = R.string.username)) },
                onValueChange = { onUsernameInput(it) },
                trailingIcon = {
                    IconButton(onClick = { onUsernameToggle() }) {
                        Icon(imageVector = Icons.Default.SwapHoriz, "")
                    }
                }
            )

            TextField(
                value = state.password,
                onValueChange = { onPasswordInput(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                label = { Text(stringResource(id = R.string.password)) },
                singleLine = true,
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
                HttpStatusCode.OK -> {}
                HttpStatusCode.Forbidden -> {
                    Text(
                        text = stringResource(id = R.string.onboarding_credentialsUnauthorized),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                null -> {
                    Text(
                        text = stringResource(id = R.string.noInternet),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                else -> {
                    Text(
                        text = stringResource(id = R.string.unknownError),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

            val schoolIdAnnotatedString = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    append(stringResource(id = R.string.onboarding_credentialsLoginAt, state.schoolId))
                    append(" ")
                    append(DOT)
                    append(" ")
                }
                pushStringAnnotation("WRONG_SCHOOL_ID", "this") // Annotate the text
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(stringResource(id = R.string.onboarding_credentialsWrongSchoolIdLink))
                }
            }

            ClickableText(
                modifier = Modifier.padding(top = 12.dp),
                text = schoolIdAnnotatedString,
                onClick = { offset ->
                    schoolIdAnnotatedString.getStringAnnotations(
                        tag = "WRONG_SCHOOL_ID",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        goBack()
                    }
                }
            )
        }
    )

    if (showCloseDialog) {
        CloseOnboardingDialog(
            onYes = { closeOnboarding() },
            onNo = { hideCloseDialog() }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun OnboardingLoginScreenPreview() {
    LoginScreen(
        state = OnboardingState(
            schoolId = "10000000"
        ),
        onUsernameInput = {},
        onPasswordInput = {},
        onUsernameToggle = {},
        onPasswordVisibilityToggle = {},
        onLogin = {},
        showCloseDialog = false,
        hideCloseDialog = {},
        goBack = {},
        closeOnboarding = {}
    )
}