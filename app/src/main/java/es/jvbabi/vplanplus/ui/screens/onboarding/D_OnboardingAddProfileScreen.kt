package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.CloseOnboardingDialog
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen

@Composable
fun OnboardingAddProfileScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel,
) {
    val state = viewModel.state.value

    LaunchedEffect(key1 = state.stage, block = {
        if (state.stage == Stage.PROFILE) {
            viewModel.newScreen()
            navController.navigate(Screen.OnboardingProfileSelectScreen.route) { if (state.onboardingCause == OnboardingCause.NEW_PROFILE) popUpTo(Screen.SettingsProfileScreen.route) }
        }
    })

    AddProfileScreen(
        state = state,
        onProfileSelect = { viewModel.onFirstProfileSelect(it) },
        onButtonClick = {
            viewModel.nextStageProfile()
        },
        showCloseDialog = state.showCloseDialog,
        hideCloseDialog = { viewModel.hideCloseDialog() },
    )

    BackHandler(enabled = true) {
        viewModel.showCloseDialog()
    }
}

@Composable
fun AddProfileScreen(
    state: OnboardingState,
    onProfileSelect: (ProfileType) -> Unit,
    onButtonClick: () -> Unit,
    showCloseDialog: Boolean,
    hideCloseDialog: () -> Unit,
) {
    OnboardingScreen(
        title = if (state.task == Task.CREATE_SCHOOL) stringResource(id = R.string.onboarding_firstProfileTitle) else stringResource(id = R.string.onboarding_newProfileTitle),
        text = { if (state.loginState == LoginState.FULL) Text(text = stringResource(id = R.string.onboarding_firstProfileText)) },
        buttonText = stringResource(id = R.string.next),
        isLoading = state.isLoading,
        enabled = state.profileType != null,
        onButtonClick = { onButtonClick() },
        content = {
            if (state.loginState == LoginState.PARTIAL) InfoCard(
                imageVector = Icons.Default.Warning,
                title = stringResource(id = R.string.onboarding_newProfileNotFullySupportedSchoolWarningTitle),
                text = stringResource(id = R.string.onboarding_newProfileNotFullySupportedSchoolWarningText),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ProfileCard(
                title = stringResource(id = R.string.onboarding_firstProfileStudentTitle),
                text = stringResource(id = R.string.onboarding_firstProfileStudentText),
                isSelected = state.profileType == ProfileType.STUDENT,
                enabled = true
            ) { onProfileSelect(ProfileType.STUDENT) }

            ProfileCard(
                title = stringResource(id = R.string.onboarding_firstProfileTeacherTitle),
                text = stringResource(id = R.string.onboarding_firstProfileTeacherText),
                isSelected = state.profileType == ProfileType.TEACHER,
                enabled = state.loginState == LoginState.FULL
            ) { onProfileSelect(ProfileType.TEACHER) }

            ProfileCard(
                title = stringResource(id = R.string.onboarding_firstProfileRoomTitle),
                text = stringResource(id = R.string.onboarding_firstProfileRoomText),
                isSelected = state.profileType == ProfileType.ROOM,
                enabled = state.loginState == LoginState.FULL
            ) { onProfileSelect(ProfileType.ROOM) }
        }
    )

    if (showCloseDialog) CloseOnboardingDialog(onNo = { hideCloseDialog() })
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
                        color = if (!enabled) Color.Gray else Color.Unspecified,
                    )
                    Text(
                        text = text,
                        color = if (!enabled) Color.Gray else Color.Unspecified,
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
                        color = if (!enabled) Color.Gray else Color.Unspecified,
                    )
                    Text(
                        text = text,
                        color = if (!enabled) Color.Gray else Color.Unspecified,
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
        state = OnboardingState(
            profileType = ProfileType.STUDENT,
            task = Task.CREATE_PROFILE,
            loginState = LoginState.PARTIAL,
        ),
        onProfileSelect = {},
        onButtonClick = {},
        showCloseDialog = false,
        hideCloseDialog = {},
    )
}