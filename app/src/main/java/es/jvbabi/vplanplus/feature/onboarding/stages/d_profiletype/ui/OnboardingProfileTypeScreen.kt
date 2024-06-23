package es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ProfileType.ROOM
import es.jvbabi.vplanplus.domain.model.ProfileType.STUDENT
import es.jvbabi.vplanplus.domain.model.ProfileType.TEACHER
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingAddProfileScreen(
    navController: NavHostController,
    viewModel: OnboardingProfileTypeViewModel = hiltViewModel(),
) {
    val state = viewModel.state

    AddProfileScreen(
        state = state,
        doAction = viewModel::doAction,
        onContinue = { viewModel.doAction(OnProceed {navController.navigate(Screen.OnboardingProfileSelectScreen.route)}) }
    )
}

@Composable
fun AddProfileScreen(
    state: OnboardingProfileTypeState,
    doAction: (UiAction) -> Unit,
    onContinue: () -> Unit,
) {
    OnboardingScreen(
        title = if (state.isFirstProfileForSchool) stringResource(id = R.string.onboarding_firstProfileTitle) else stringResource(id = R.string.onboarding_newProfileTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_firstProfileText)) },
        buttonText = stringResource(id = R.string.next),
        isLoading = false,
        enabled = state.profileType != null,
        onButtonClick = { onContinue() },
        content = {
            if (state.isLimitedToStudentProfile) InfoCard(
                imageVector = Icons.Default.Warning,
                title = stringResource(id = R.string.onboarding_newProfileNotFullySupportedSchoolWarningTitle),
                text = stringResource(id = R.string.onboarding_newProfileNotFullySupportedSchoolWarningText),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ProfileCard(
                title = stringResource(id = R.string.onboarding_firstProfileStudentTitle),
                text = stringResource(id = R.string.onboarding_firstProfileStudentText),
                isSelected = state.profileType == STUDENT,
                enabled = true
            ) { doAction(SelectProfileType(STUDENT)) }

            ProfileCard(
                title = stringResource(id = R.string.onboarding_firstProfileTeacherTitle),
                text = stringResource(id = R.string.onboarding_firstProfileTeacherText),
                isSelected = state.profileType == TEACHER,
                enabled = !state.isLimitedToStudentProfile
            ) { doAction(SelectProfileType(TEACHER)) }

            ProfileCard(
                title = stringResource(id = R.string.onboarding_firstProfileRoomTitle),
                text = stringResource(id = R.string.onboarding_firstProfileRoomText),
                isSelected = state.profileType == ROOM,
                enabled = !state.isLimitedToStudentProfile
            ) { doAction(SelectProfileType(ROOM)) }
        }
    )
}

@Composable
fun ProfileCard(
    title: String,
    text: String,
    isSelected: Boolean,
    enabled: Boolean = true,
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
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (!enabled) Color.Gray else MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Text(
                        text = text,
                        color = if (!enabled) Color.Gray else MaterialTheme.colorScheme.onSecondaryContainer,
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
            onClick = { if (enabled) onProfileSelect() }
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
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (!enabled) Color.Gray else MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Text(
                        text = text,
                        color = if (!enabled) Color.Gray else MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun OnboardingNewProfileScreenPreview() {
    AddProfileScreen(
        state = OnboardingProfileTypeState(),
        doAction = {},
        onContinue = {}
    )
}