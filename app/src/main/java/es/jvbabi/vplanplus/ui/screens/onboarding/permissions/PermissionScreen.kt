package es.jvbabi.vplanplus.ui.screens.onboarding.permissions

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen

@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = "-", block = {
        viewModel.init(context = context)
    })

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (viewModel.isLast()) navController.navigate(Screen.OnboardingSetupScreen.route)
            else viewModel.nextPermission()
        })

    Log.d("PermissionScreen", "index: ${state.index}/ ${Permission.permissions.size}")
    PermissionScreenContent(
        permissionIndex = state.index,
        onAskForPermission = {
            launcher.launch(Permission.permissions[state.index].type)
        },
        onNext = {
            if (state.permission[state.index].second) {
                if (viewModel.isLast()) navController.navigate(Screen.OnboardingSetupScreen.route)
                else viewModel.nextPermission()
            }
        }
    )
}

@Composable
fun PermissionScreenContent(
    permissionIndex: Int,
    onAskForPermission: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    val permission = Permission.permissions[permissionIndex]
    OnboardingScreen(
        title = stringResource(
            id = R.string.onboarding_permissionTitle,
            permissionIndex + 1,
            Permission.permissions.size
        ),
        text = {
            Column {
                Text(
                    text = stringResource(id = permission.name),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(text = stringResource(id = permission.description))
            }
        },
        buttonText = stringResource(id = R.string.ok),
        isLoading = false,
        enabled = true,
        onButtonClick = { onAskForPermission() }) {
        val progress = remember { Animatable((permissionIndex) / Permission.permissions.size.toFloat()) }
        LaunchedEffect(key1 = permissionIndex, block = {
            Log.d("PermissionScreen", "index: ${permissionIndex}/ ${Permission.permissions.size}")
            progress.animateTo((permissionIndex) / Permission.permissions.size.toFloat())
        })
        if (progress.value == permissionIndex / Permission.permissions.size.toFloat()) onNext()
        LinearProgressIndicator(
            progress = { progress.value },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PermissionsScreenPreview() {
    PermissionScreenContent(0)
}