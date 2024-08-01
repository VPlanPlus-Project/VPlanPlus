package es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingDefaultLesson
import es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.ui.components.NoDataAvailable
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingDefaultLessonScreen(
    navHostController: NavHostController,
    viewModel: OnboardingDefaultLessonsViewModel = hiltViewModel()
) {
    val state = viewModel.state

    OnboardingDefaultLessonContent(
        state = state,
        doAction = viewModel::doAction,
        onProceed = {
            viewModel.doAction(OnProceed { navHostController.navigate(Screen.OnboardingPermissionsScreen.route) })
        }
    )
}

@Composable
fun OnboardingDefaultLessonContent(
    state: OnboardingDefaultLessonsState,
    doAction: (action: UiAction) -> Unit,
    onProceed: () -> Unit,
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_defaultLessonsTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_defaultLessonsText)) },
        buttonText = stringResource(id = R.string.next),
        isLoading = false,
        enabled = true,
        onButtonClick = onProceed,
        content = {
            if (state.defaultLessons.isEmpty()) NoDataAvailable()
            else Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.defaultLessons.toList()
                    .sortedBy { (key, _) -> key.subject }
                    .toMap()
                    .forEach {
                        DefaultLessonCard(
                            subject = it.key.subject,
                            teacherAcronym = it.key.teacher,
                            activated = it.value,
                            onClick = { doAction(ToggleDefaultLesson(it.key)) },
                            courseGroup = it.key.courseGroup
                        )
                    }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun OnboardingDefaultLessonScreenPreview() {
    OnboardingDefaultLessonContent(
        state = OnboardingDefaultLessonsState(
            defaultLessons = mapOf(
                OnboardingDefaultLesson(
                    subject = "DEU",
                    teacher = "Mul",
                    clazz = "1A",
                    vpId = 0,
                    courseGroup = "DE1"
                ) to true,
                OnboardingDefaultLesson(
                    subject = "MAT",
                    teacher = "Wer",
                    clazz = "1A",
                    vpId = 1,
                    courseGroup = "MA1"
                ) to false,
            )
        ),
        doAction = {},
        onProceed = {}
    )
}

@Preview(showBackground = true)
@Composable
fun NoDefaultLessonsAvailable() {
    OnboardingDefaultLessonContent(
        state = OnboardingDefaultLessonsState(),
        doAction = {},
        onProceed = {}
    )
}

@Composable
fun DefaultLessonCard(
    subject: String,
    teacherAcronym: String?,
    activated: Boolean,
    courseGroup: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.CenterStart
    ) {
        RowVerticalCenterSpaceBetweenFill {
            RowVerticalCenter {
                Checkbox(checked = activated, onCheckedChange = { onClick() })
                Text(text = subject, style = MaterialTheme.typography.titleMedium)
                Spacer8Dp()
                Text(
                    text = teacherAcronym ?: stringResource(id = R.string.settings_profileDefaultLessonNoTeacher),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            if (courseGroup != null) Text(
                text = courseGroup,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun DefaultLessonCardPreview() {
    DefaultLessonCard(subject = "DEU", teacherAcronym = "Mul", activated = true, onClick = {}, courseGroup = null)
}

@Preview
@Composable
private fun DefaultLessonCardWithoutTeacherPreview() {
    DefaultLessonCard(subject = "DEU", teacherAcronym = "", activated = true, onClick = {}, courseGroup = "DE1")
}