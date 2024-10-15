package es.jvbabi.vplanplus.feature.ndp.ui.guided

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskDone
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.TeacherPreview
import es.jvbabi.vplanplus.util.toInt
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

@Composable
fun NdpHomeworkScreen(
    homework: List<PersonalizedHomework>,
    onToggleTask: (task: HomeworkTaskDone) -> Unit,
    onHide: (homework: PersonalizedHomework) -> Unit,
    onOpenHomework: (homework: PersonalizedHomework) -> Unit,
    onContinue: () -> Unit,
    currentStage: NdpStage,
    enabled: Boolean
) {
    val listState = rememberLazyListState()
    LaunchedEffect(remember{ derivedStateOf { listState.firstVisibleItemIndex }}, listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val index = listState.firstVisibleItemIndex + (listState.firstVisibleItemScrollOffset > (listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == listState.firstVisibleItemIndex } ?: return@LaunchedEffect).size / 2).toInt()
            try {
                if (listState.firstVisibleItemScrollOffset != 0) listState.animateScrollToItem(index)
            } catch (e: NoSuchElementException) {
                // Do nothing
            }
        }
    }
    LaunchedEffect(homework) {
        Log.d("LE", "CurrentStage: ${currentStage.name}")
        if (currentStage != NdpStage.HOMEWORK) return@LaunchedEffect
        val currentIndex = homework.indexOfFirst { !it.allDone() }
        Log.d("LE", "CurrentIndex: $currentIndex")
        if (currentIndex != -1) {
            try {
                listState.animateScrollToItem(currentIndex)
            } catch (e: NoSuchElementException) {
                // Do nothing
            }
        } else onContinue()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp)
                .weight(1f),
            state = listState
        ) {
            items(homework) { homeworkItem ->
                NdpHomeworkListItem(
                    homework = homeworkItem,
                    onToggleTask = onToggleTask,
                    onHide = onHide,
                    onOpenHomework = onOpenHomework
                )
                Spacer8Dp()
            }
        }
        Box(Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onContinue,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp),
                enabled = enabled
            ) {
                RowVerticalCenter {
                    Icon(Icons.Default.SkipNext, contentDescription = null)
                    Spacer4Dp()
                    Text(stringResource(R.string.ndp_guidedHomeworkSkipButton))
                }
            }
        }
    }
}

@Composable
private fun NdpHomeworkListItem(
    homework: PersonalizedHomework,
    onToggleTask: (task: HomeworkTaskDone) -> Unit,
    onHide: (homework: PersonalizedHomework) -> Unit,
    onOpenHomework: (homework: PersonalizedHomework) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onOpenHomework(homework) }
            .padding(8.dp)
    ) {
        RowVerticalCenterSpaceBetweenFill(
            modifier = Modifier
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                .fillMaxWidth()
        ) {
            RowVerticalCenter {
                SubjectIcon(
                    subject = homework.homework.defaultLesson?.subject,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer8Dp()
                Text(
                    text = homework.homework.defaultLesson?.subject ?: stringResource(R.string.homework_noSubject),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (homework.homework.defaultLesson?.teacher != null) {
                    Text(
                        text = " $DOT ${homework.homework.defaultLesson?.teacher?.acronym}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                if (homework.homework.defaultLesson?.courseGroup != null) {
                    Text(
                        text = " $DOT ${homework.homework.defaultLesson?.courseGroup}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
            if (homework is PersonalizedHomework.CloudHomework) TextButton(onClick = { onHide(homework) }) {
                RowVerticalCenter {
                    Icon(Icons.Default.SkipNext, contentDescription = null)
                    Spacer4Dp()
                    Text("Hide and skip")
                }
            }
        }
        Spacer4Dp()
        homework.tasks.forEach { task ->
            val onToggleTaskLambda = remember(task.isDone) { { onToggleTask(task) } }
            RowVerticalCenter(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onToggleTaskLambda() }
            ) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = {
                        onToggleTaskLambda()
                    }
                )
                Spacer8Dp()
                Text(
                    text = task.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview
@Composable
private fun NdpHomeworkScreenPreview() {
    val school = SchoolPreview.generateRandomSchool()
    val group = GroupPreview.generateGroup(school)
    val profile = ProfilePreview.generateClassProfile(group)
    NdpHomeworkScreen(
        homework = listOf(
            PersonalizedHomework.LocalHomework(
                homework = HomeworkCore.LocalHomework(
                    id = -1,
                    defaultLesson = DefaultLesson(
                        defaultLessonId = UUID.randomUUID(),
                        vpId = 1,
                        subject = "DEU",
                        teacher = TeacherPreview.teacher(school),
                        `class` = group,
                        courseGroup = null
                    ),
                    createdAt = ZonedDateTime.now(),
                    documents = emptyList(),
                    tasks = emptyList(),
                    until = LocalDate.now().atStartOfDay().plusDays(7)
                        .atZone(ZoneId.systemDefault()),
                    profile = profile
                ),
                profile = profile,
                tasks = listOf(
                    HomeworkTaskDone(
                        id = -1,
                        homeworkId = -1,
                        content = "Task A",
                        isDone = true
                    ),
                    HomeworkTaskDone(
                        id = -1,
                        homeworkId = -1,
                        content = "Task B",
                        isDone = false
                    )
                )
            ),
        ),
        onToggleTask = {},
        onHide = {},
        onContinue = {},
        onOpenHomework = {},
        enabled = false,
        currentStage = NdpStage.HOMEWORK
    )
}