package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen
import kotlinx.coroutines.launch

@Composable
fun OnboardingFirstProfileScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value
    val coroutineScope = rememberCoroutineScope()

    if (state.classList.isNotEmpty()) {
        viewModel.newScreen()
        navController.navigate(Screen.OnboardingClassListScreen.route) { popUpTo(0) }
    }

    FirstProfileScreen(
        state = state,
        onProfileSelect = { viewModel.onFirstProfileSelect(it) },
        onButtonClick = {
            coroutineScope.launch {
                viewModel.onFirstProfileSubmit()
            }
        }
    )
}

@Composable
fun FirstProfileScreen(
    state: OnboardingState,
    onProfileSelect: (FirstProfile) -> Unit,
    onButtonClick: () -> Unit,
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_firstProfileTitle),
        text = stringResource(id = R.string.onboarding_firstProfileText),
        buttonText = stringResource(id = R.string.next),
        isLoading = state.isLoading,
        enabled = state.firstProfile != null,
        onButtonClick = { onButtonClick() }) {

        ProfileCard(
            title = stringResource(id = R.string.onboarding_firstProfileStudentTitle),
            text = stringResource(id = R.string.onboarding_firstProfileStudentText),
            isSelected = state.firstProfile == FirstProfile.STUDENT,
        ) { onProfileSelect(FirstProfile.STUDENT) }

        ProfileCard(
            title = stringResource(id = R.string.onboarding_firstProfileTeacherTitle),
            text = stringResource(id = R.string.onboarding_firstProfileTeacherText),
            isSelected = state.firstProfile == FirstProfile.TEACHER,
        ) { onProfileSelect(FirstProfile.TEACHER) }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCard(
    title: String,
    text: String,
    isSelected: Boolean,
    onProfileSelect: () -> Unit,
) {
    val borderAlpha = animateFloatAsState(targetValue = if (isSelected) 1f else 0f, label = "BorderAlpha")
    if (isSelected) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = borderAlpha.value)),
            modifier = Modifier
                .padding(PaddingValues(0.dp, 4.dp))
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = text,
                    )

                }
            }
        }
    } else {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier
                .padding(PaddingValues(0.dp, 4.dp))
                .fillMaxWidth(),
            onClick = { onProfileSelect() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = text,
                    )

                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun OnboardingFirstProfileScreenPreview() {
    FirstProfileScreen(state = OnboardingState(firstProfile = FirstProfile.STUDENT), onProfileSelect = {}, onButtonClick = {})
}