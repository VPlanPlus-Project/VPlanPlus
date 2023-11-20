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
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen
import kotlinx.coroutines.launch

@Composable
fun OnboardingAddProfileScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel,
) {
    val state = viewModel.state.value
    val coroutineScope = rememberCoroutineScope()

    if (state.profileOptions.isNotEmpty() && state.profileType != null) {
        viewModel.newScreen()
        navController.navigate(Screen.OnboardingProfileSelectScreen.route) { if (state.onboardingCause == OnboardingCause.FIRST_START) popUpTo(0) else popUpTo(Screen.SettingsProfileScreen.route) }
    }

    AddProfileScreen(
        state = state,
        onProfileSelect = { viewModel.onFirstProfileSelect(it) },
        onButtonClick = {
            coroutineScope.launch {
                viewModel.onProfileTypeSubmit()
            }
        }
    )
}

@Composable
fun AddProfileScreen(
    state: OnboardingState,
    onProfileSelect: (ProfileType) -> Unit,
    onButtonClick: () -> Unit,
) {
    OnboardingScreen(
        title = if (state.task == Task.CREATE_SCHOOL) stringResource(id = R.string.onboarding_firstProfileTitle) else stringResource(id = R.string.onboarding_newProfileTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_firstProfileText)) },
        buttonText = stringResource(id = R.string.next),
        isLoading = state.isLoading,
        enabled = state.profileType != null,
        onButtonClick = { onButtonClick() }) {

        ProfileCard(
            title = {
                Text(text = stringResource(id = R.string.onboarding_firstProfileStudentTitle), style = MaterialTheme.typography.headlineSmall)
            },
            text = stringResource(id = R.string.onboarding_firstProfileStudentText),
            isSelected = state.profileType == ProfileType.STUDENT,
        ) { onProfileSelect(ProfileType.STUDENT) }

        ProfileCard(
            title = {
                Text(text = stringResource(id = R.string.onboarding_firstProfileTeacherTitle), style = MaterialTheme.typography.headlineSmall)
            },
            text = stringResource(id = R.string.onboarding_firstProfileTeacherText),
            isSelected = state.profileType == ProfileType.TEACHER,
        ) { onProfileSelect(ProfileType.TEACHER) }

        ProfileCard(
            title = { Text(text = stringResource(id = R.string.onboarding_firstProfileRoomTitle), style = MaterialTheme.typography.headlineSmall) },
            text = stringResource(id = R.string.onboarding_firstProfileRoomText),
            isSelected = state.profileType == ProfileType.ROOM
        ) { onProfileSelect(ProfileType.ROOM) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCard(
    title: @Composable () -> Unit,
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
                    title()
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
                    title()
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
fun OnboardingNewProfileScreenPreview() {
    AddProfileScreen(state = OnboardingState(profileType = ProfileType.STUDENT, task = Task.CREATE_PROFILE), onProfileSelect = {}, onButtonClick = {})
}