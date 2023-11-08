package es.jvbabi.vplanplus.ui.screens.onboarding

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen
import kotlinx.coroutines.launch

@Composable
fun OnboardingSchoolIdScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value
    val coroutineScope = rememberCoroutineScope()

    if (state.schoolIdState == SchoolIdCheckResult.VALID) {
        viewModel.newScreen()
        navController.navigate(Screen.OnboardingLoginScreen.route)
    }

    SchoolId(
        onSchoolIdInputChange = { viewModel.onSchoolIdInput(it) },
        onButtonClick = {
            coroutineScope.launch {
                Log.d("OnboardingSchoolIdScreen", "submitting schoolId ${state.schoolId}")
                viewModel.onSchoolIdSubmit()
            }
        },
        state = state
    )

}

@Composable
fun SchoolId(
    onSchoolIdInputChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    state: OnboardingState,
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_schoolIdTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_schoolIdText)) },
        buttonText = stringResource(id = R.string.next),
        isLoading = state.isLoading,
        enabled = !state.isLoading && (state.schoolIdState == SchoolIdCheckResult.SYNTACTICALLY_CORRECT || state.schoolIdState == null),
        onButtonClick = { onButtonClick() },
        content = {
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
        })
}

@Composable
@Preview(showBackground = true)
fun SchoolIdScreenPreview() {
    SchoolId({}, {}, OnboardingState())
}