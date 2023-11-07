package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen

@Composable
fun FirstProfileStudent() {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_studentChooseClassTitle),
        text = stringResource(id = R.string.onboarding_studentChooseClassText),
        buttonText = stringResource(id = R.string.next),
        isLoading = false,
        enabled = false,
        onButtonClick = { /*TODO*/ }) {
        
    }
}

@Preview(showBackground = true)
@Composable
fun FirstProfileStudentPreview() {
    FirstProfileStudent()
}