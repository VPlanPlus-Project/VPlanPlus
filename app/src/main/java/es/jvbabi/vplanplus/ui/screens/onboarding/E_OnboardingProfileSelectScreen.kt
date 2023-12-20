package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.ui.common.InfoDialog
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen
import kotlinx.coroutines.launch

@Composable
fun OnboardingProfileOptionListScreen(
    navController: NavHostController,
    onboardingViewModel: OnboardingViewModel
) {
    val state = onboardingViewModel.state.value
    val coroutineScope = rememberCoroutineScope()

    ProfileOptionsScreen(
        state = state,
        onClassSelect = { onboardingViewModel.onProfileSelect(it) }, {
            coroutineScope.launch { onboardingViewModel.onProfileSubmit() }
            if (state.profileType == ProfileType.STUDENT) navController.navigate(Screen.OnboardingDefaultLessonScreen.route) { popUpTo(0) }
            else navController.navigate(Screen.OnboardingPermissionsScreen.route) { popUpTo(0) }
        },
        setDialogVisibility = { onboardingViewModel.setTeacherDialogVisibility(it) }
    )

}

@Composable
fun ProfileOptionsScreen(
    state: OnboardingState,
    onClassSelect: (String) -> Unit,
    onButtonClick: () -> Unit,
    setDialogVisibility: (Boolean) -> Unit = {}
) {

    val studentText = studentAnnotatedText()
    val teacherText = teacherAnnotatedText(showHint = state.task == Task.CREATE_SCHOOL)
    val roomText = roomAnnotatedText()

    OnboardingScreen(
        title = when (state.profileType!!) {
            ProfileType.STUDENT -> stringResource(id = R.string.onboarding_studentChooseClassTitle)
            ProfileType.TEACHER -> stringResource(id = R.string.onboarding_teacherChooseTeacherTitle)
            ProfileType.ROOM -> stringResource(id = R.string.onboarding_roomChooseRoomTitle)
        },
        text = {
            ClickableText(text = when (state.profileType) {
                ProfileType.STUDENT -> studentText
                ProfileType.TEACHER -> teacherText
                ProfileType.ROOM -> roomText
            },
                onClick = { offset ->
                    teacherText.getStringAnnotations("CANT_FIND_ACRONYM", offset, offset)
                        .firstOrNull()?.let {
                            setDialogVisibility(true)
                        }
                })
        },
        buttonText = stringResource(id = R.string.next),
        isLoading = state.isLoading,
        enabled = !state.isLoading && state.selectedProfileOption != null,
        onButtonClick = { onButtonClick() }) {

        if (state.showTeacherDialog) InfoDialog(
            icon = Icons.Default.Info,
            title = stringResource(id = R.string.info),
            message = stringResource(
                id = R.string.onboarding_firstProfileTeacherDialogText
            ),
            onOk = { setDialogVisibility(false) }
        )

        Column {
            state.profileOptions.forEach {
                ProfileOptionsItem(
                    className = it,
                    isSelected = state.selectedProfileOption == it
                ) { onClassSelect(it) }
            }
        }
    }
}

@Composable
fun ProfileOptionsItem(
    className: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderAlpha =
        animateFloatAsState(targetValue = if (isSelected) 1f else 0f, label = "BorderAlpha")
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = className,
                        style = MaterialTheme.typography.headlineSmall
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
            onClick = { onClick() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = className,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClassListScreenPreview() {
    ProfileOptionsScreen(
        state = OnboardingState(
            profileType = ProfileType.STUDENT,
            profileOptions = listOf(
                "1a",
                "1b",
                "1c",
                "2a",
                "2b",
                "2c",
                "3a",
                "3b",
                "3c",
                "4a",
                "4b",
                "4c",
                "5a",
                "5b",
                "5c"
            )
        ),
        onClassSelect = {},
        onButtonClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun TeacherListScreenPreview() {
    ProfileOptionsScreen(
        state = OnboardingState(
            profileType = ProfileType.TEACHER,
            profileOptions = listOf("Bac", "Mei", "Kra", "Vle")
        ),
        onClassSelect = {},
        onButtonClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun RoomListScreenPreview() {
    ProfileOptionsScreen(
        state = OnboardingState(
            profileType = ProfileType.ROOM,
            profileOptions = listOf("108", "109", "TH1", "TH2", "K17", "207", "208")
        ),
        onClassSelect = {},
        onButtonClick = {}
    )
}

@Composable
fun studentAnnotatedText(): AnnotatedString {
    return buildAnnotatedString { append(stringResource(id = R.string.onboarding_studentChooseClassText)) }
}

@Composable
fun teacherAnnotatedText(showHint: Boolean): AnnotatedString {
    return buildAnnotatedString {
        append(stringResource(id = R.string.onboarding_teacherChooseTeacherText))
        if (!showHint) return@buildAnnotatedString
        append(" ")
        pushStringAnnotation("CANT_FIND_ACRONYM", "this") // Annotate the text
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

@Composable
fun roomAnnotatedText(): AnnotatedString {
    return buildAnnotatedString { append(stringResource(id = R.string.onboarding_roomChooseRoomText)) }
}