package es.jvbabi.vplanplus.ui.screens.home

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.screens.Screen
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect("Init") {
        viewModel.init()
    }

    if (state.initDone && !state.activeProfileFound) {
        navHostController.navigate(Screen.OnboardingWelcomeScreen.route) {
            popUpTo(0)
        }
    } else {
        HomeScreenContent(state = state, onGetVPlan = {
            coroutineScope.launch {
                viewModel.getVPlanData()
            }
        })
    }
}

@Composable
fun HomeScreenContent(
    state: HomeState,
    onGetVPlan: () -> Unit
) {
    val context = LocalContext.current
    if (!state.initDone) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            Toast
                                .makeText(context, "Not implemented", LENGTH_SHORT)
                                .show()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(20.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = stringResource(id = R.string.home_search),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .height(40.dp)
                            .width(40.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = MaterialTheme.colorScheme.secondary)
                            .clickable(enabled = true) {
                                Toast
                                    .makeText(context, "Not implemented", LENGTH_SHORT)
                                    .show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.activeProfileShortText,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Text(text = "Next holiday: ${state.nextHoliday}")
                    Button(
                        onClick = {
                            onGetVPlan()
                        }
                    ) {
                        Text(text = "Get VPlan data")
                    }
                }
            }

            Column {
            }
        }
    }
}

@Composable
fun CurrentLessonCard(lesson: es.jvbabi.vplanplus.ui.screens.home.Lesson) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .fillMaxWidth(lesson.progress.toFloat())
                    .fillMaxHeight()
            ) {}
            Box(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                    Column {
                        Text(text = "Jetzt:", style = MaterialTheme.typography.titleSmall)
                        Row {
                            Text(text = lesson.subject, style = MaterialTheme.typography.titleLarge, color = if (lesson.subjectChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer)
                            Text(text = " • ", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            Text(text = lesson.room, style = MaterialTheme.typography.titleLarge, color = if (lesson.roomChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                        Text(text = lesson.teacher, style = MaterialTheme.typography.titleMedium, color = if (lesson.teacherChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                    SubjectIcon(subject = lesson.subject, modifier = Modifier
                        .height(70.dp)
                        .width(70.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }
    }
}

@Composable
@Preview
fun CurrentLessonCardPreview() {
    CurrentLessonCard(
        lesson = Lesson(
            subject = "Informatik",
            teacher = "Tec",
            room = "208",
            roomChanged = true,
            progress = 0.8,
        )
    )
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreenContent(
        HomeState(
            initDone = true,
            activeProfileFound = true,
            activeProfileShortText = "9e",
            nextHoliday = LocalDate.now(),
            lessons = listOf(
                Pair(
                    Lesson(
                        defaultLessonId = 0,
                        lesson = 0,
                        classId = 0,
                        timestamp = 0L,
                        changedTeacherId = null,
                        changedSubject = null,
                        changedInfo = "Test Info",
                        roomId = 203,
                        roomIsChanged = false
                    ),
                    DefaultLesson(
                        schoolId = "0",
                        vpId = 0,
                        subject = "Test Subject",
                        teacherId = 0
                    )
                )
            )
        )
    ) {}
}