package es.jvbabi.vplanplus.feature.ndp.ui.guided

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.DataType
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTaskDone
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.TeacherPreview
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import es.jvbabi.vplanplus.util.lerp
import es.jvbabi.vplanplus.util.toFloat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

@Composable
fun NdpHostScreen(
    navHostController: NavHostController,
    viewModel: NdpHostViewModel = hiltViewModel()
) {
    NdpHostScreenContent(
        state = viewModel.state,
        doAction = viewModel::doAction
    )
}

@Composable
private fun NdpHostScreenContent(
    state: NdpHostState,
    doAction: (NdpEvent) -> Unit
) {
    val pagerState = rememberPagerState(state.displayStage.ordinal) { state.currentStage.ordinal + 1 }
    LaunchedEffect(state.displayStage) {
        pagerState.animateScrollToPage(state.displayStage.ordinal)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeContent.asPaddingValues())
            .padding(top = 8.dp)
    ) {
        if (state.nextSchoolDay == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Column
        }

        RowVerticalCenter(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            NdpBar(subsegments = 1, progress = (state.currentStage.ordinal > NdpStage.START.ordinal).toFloat())
            if (state.nextSchoolDay.homework.isNotEmpty()) NdpBar(subsegments = state.nextSchoolDay.homework.size, progress = .8f)
            NdpBar(subsegments = 2, progress = 0f)
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            pageSize = PageSize.Fill,
            state = pagerState
        ) { stageOrdinal ->
            val stage = NdpStage.entries[stageOrdinal]
            when (stage) {
                NdpStage.START -> {
                    NdpStartScreenContent(
                        date = state.nextSchoolDay.date,
                        enabled = state.currentStage == NdpStage.START,
                    ) { doAction(NdpEvent.Start) }
                }
                NdpStage.HOMEWORK -> {
                    NdpHomeworkScreen(
                        homework = state.nextSchoolDay.homework
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.NdpBar(
    subsegments: Int,
    progress: Float
) {
    val displayProgress by animateFloatAsState(progress, label = "NdpBarProgress")
    RowVerticalCenter(
        modifier = Modifier
            .weight(1f, true)
            .clip(RoundedCornerShape(50)),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(subsegments) { segment ->
            val segmentProgress = displayProgress * subsegments
            Box(
                modifier = Modifier
                    .weight(1f, true)
                    .height(4.dp)
                    .clip(RoundedCornerShape(25))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(lerp(0f, 1f, segmentProgress - segment))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Preview
@Composable
private fun NdpHostScreenPreview() {
    val school = SchoolPreview.generateRandomSchool()
    val group = GroupPreview.generateGroup(school)
    val profile = ProfilePreview.generateClassProfile(group)
    NdpHostScreenContent(
        state = NdpHostState(
            nextSchoolDay = SchoolDay(
                date = LocalDate.now().atStartOfWeek().plusDays(7),
                type = DayType.NORMAL,
                version = 1,
                info = null,
                dataType = DataType.SUBSTITUTION_PLAN,
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
                            until = LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()),
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
                    PersonalizedHomework.LocalHomework(
                        homework = HomeworkCore.LocalHomework(
                            id = -2,
                            defaultLesson = DefaultLesson(
                                defaultLessonId = UUID.randomUUID(),
                                vpId = 2,
                                subject = "MA",
                                teacher = TeacherPreview.teacher(school),
                                `class` = group,
                                courseGroup = null
                            ),
                            createdAt = ZonedDateTime.now(),
                            documents = emptyList(),
                            tasks = emptyList(),
                            until = LocalDate.now().atStartOfDay().plusDays(7).atZone(ZoneId.systemDefault()),
                            profile = profile
                        ),
                        profile = profile,
                        tasks = listOf(
                            HomeworkTaskDone(
                                id = -3,
                                homeworkId = -2,
                                content = "Task 2A",
                                isDone = true
                            ),
                            HomeworkTaskDone(
                                id = -4,
                                homeworkId = -2,
                                content = "Task 2B",
                                isDone = false
                            )
                        )
                    )
                ),
                exams = emptyList(),
                grades = emptyList(),
                lessons = emptyList(),
            ),
            currentStage = NdpStage.HOMEWORK,
            displayStage = NdpStage.HOMEWORK
        ),
        doAction = {}
    )
}