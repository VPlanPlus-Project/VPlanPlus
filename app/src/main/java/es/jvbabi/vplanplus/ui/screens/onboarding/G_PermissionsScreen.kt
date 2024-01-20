package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.Permission
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen

@Composable
fun OnboardingPermissionScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel,
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = state.stage, block = {
        if (state.stage == Stage.FINISH) navController.navigate(Screen.OnboardingSetupScreen.route)
    })

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) viewModel.nextPermission(context)
        })

    PermissionScreenContent(
        state = state,
        onAskForPermission = { launcher.launch(Permission.permissions[state.currentPermissionIndex].type) },
        onSkipPermission = { viewModel.nextPermission(context) },
    )

    BackHandler {
        viewModel.showCloseDialog()
    }
}

@Composable
fun PermissionScreenContent(
    state: OnboardingState,
    onAskForPermission: () -> Unit = {},
    onSkipPermission: () -> Unit = {},
) {
    OnboardingScreen(
        title = stringResource(
            id = R.string.onboarding_permissionTitle,
            state.currentPermissionIndex + 1,
            Permission.permissions.size
        ),
        text = {},
        enabled = true,
        isLoading = false,
        onButtonClick = { onAskForPermission() },
        buttonText = stringResource(id = R.string.next),
        content = {
            Column {
                Text(
                    text = stringResource(id = Permission.permissions[state.currentPermissionIndex].name),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(text = stringResource(id = Permission.permissions[state.currentPermissionIndex].description))
            }
            LinearProgressIndicator(
                progress = { state.currentPermissionIndex / Permission.permissions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        },
        footer = {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSkipPermission() }
            ) {
                Text(text = stringResource(id = R.string.onboarding_skipPermissionButton))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun PermissionScreenPreview() {
    PermissionScreenContent(
        state = OnboardingState(),
    )
}