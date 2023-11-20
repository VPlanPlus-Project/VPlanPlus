package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.xml.WplanVpXmlDefaultLesson
import es.jvbabi.vplanplus.domain.model.xml.WplanVpXmlDefaultLessonWrapper
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.onboarding.common.OnboardingScreen

@Composable
fun OnboardingDefaultLessonScreen(
    navHostController: NavHostController,
    onboardingViewModel: OnboardingViewModel
) {
    val state = onboardingViewModel.state.value
    val context = LocalContext.current

    OnboardingDefaultLessonContent(
        state = state,
        onNextClicked = {
            onboardingViewModel.onInsertData(context)
            navHostController.navigate(Screen.OnboardingPermissionsScreen.route) { popUpTo(0) }
        },
        onDefaultLessonClicked = {
            onboardingViewModel.setDefaultLesson(it, !state.defaultLessons[it]!!)
        },
        onReloadDefaultLessons = { onboardingViewModel.loadDefaultLessons() }
    )
}

@Composable
fun OnboardingDefaultLessonContent(
    state: OnboardingState,
    onNextClicked: () -> Unit = {},
    onDefaultLessonClicked: (WplanVpXmlDefaultLessonWrapper) -> Unit = {},
    onReloadDefaultLessons: () -> Unit = {}
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_defaultLessonsTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_defaultLessonsText)) },
        buttonText = stringResource(id = R.string.next),
        isLoading = false,
        enabled = state.defaultLessons.values.any { it },
        onButtonClick = { onNextClicked() }) {
        Column {
            if (state.defaultLessonsLoading) Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            } else {
                if (state.defaultLessons.isEmpty()) {
                    Button(onClick = { onReloadDefaultLessons() }) {
                        Text(text = "Reload")
                    }
                } else state.defaultLessons.toList().sortedBy { (key, _) -> key.defaultLesson!!.subjectShort}.toMap().forEach {
                    Box(
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        DefaultLessonCard(
                            subject = it.key.defaultLesson!!.subjectShort!!,
                            teacherAcronym = it.key.defaultLesson!!.teacherShort!!,
                            activated = it.value,
                            onClick = { onDefaultLessonClicked(it.key) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingDefaultLessonScreenPreview() {
    OnboardingDefaultLessonContent(
        state = OnboardingState(
            defaultLessons = mapOf(
                WplanVpXmlDefaultLessonWrapper().apply {
                    this.defaultLesson = WplanVpXmlDefaultLesson().apply {
                        this.lessonId = 10
                        this.subjectShort = "DEU"
                        this.teacherShort = "Mul"
                    }
                } to true
            )
        )
    )
}

@Composable
fun DefaultLessonCard(
    subject: String,
    teacherAcronym: String,
    activated: Boolean,
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = activated, onCheckedChange = { onClick() })
            Text(text = "$subject • $teacherAcronym", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview
@Composable
fun DefaultLessonCardPreview() {
    DefaultLessonCard(subject = "DEU", teacherAcronym = "Mul", activated = true, onClick = {})
}