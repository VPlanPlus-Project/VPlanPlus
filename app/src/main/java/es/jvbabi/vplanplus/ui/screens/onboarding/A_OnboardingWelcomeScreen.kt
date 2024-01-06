package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.InfoDialog
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OnboardingWelcomeScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    var testSchoolError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Welcome(
        onButtonClick = { navController.navigate(Screen.OnboardingSchoolIdScreen.route) },
        onTestSchoolClick = {
            scope.launch {
                isLoading = true
                viewModel.useTestSchool()
                while (viewModel.state.value.isLoading) {
                    delay(50)
                }
                isLoading = false
                if (viewModel.state.value.loginState == LoginState.NONE) {
                    testSchoolError = true
                    viewModel.reset()
                    return@launch
                }
                navController.navigate(Screen.OnboardingFirstProfileScreen.route)
            }
        },
        testSchoolError = testSchoolError,
        onDialogDismissed = { testSchoolError = false },
        isLoading = isLoading
    )
}

@Composable
fun Welcome(
    onButtonClick: () -> Unit,
    onTestSchoolClick: () -> Unit,
    onDialogDismissed: () -> Unit,
    testSchoolError: Boolean,
    isLoading: Boolean
) {
    OnboardingScreen(
        title = stringResource(id = R.string.app_name),
        text = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(id = R.string.onboarding_welcomeText))
                TextButton(onClick = onTestSchoolClick, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                    if (isLoading) CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = Color.Gray,
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                            .padding(6.dp)
                    ) else Text(text = stringResource(id = R.string.onboarding_welcomeContinueTestSchool))
                }
            }
        },
        buttonText = stringResource(id = R.string.lets_go),
        isLoading = false,
        enabled = true,
        onButtonClick = { onButtonClick() }) {
    }

    if (testSchoolError) {
        InfoDialog(
            icon = Icons.Default.Error,
            title = stringResource(id = R.string.onboarding_welcomeTestSchoolErrorTitle),
            message = stringResource(id = R.string.onboarding_welcomeTestSchoolErrorText),
            onOk = { onDialogDismissed() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingWelcomeScreenPreview() {
    Welcome(
        onButtonClick = {},
        onTestSchoolClick = {},
        onDialogDismissed = {},
        testSchoolError = false,
        isLoading = true
    )
}