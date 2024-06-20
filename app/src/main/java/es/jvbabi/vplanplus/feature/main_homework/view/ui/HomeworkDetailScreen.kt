package es.jvbabi.vplanplus.feature.main_homework.view.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.DefaultLessonCard
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.Documents
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.DueToCard
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.ProgressCard
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.Tasks
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.preview.TeacherPreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import java.time.ZonedDateTime
import java.util.UUID

@Composable
fun HomeworkDetailScreen(
    navHostController: NavHostController,
    viewModel: HomeworkDetailViewModel = hiltViewModel(),
    homeworkId: Int
) {
    LaunchedEffect(key1 = homeworkId) { viewModel.init(homeworkId) }
    val state = viewModel.state
    HomeworkDetailScreenContent(
        onBack = { navHostController.popBackStack() },
        onAction = viewModel::onAction,
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkDetailScreenContent(
    onBack: () -> Unit = {},
    onAction: (action: UiAction) -> Unit = {},
    state: HomeworkDetailState
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Hausaufgabe") }, // TODO sr
                navigationIcon = { IconButton(onClick = onBack) { BackIcon() } },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer4Dp()
            ProgressCard(tasks = state.homework?.tasks?.size ?: 0, done = state.homework?.tasks?.count { it.done } ?: 0)
            RowVerticalCenter(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DefaultLessonCard(defaultLesson = state.homework?.defaultLesson)
                VerticalDivider(Modifier.height(64.dp))
                DueToCard(until = state.homework?.until?.toLocalDate())
            }
            HorizontalDivider(Modifier.padding(8.dp))
            Tasks(tasks = state.homework?.tasks?: emptyList(), onTaskClicked = { onAction(TaskDoneStateToggledAction(it)) })
            Documents(documents = state.homework?.documents ?: emptyList())
        }
    }
}

@Preview
@Composable
fun HomeworkDetailScreenPreview() {
    val school = School.generateRandomSchools(1).first()
    val clazz = ClassesPreview.generateClass(school)
    val vppId = VppIdPreview.generateVppId(clazz)
    val profile = ProfilePreview.generateClassProfile(vppId)
    val teacher = TeacherPreview.teacher(school)
    HomeworkDetailScreenContent(
        state = HomeworkDetailState(
            homework = Homework(
                id = 1,
                createdBy = vppId,
                documents = emptyList(),
                tasks = listOf(
                    HomeworkTask(id = 1, content = "Task 1", done = false),
                    HomeworkTask(id = 2, content = "Task 2", done = true),
                    HomeworkTask(id = 3, content = "Task 3", done = false)
                ),
                profile = profile,
                classes = clazz,
                createdAt = ZonedDateTime.now(),
                until = ZonedDateTime.now().plusDays(1),
                defaultLesson = DefaultLesson(defaultLessonId = UUID.randomUUID(), `class` = clazz, vpId = 1, subject = "DEU", teacher = teacher),
                isHidden = false,
                isPublic = true
            )
        )
    )
}