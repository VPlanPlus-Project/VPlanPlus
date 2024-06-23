package es.jvbabi.vplanplus.feature.onboarding.stages.g_permissions.ui

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.common.Permission
import es.jvbabi.vplanplus.ui.screens.Screen


@Composable
fun OnboardingPermissionScreen(
    navController: NavHostController,
    viewModel: OnboardingPermissionsViewModel = viewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.init(context)
    }

    val state = viewModel.state

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { viewModel.doAction(Next(context)) }
    )

    PermissionScreenContent(
        state = state,
        onAskForPermission = { launcher.launch(state.permissions.toList()[state.currentIndex!!].first.type) },
        onSkipPermission = { viewModel.doAction(Next(context)) },
    )

    LaunchedEffect(key1 = state.allDone) {
        if (state.allDone) navController.navigate(Screen.OnboardingSetupScreen.route)
    }
}

@Composable
fun PermissionScreenContent(
    state: OnboardingPermissionsState,
    onAskForPermission: () -> Unit = {},
    onSkipPermission: () -> Unit = {},
) {
    OnboardingScreen(
        title = if (state.currentIndex != null) stringResource(
            id = R.string.onboarding_permissionTitle,
            state.currentIndex + 1,
            state.permissions.size
        ) else "",
        text = {},
        enabled = true,
        isLoading = false,
        onButtonClick = { onAskForPermission() },
        buttonText = stringResource(id = R.string.next),
        content = {
            Column {
                if (state.currentIndex == null) return@Column
                Text(
                    text = stringResource(id = state.permissions.toList()[state.currentIndex].first.name),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(text = stringResource(id = state.permissions.toList()[state.currentIndex].first.description))
            }
            LinearProgressIndicator(
                progress = { (state.currentIndex ?: 0) / state.permissions.size.toFloat().coerceAtLeast(1f) },
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
        state = OnboardingPermissionsState(
            permissions = Permission.onboardingPermissions.associateWith { false },
            currentIndex = 0,
        ),
    )
}