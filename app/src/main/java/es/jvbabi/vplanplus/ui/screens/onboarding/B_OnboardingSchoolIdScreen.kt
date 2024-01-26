package es.jvbabi.vplanplus.ui.screens.onboarding

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.CloseOnboardingDialog
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen

@Composable
fun OnboardingSchoolIdScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) viewModel.showQr()
        })

    LaunchedEffect(key1 = state.showQr, block = {
        if (state.showQr) navController.navigate(Screen.OnboardingQrScreen.route)
    })

    LaunchedEffect(key1 = state.stage, block = {
        if (state.stage == Stage.CREDENTIALS) {
            viewModel.newScreen()
            navController.navigate(Screen.OnboardingLoginScreen.route) {
                if (state.onboardingCause == OnboardingCause.NEW_PROFILE) popUpTo(Screen.SettingsProfileScreen.route) {
                    inclusive = true
                }
            }
        }
        if (state.stage == Stage.PROFILE_TYPE) {
            viewModel.newScreen()
            navController.navigate(Screen.OnboardingFirstProfileScreen.route) {
                if (state.onboardingCause == OnboardingCause.NEW_PROFILE) popUpTo(Screen.SettingsProfileScreen.route) {
                    inclusive = true
                }
            }
        }
    })

    SchoolId(
        onSchoolIdInputChange = { viewModel.onSchoolIdInput(it) },
        onButtonClick = {
            viewModel.nextStageCredentials()
        },
        state = state,
        showCloseDialog = state.showCloseDialog,
        hideCloseDialog = { viewModel.hideCloseDialog() },
        closeOnboarding = {
            if (state.onboardingCause == OnboardingCause.FIRST_START) {
                (context as Activity).finish()
            } else {
                navController.navigateUp()
            }
        },
        onQrCodeClick = {
            launcher.launch(android.Manifest.permission.CAMERA)
        }
    )

    BackHandler(enabled = !state.showCloseDialog) {
        viewModel.showCloseDialog()
    }
}

@Composable
fun SchoolId(
    onSchoolIdInputChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    onQrCodeClick: () -> Unit,
    showCloseDialog: Boolean,
    closeOnboarding: () -> Unit,
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
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    onClick = { onQrCodeClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !state.isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        modifier = Modifier
                            .height(20.dp)
                            .padding(end = 8.dp)
                    )
                    Text(text = stringResource(id = R.string.onboarding_welcomeScanQrCode))
                }
            }
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
private fun SchoolIdScreenPreview() {
    SchoolId(
        onSchoolIdInputChange = {},
        onButtonClick = {},
        showCloseDialog = false,
        hideCloseDialog = {},
        closeOnboarding = {},
        state = OnboardingState(),
        onQrCodeClick = {}
    )
}