package es.jvbabi.vplanplus.feature.exams.ui.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
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
import es.jvbabi.vplanplus.feature.exams.ui.details.components.DateSelectorSheet
import es.jvbabi.vplanplus.feature.exams.ui.details.components.TypeSelectorSheet
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer12Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.noRippleClickable
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.stringResource
import es.jvbabi.vplanplus.util.formatDayDuration
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
        onBack = remember { { navHostController.navigateUp() } },
        doAction = viewModel::doAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamDetailsContent(
    state: ExamDetailsState,
    onBack: () -> Unit = {},
    doAction: (action: ExamDetailsEvent) -> Unit = {},
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var showTypeSelector by rememberSaveable { mutableStateOf(false) }
    val typeSelectorSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (showTypeSelector) TypeSelectorSheet(
        currentType = state.editModeType ?: state.exam?.type ?: ExamType.Project,
        sheetState = typeSelectorSheetState,
        onTypeSelected = { doAction(ExamDetailsEvent.UpdateType(it)) },
        onDismiss = { showTypeSelector = false }
    )

    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    val datePickerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (showDatePickerDialog) DateSelectorSheet(
        selectedDate = state.exam?.date ?: LocalDate.now(),
        sheetState = datePickerSheetState,
        onDismiss = { showDatePickerDialog = false },
        onSetDate = { doAction(ExamDetailsEvent.UpdateDate(it)) }
    )

    LaunchedEffect(key1 = state.editModeUpdatingTitleState) {
        if (state.editModeUpdatingTitleState != UpdatingState.DONE || !state.canUndoTitleUpdate) return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = context.getString(R.string.examsDetails_titleUpdated),
            withDismissAction = true,
            actionLabel = context.getString(R.string.undo),
            duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) doAction(ExamDetailsEvent.UndoTitleUpdate)
    }

    LaunchedEffect(key1 = state.editModeUpdatingTypeState) {
        if (state.editModeUpdatingTypeState != UpdatingState.DONE || !state.canUndoTypeUpdate) return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = context.getString(R.string.examsDetails_typeUpdated),
            withDismissAction = true,
            actionLabel = context.getString(R.string.undo),
            duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) doAction(ExamDetailsEvent.UndoTypeUpdate)
    }

    LaunchedEffect(key1 = state.editModeUpdatingDescriptionState) {
        if (state.editModeUpdatingDescriptionState != UpdatingState.DONE || !state.canUndoDescriptionUpdate) return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = context.getString(R.string.examsDetails_descriptionUpdated),
            withDismissAction = true,
            actionLabel = context.getString(R.string.undo),
            duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) doAction(ExamDetailsEvent.UndoDescriptionUpdate)
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    AnimatedContent(
                        targetState = state.exam,
                        contentKey = { it?.hashCode() },
                        transitionSpec = {
                            fadeIn(animationSpec = tween()) togetherWith fadeOut(
                                animationSpec = tween()
                            )
                        },
                        label = "exam_title",
                        modifier = Modifier.fillMaxWidth()
                    ) { exam ->
                        if (exam == null) {
                            CircularProgressIndicator(Modifier.size(24.dp))
                            return@AnimatedContent
                        }
                        Column {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(end = 16.dp)) {
                                AnimatedContent(
                                    targetState = state.editModeUpdatingTitleState,
                                    label = "is editing",
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .alpha(1-scrollBehavior.state.collapsedFraction)
                                ) { updatingState ->
                                    when (updatingState) {
                                        UpdatingState.UPDATING -> CircularProgressIndicator(
                                            Modifier.size(
                                                24.dp
                                            )
                                        )

                                        UpdatingState.DONE -> Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )

                                        else -> Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                }
                                BasicTextField(
                                    value = state.editModeTitle ?: exam.title,
                                    onValueChange = { doAction(ExamDetailsEvent.UpdateTitle(it)) },
                                    singleLine = true,
                                    readOnly = !state.isUserAllowedToEdit || state.editModeUpdatingTitleState == UpdatingState.UPDATING,
                                    enabled = scrollBehavior.state.collapsedFraction == 0f && state.isUserAllowedToEdit,
                                    textStyle = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface),
                                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                                )
                            }
                            RowVerticalCenter(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = (32 * (1-scrollBehavior.state.collapsedFraction)).dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(scrollBehavior.state.collapsedFraction == 0f) { showDatePickerDialog = true }
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    text = stringResource(
                                        R.string.examsDetails_date,
                                        (state.editModeDate ?: exam.date).format(DateTimeFormatter.ofPattern("EEEE, dd. MMMM yyyy")),
                                        LocalDate.now().formatDayDuration((state.editModeDate ?: exam.date), false)
                                    ),
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                AnimatedContent(
                                    targetState = state.editModeUpdatingDateState,
                                    label = "is editing",
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .alpha(1-scrollBehavior.state.collapsedFraction)
                                ) { updatingState ->
                                    when (updatingState) {
                                        UpdatingState.UPDATING -> CircularProgressIndicator(
                                            Modifier.size(
                                                16.dp
                                            )
                                        )

                                        UpdatingState.DONE -> Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.Gray
                                        )

                                        else -> Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                }

                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onBack) { BackIcon() }
                },
                actions = {
                    if (state.isUserAllowedToEdit) IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Delete,
                            null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    .padding(horizontal = 8.dp)
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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showTypeSelector = true }
                        .padding(8.dp)
                ) type@{
                    Column {
                        RowVerticalCenter {
                            Icon(
                                Icons.Default.Category,
                                null,
                                modifier = Modifier.size(MaterialTheme.typography.labelLarge.lineHeight.toDp())
                            )
                            Spacer4Dp()
                            Text(
                                text = stringResource(R.string.examsDetails_type),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer4Dp()
                        Column {
                            Text(
                                text = stringResource(
                                    (state.editModeType ?: exam.type).stringResource()
                                )
                            )
                        }
                    }

                    AnimatedContent(
                        targetState = state.editModeUpdatingTypeState,
                        label = "is editing",
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) { updatingState ->
                        when (updatingState) {
                            UpdatingState.UPDATING -> CircularProgressIndicator(Modifier.size(24.dp))
                            UpdatingState.DONE -> Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )

                            else -> Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }

                val descriptionFocusRequester = remember { FocusRequester() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .noRippleClickable { descriptionFocusRequester.requestFocus() }
                        .padding(8.dp)
                ) description@{
                    Column {
                        RowVerticalCenter {
                            Icon(
                                Icons.AutoMirrored.Default.ShortText,
                                null,
                                modifier = Modifier.size(MaterialTheme.typography.labelLarge.lineHeight.toDp())
                            )
                            Spacer4Dp()
                            Text(
                                text = stringResource(R.string.examsDetails_description),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer4Dp()
                        Box {
                            if (state.editModeDescription == null && exam.description == null) Text(
                                text = stringResource(R.string.examsDetails_descriptionEmpty),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            BasicTextField(
                                value = state.editModeDescription ?: exam.description ?: "",
                                readOnly = !state.isUserAllowedToEdit,
                                onValueChange = { doAction(ExamDetailsEvent.UpdateDescription(it)) },
                                singleLine = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(descriptionFocusRequester),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (exam.description == null && state.editModeDescription == null) Color.Gray else MaterialTheme.colorScheme.onSurface
                                ),
                            )
                        }
                    }

                    AnimatedContent(
                        targetState = state.editModeUpdatingDescriptionState,
                        label = "is editing",
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) { updatingState ->
                        when (updatingState) {
                            UpdatingState.UPDATING -> CircularProgressIndicator(Modifier.size(24.dp))
                            UpdatingState.DONE -> Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )

                            else -> Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                        }
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
                type = ExamType.Project,
                date = LocalDate.now().plusDays(1),
                title = "Example",
                description = null,
                createdBy = null,
                group = group,
                createdAt = ZonedDateTime.now().minusDays(3L),
                subject = DefaultLesson(UUID.randomUUID(), 1, "DEU", null, group, null)
            ),
            currentProfile = profile
        )
    )
}