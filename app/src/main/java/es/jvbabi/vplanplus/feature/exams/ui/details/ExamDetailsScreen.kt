package es.jvbabi.vplanplus.feature.exams.ui.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamType
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer12Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.stringResource
import es.jvbabi.vplanplus.util.formatDayDuration
import es.jvbabi.vplanplus.util.runComposable
import es.jvbabi.vplanplus.util.toDp
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

@Composable
fun ExamDetailsScreen(
    viewModel: ExamDetailsViewModel = hiltViewModel(),
    navHostController: NavHostController,
    examId: Int
) {
    val state = viewModel.state

    LaunchedEffect(examId) {
        viewModel.init(examId)
    }

    ExamDetailsContent(
        state = state,
        onBack = remember { { navHostController.navigateUp()} }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamDetailsContent(
    state: ExamDetailsState,
    onBack: () -> Unit = {},
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    AnimatedContent(
                        targetState = state.exam,
                        contentKey = { it?.hashCode() },
                        transitionSpec = { fadeIn(animationSpec = tween()) togetherWith fadeOut(animationSpec = tween()) },
                        label = "exam_title",
                        modifier = Modifier.fillMaxWidth()
                    ) { exam ->
                        if (exam == null) {
                            CircularProgressIndicator(Modifier.size(24.dp))
                            return@AnimatedContent
                        }
                        Column {
                            Text(
                                text = exam.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(R.string.examsDetails_date, exam.date.format(DateTimeFormatter.ofPattern("EEEE, dd. MMMM yyyy")), exam.date.formatDayDuration(LocalDate.now(), false)),
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onBack) { BackIcon() }
                },
                actions = {
                    if (state.isUserAllowedToEdit) IconButton(onClick = {}) { Icon(Icons.Default.Edit, null) }
                    if (state.isUserAllowedToEdit) IconButton(onClick = {}) { Icon(Icons.Default.Delete, null) }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.exam,
            contentKey = { it?.hashCode() },
            transitionSpec = { fadeIn(animationSpec = tween()) togetherWith fadeOut(animationSpec = tween()) },
            label = "exam"
        ) { exam ->
            if (exam == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@AnimatedContent
            }
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                LocalDate.now().until(exam.date, ChronoUnit.DAYS).let { daysLeft ->
                    if (daysLeft > 4) return@let
                    InfoCard(
                        imageVector = Icons.Default.Warning,
                        title = stringResource(id = R.string.examsDetails_warning),
                        text = stringResource(id = R.string.examsDetails_warningText, daysLeft),
                        buttonText1 = stringResource(id = R.string.examsDetails_imReadyButton),
                        buttonAction1 = {}
                    )
                    Spacer12Dp()
                }

                runComposable type@{
                    RowVerticalCenter {
                        Icon(Icons.Default.Category, null, modifier = Modifier.size(MaterialTheme.typography.labelLarge.lineHeight.toDp()))
                        Spacer4Dp()
                        Text(
                            text = stringResource(R.string.examsDetails_type),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer4Dp()
                    Column {
                        Text(text = stringResource(exam.type.stringResource()))
                    }

                    Spacer12Dp()
                }

                runComposable description@{
                    RowVerticalCenter {
                        Icon(Icons.AutoMirrored.Default.ShortText, null, modifier = Modifier.size(MaterialTheme.typography.labelLarge.lineHeight.toDp()))
                        Spacer4Dp()
                        Text(
                            text = stringResource(R.string.examsDetails_description),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer4Dp()
                    Column {
                        Text(text = exam.description ?: "")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ExamDetailsScreenPreview() {
    val school = SchoolPreview.generateRandomSchool()
    val group = GroupPreview.generateGroup(school)
    val profile = ProfilePreview.generateClassProfile(group)
    ExamDetailsContent(
        state = ExamDetailsState(
            exam = Exam(
                id = -1,
                type = ExamType.PROJECT,
                date = LocalDate.now().plusDays(1),
                title = "Example",
                description = "Example description",
                createdBy = null,
                group = group,
                createdAt = ZonedDateTime.now().minusDays(3L),
                subject = DefaultLesson(UUID.randomUUID(), 1, "DEU", null, group, null)
            ),
            currentProfile = profile
        )
    )
}