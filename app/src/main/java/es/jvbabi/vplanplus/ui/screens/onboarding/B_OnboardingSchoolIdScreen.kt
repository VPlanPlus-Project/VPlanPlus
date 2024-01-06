package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import es.jvbabi.vplanplus.ui.common.InfoDialog
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.CloseOnboardingDialog
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen

@Composable
fun OnboardingSchoolIdScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value

    LaunchedEffect(key1 = state.stage, block = {
        if (state.stage == Stage.CREDENTIALS) {
            viewModel.newScreen()
            navController.navigate(Screen.OnboardingLoginScreen.route)
        }
        if (state.stage == Stage.PROFILE_TYPE) {
            viewModel.newScreen()
            navController.navigate(Screen.OnboardingFirstProfileScreen.route)
        }
    })

    SchoolId(
        onSchoolIdInputChange = { viewModel.onSchoolIdInput(it) },
        onButtonClick = {
            viewModel.nextStageCredentials()
        },
        state = state,
        onTestSchoolErrorDialogDismissed = { viewModel.onTestSchoolErrorDialogDismissed() },
        onTestSchoolClick = {
            viewModel.useTestSchool()
        },
        showCloseDialog = state.showCloseDialog,
        hideCloseDialog = { viewModel.hideCloseDialog() }
    )

    BackHandler(enabled = !state.showCloseDialog) {
        viewModel.showCloseDialog()
    }
}

@Composable
fun SchoolId(
    onSchoolIdInputChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    onTestSchoolClick: () -> Unit,
    onTestSchoolErrorDialogDismissed: () -> Unit,
    showCloseDialog: Boolean,
    hideCloseDialog: () -> Unit,
    state: OnboardingState
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_schoolIdTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_schoolIdText)) },
        buttonText = stringResource(id = R.string.next),
        isLoading = state.isLoading,
        enabled = !state.isLoading && (state.schoolIdState == SchoolIdCheckResult.SYNTACTICALLY_CORRECT || state.schoolIdState == null),
        onButtonClick = { onButtonClick() },
        content = {
            Column {
                TextField(
                    value = state.schoolId,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onSchoolIdInputChange(it.take(8)) },
                    label = { Text(text = stringResource(id = R.string.onboarding_schoolIdHint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                when (state.currentResponseType) {
                    Response.NOT_FOUND -> {
                        Text(
                            text = stringResource(id = R.string.onboarding_schoolIdNotFound),
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
        },
        footer = {
            TextButton(
                onClick = onTestSchoolClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && !state.testSchoolLoading
            ) {
                if (state.testSchoolLoading) CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = Color.Gray,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .padding(6.dp)
                ) else Text(text = stringResource(id = R.string.onboarding_welcomeContinueTestSchool))
            }
        }
    )

    if (state.testSchoolError) {
        InfoDialog(
            icon = Icons.Default.Error,
            title = stringResource(id = R.string.onboarding_welcomeTestSchoolErrorTitle),
            message = stringResource(id = R.string.onboarding_welcomeTestSchoolErrorText),
            onOk = { onTestSchoolErrorDialogDismissed() }
        )
    }
    if (showCloseDialog) CloseOnboardingDialog(onNo = { hideCloseDialog() })
}

@Composable
@Preview(showBackground = true)
private fun SchoolIdScreenPreview() {
    SchoolId(
        onSchoolIdInputChange = {},
        onButtonClick = {},
        onTestSchoolClick = {},
        onTestSchoolErrorDialogDismissed = {},
        showCloseDialog = false,
        hideCloseDialog = {},
        state = OnboardingState()
    )
}