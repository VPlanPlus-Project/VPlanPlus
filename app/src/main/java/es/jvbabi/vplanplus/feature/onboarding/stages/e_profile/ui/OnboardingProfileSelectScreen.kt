package es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoDialog
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.VerticalExpandVisibility
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingProfileSelectScreen(
    navHostController: NavHostController,
    viewModel: OnboardingProfileSelectViewModel = hiltViewModel()
) {
    val state = viewModel.state

    ProfileOptionsScreen(
        state = state,
        onProceed = {
            if (state.selectedOption == null) return@ProfileOptionsScreen
            viewModel.doAction(OnProceed {
                if (state.profileType == ProfileType.STUDENT) navHostController.navigate(Screen.OnboardingDefaultLessonScreen.route)
                else navHostController.navigate(Screen.OnboardingPermissionsScreen.route)
            })
        },
        doAction = viewModel::doAction
    )
}

@Composable
private fun ProfileOptionsScreen(
    state: OnboardingProfileSelectState,
    onProceed: () -> Unit,
    doAction: (action: UiAction) -> Unit = {}
) {

    val studentText = studentAnnotatedText()
    val teacherText = teacherAnnotatedText(showHint = state.isFirstProfile)
    val roomText = roomAnnotatedText()

    OnboardingScreen(
        title = when (state.profileType) {
            ProfileType.STUDENT -> stringResource(id = R.string.onboarding_studentChooseClassTitle)
            ProfileType.TEACHER -> stringResource(id = R.string.onboarding_teacherChooseTeacherTitle)
            ProfileType.ROOM -> stringResource(id = R.string.onboarding_roomChooseRoomTitle)
        },
        text = {
            Text(
                text = when (state.profileType) {
                    ProfileType.STUDENT -> studentText
                    ProfileType.TEACHER -> teacherText
                    ProfileType.ROOM -> roomText
                }
            )
            VerticalExpandVisibility(visible = state.profileType == ProfileType.STUDENT && state.options.any { (it.value?:0) > 0 }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PeopleAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(16.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.onboarding_studentChooseClassPeopleLabel),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        buttonText = stringResource(id = R.string.next),
        isLoading = false,
        enabled = state.canProceed,
        onButtonClick = onProceed,
        content = {
            Column {
                state.options.forEach { (name, users) ->
                    ProfileOptionsItem(
                        itemName = name,
                        itemCount = if (state.options.any { (it.value?:0) > 0 }) users else null,
                        isSelected = state.selectedOption == name
                    ) { doAction(SelectOption(name)) }
                }
            }
        }
    )
}

@Composable
fun ProfileOptionsItem(
    itemName: String,
    itemCount: Int?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderAlpha =
        animateFloatAsState(targetValue = if (isSelected) 1f else 0f, label = "BorderAlpha")

    val content = @Composable {
        RowVerticalCenter(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            if (itemCount != null) {
                Text(
                    text = DOT,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.PeopleAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(16.dp)
                )
                Text(
                    text = itemCount.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
    if (isSelected) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = borderAlpha.value)
            ),
            modifier = Modifier
                .padding(PaddingValues(0.dp, 4.dp))
                .fillMaxWidth(),
        ) { content() }
    } else {
        Card(
            colors = CardDefaults.cardColors(),
            modifier = Modifier
                .padding(PaddingValues(0.dp, 4.dp))
                .fillMaxWidth(),
            onClick = { onClick() }
        ) { content() }
    }
}

@Preview(showBackground = true)
@Composable
fun ClassListScreenPreview() {
    ProfileOptionsScreen(
        state = OnboardingProfileSelectState(
            profileType = ProfileType.STUDENT,
            options = mapOf(
                "1a" to 1,
                "1b" to 0,
                "1c" to 0,
                "2a" to 0,
                "2b" to 1,
                "2c" to 3,
                "3a" to 2,
                "3b" to 1,
                "3c" to 2,
                "4a" to 6,
                "4b" to 9,
                "4c" to 5,
                "5a" to 11,
                "5b" to 15,
                "5c" to 16
            )
        ),
        onProceed = {}
    )
}

@Preview(showBackground = true)
@Composable
fun TeacherListScreenPreview() {
    ProfileOptionsScreen(
        state = OnboardingProfileSelectState(
            profileType = ProfileType.TEACHER,
            options = mapOf(
                "Bac" to null,
                "Mei" to null,
                "Kra" to null,
                "Vle" to null,
            )
        ),
        onProceed = {}
    )
}

@Preview(showBackground = true)
@Composable
fun RoomListScreenPreview() {
    ProfileOptionsScreen(
        state = OnboardingProfileSelectState(
            profileType = ProfileType.ROOM,
            options = mapOf(
                "108" to null,
                "109" to null,
                "TH1" to null,
                "TH2" to null,
                "K17" to null,
                "207" to null,
                "208" to null,
            )
        ),
        onProceed = {}
    )
}

@Composable
fun studentAnnotatedText(): AnnotatedString {
    return buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.onSurface
            )
        ) {
            append(stringResource(id = R.string.onboarding_studentChooseClassText))
        }
    }
}

@Composable
fun teacherAnnotatedText(showHint: Boolean): AnnotatedString {
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }
    if (showInfoDialog) InfoDialog(
        icon = Icons.Default.Info,
        title = stringResource(id = R.string.info),
        message = stringResource(
            id = R.string.onboarding_firstProfileTeacherDialogText
        ),
        onOk = { showInfoDialog = false }
    )
    return buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.onSurface
            )
        ) {
            append(stringResource(id = R.string.onboarding_teacherChooseTeacherText))
        }
        if (!showHint) return@buildAnnotatedString
        append(" ")
        withLink(
            LinkAnnotation.Clickable(
                linkInteractionListener = { showInfoDialog = true },
                tag = "teacherHint"
            )
        ) {
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(stringResource(id = R.string.onboarding_teacherChooseTeacherCantFindProfile))
            }
        }
    }
}

@Composable
fun roomAnnotatedText(): AnnotatedString {
    return buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.onSurface
            )
        ) {
            append(stringResource(id = R.string.onboarding_roomChooseRoomText))
        }
    }
}