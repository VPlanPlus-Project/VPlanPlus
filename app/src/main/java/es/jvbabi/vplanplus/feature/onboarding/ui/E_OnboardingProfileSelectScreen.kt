package es.jvbabi.vplanplus.feature.onboarding.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoDialog
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingProfileOptionListScreen(
    navHostController: NavHostController,
    onboardingViewModel: OnboardingViewModel
) {
    val state = onboardingViewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = state.stage, block = {
        if (state.stage == Stage.PROFILE_TYPE) navHostController.navigateUp()
        if (state.stage == Stage.DEFAULT_LESSONS) navHostController.navigate(Screen.OnboardingDefaultLessonScreen.route)
        if (state.stage == Stage.PERMISSIONS) navHostController.navigate(Screen.OnboardingPermissionsScreen.route)
        if (state.stage == Stage.FINISH) navHostController.navigate(Screen.OnboardingSetupScreen.route)
    })

    ProfileOptionsScreen(
        state = state,
        onItemSelect = { onboardingViewModel.onProfileSelect(it) },
        onButtonClick = { onboardingViewModel.nextStageDefaultLessonOrPermissions(context) },
        setDialogVisibility = { onboardingViewModel.setTeacherDialogVisibility(it) },
    )

    BackHandler {
        if (state.task == Task.CREATE_PROFILE) navHostController.navigate(Screen.OnboardingNewProfileScreen.route) {
            popUpTo(
                0
            )
        }
        else onboardingViewModel.goBackToProfileType()
    }
}

@Composable
fun ProfileOptionsScreen(
    state: OnboardingState,
    onItemSelect: (String) -> Unit,
    onButtonClick: () -> Unit,
    setDialogVisibility: (Boolean) -> Unit = {},
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
            ClickableText(
                text = when (state.profileType) {
                    ProfileType.STUDENT -> studentText
                    ProfileType.TEACHER -> teacherText
                    ProfileType.ROOM -> roomText
                },
                onClick = { offset ->
                    teacherText.getStringAnnotations("CANT_FIND_ACRONYM", offset, offset)
                        .firstOrNull()?.let {
                            setDialogVisibility(true)
                        }
                }
            )
            if (state.profileType == ProfileType.STUDENT && state.profileOptions.any { it.second > 0 }) {
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
        isLoading = state.isLoading,
        enabled = !state.isLoading && state.selectedProfileOption != null,
        onButtonClick = { onButtonClick() },
        content = {

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
                        itemName = it.first,
                        itemCount = it.second,
                        isSelected = state.selectedProfileOption == it.first
                    ) { onItemSelect(it.first) }
                }
            }
        }
    )
}

@Composable
fun ProfileOptionsItem(
    itemName: String,
    itemCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderAlpha =
        animateFloatAsState(targetValue = if (isSelected) 1f else 0f, label = "BorderAlpha")

    val content = @Composable {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            if (itemCount > 0) {
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
        state = OnboardingState(
            profileType = ProfileType.STUDENT,
            profileOptions = listOf(
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
        onItemSelect = {},
        onButtonClick = {},
    )
}

@Preview(showBackground = true)
@Composable
fun TeacherListScreenPreview() {
    ProfileOptionsScreen(
        state = OnboardingState(
            profileType = ProfileType.TEACHER,
            profileOptions = listOf("Bac" to 0, "Mei" to 0, "Kra" to 0, "Vle" to 0)
        ),
        onItemSelect = {},
        onButtonClick = {},
    )
}

@Preview(showBackground = true)
@Composable
fun RoomListScreenPreview() {
    ProfileOptionsScreen(
        state = OnboardingState(
            profileType = ProfileType.ROOM,
            profileOptions = listOf(
                "108" to 0,
                "109" to 0,
                "TH1" to 0,
                "TH2" to 0,
                "K17" to 0,
                "207" to 0,
                "208" to 0
            )
        ),
        onItemSelect = {},
        onButtonClick = {},
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