package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.ui

import android.Manifest
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingSchoolIdScreen(
    navController: NavHostController,
    viewModel: OnboardingSchoolIdViewModel = hiltViewModel()
) {
    val state = viewModel.state

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { if (it) navController.navigate(Screen.OnboardingQrScreen.route) }
    )

    SchoolId(
        onAction = viewModel::doAction,
        state = state,
        onQrCodeClick = { launcher.launch(Manifest.permission.CAMERA) },
        onProceed = { if (state.canProceed) viewModel.doAction(OnProceed {navController.navigate(Screen.OnboardingLoginScreen.route)})  }
    )
}

@Composable
fun SchoolId(
    onAction: (UiAction) -> Unit,
    onProceed: () -> Unit,
    onQrCodeClick: () -> Unit,
    state: OnboardingSchoolIdState
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_schoolIdTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_schoolIdText)) },
        buttonText = stringResource(id = R.string.next),
        isLoading = state.isLoading,
        enabled = state.canProceed,
        onButtonClick = { onProceed() },
        content = {
            Column {
                TextField(
                    value = state.sp24SchoolId,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onAction(InputSchoolId(it.take(8))) },
                    label = { Text(text = stringResource(id = R.string.onboarding_schoolIdHint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                when (state.schoolIdError) {
                    SchoolIdError.DOES_NOT_EXIST -> {
                        Text(
                            text = stringResource(id = R.string.onboarding_schoolIdNotFound),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    SchoolIdError.NETWORK_ERROR -> {
                        Text(
                            text = stringResource(id = R.string.noInternet),
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
}

@Composable
@Preview(showBackground = true)
private fun SchoolIdScreenPreview() {
    SchoolId(
        onAction = {},
        state = OnboardingSchoolIdState(),
        onQrCodeClick = {},
        onProceed = {}
    )
}