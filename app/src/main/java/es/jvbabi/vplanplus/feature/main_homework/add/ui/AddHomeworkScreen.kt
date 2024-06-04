package es.jvbabi.vplanplus.feature.main_homework.add.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.DateChip
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.SelectDialog
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddHomeworkScreen(
    navHostController: NavHostController,
    viewModel: AddHomeworkViewModel = hiltViewModel(),
    vpId: Long? = null
) {

    val state = viewModel.state.value
    LaunchedEffect(vpId, state.initDone) {
        if (vpId == null) return@LaunchedEffect
        viewModel.setDefaultLesson(state.defaultLessons.firstOrNull { it.vpId == vpId })
    }
    AddHomeworkContent(
        onBack = { navHostController.popBackStack() },
        onOpenDefaultLessonDialog = { viewModel.setLessonDialogOpen(true) },
        onCloseDefaultLessonDialog = { viewModel.setLessonDialogOpen(false) },
        onOpenDateDialog = { viewModel.setUntilDialogOpen(true) },
        onCloseDateDialog = { viewModel.setUntilDialogOpen(false) },
        onSetDefaultLesson = { viewModel.setDefaultLesson(it) },
        onSetDate = { viewModel.setUntil(it) },
        onToggleForAll = { viewModel.toggleForAll() },
        onChangeNewTask = { viewModel.setNewTask(it) },
        onAddTask = { viewModel.addTask() },
        onModifyTask = { before, after -> viewModel.modifyTask(before, after) },
        onHideBannerForever = { viewModel.hideCloudInfoBanner() },
        onOpenVppIdSettings = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
        onToggleStoreInCloud = viewModel::onToggleCloud,
        onSave = viewModel::requestSave,
        onAction = viewModel::onUiAction,
        state = state
    )

    LaunchedEffect(key1 = state.result) {
        if (
            state.result == HomeworkModificationResult.SUCCESS_OFFLINE ||
            state.result == HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        ) {
            navHostController.popBackStack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHomeworkContent(
    onBack: () -> Unit = {},
    onOpenDefaultLessonDialog: () -> Unit = {},
    onCloseDefaultLessonDialog: () -> Unit = {},
    onSetDefaultLesson: (DefaultLesson?) -> Unit = {},
    onOpenDateDialog: () -> Unit = {},
    onCloseDateDialog: () -> Unit = {},
    onSetDate: (LocalDate?) -> Unit = {},
    onToggleForAll: () -> Unit = {},
    onChangeNewTask: (String) -> Unit = {},
    onAddTask: () -> Unit = {},
    onModifyTask: (before: String, after: String) -> Unit = { _, _ -> },
    onHideBannerForever: () -> Unit = {},
    onOpenVppIdSettings: () -> Unit = {},
    onToggleStoreInCloud: () -> Unit = {},
    onSave: () -> Unit = {},
    onAction: (action: AddHomeworkUiEvent) -> Unit = { _ -> },
    state: AddHomeworkState
) {

    val noTeacher = stringResource(id = R.string.settings_profileDefaultLessonNoTeacher)
    if (state.isLessonDialogOpen) {
        SelectDialog(
            icon = Icons.Default.School,
            message =
                if (state.defaultLessonsFiltered) stringResource(id = R.string.addHomework_defaultLessonFilteredMessage)
                else null,
            title = stringResource(id = R.string.addHomework_defaultLessonTitle),
            items = state.defaultLessons.sortedBy { it.subject },
            value = state.selectedDefaultLesson,
            itemToString = { it.subject + " $DOT " + (it.teacher?.acronym ?: noTeacher) },
            onDismiss = onCloseDefaultLessonDialog,
            onOk = { onSetDefaultLesson(it) }
        )
    }
    if (state.isUntilDialogOpen) {
        val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = DateUtils.getDateFromTimestamp(utcTimeMillis / 1000)
                return date.isAfter(LocalDate.now())
            }
        })

        DatePickerDialog(
            onDismissRequest = onCloseDateDialog,
            confirmButton = {
                TextButton(onClick = {
                    val date =
                        if (datePickerState.selectedDateMillis == null) null
                        else DateUtils.getDateFromTimestamp(datePickerState.selectedDateMillis!! / 1000)
                    onSetDate(date)
                }) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseDateDialog) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.home_addHomeworkLabel))
                        if (state.username != null) Text(
                            text = state.username,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close)
                        )
                    }
                },
                actions = {
                    val colorScheme = MaterialTheme.colorScheme
                    Balloon(
                        builder = rememberBalloonBuilder {
                            setArrowSize(10)
                            setArrowPosition(0.5f)
                            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                            setWidth(BalloonSizeSpec.WRAP)
                            setHeight(BalloonSizeSpec.WRAP)
                            setPadding(12)
                            setMarginHorizontal(12)
                            setCornerRadius(16f)
                            setBackgroundColor(colorScheme.primaryContainer)
                            setBalloonAnimation(BalloonAnimation.FADE)
                        },
                        balloonContent = {
                            Text(text = stringResource(id = R.string.addHomework_newDesignInfo), color = colorScheme.onPrimaryContainer)
                        }
                    ) { balloonWindow ->
                        LaunchedEffect(key1 = state.showNewSaveButtonLocationBalloon, key2 = state.canSubmit) {
                            if (state.showNewSaveButtonLocationBalloon && state.canSubmit) {
                                balloonWindow.showAlignBottom()
                                onAction(NewLayoutBalloonDismissed)
                            }
                        }
                        TextButton(
                            onClick = onSave,
                            enabled = state.canSubmit
                        ) {
                            Text(text = stringResource(id = R.string.save))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            val withoutTeacher = stringResource(
                id = R.string.addHomework_lessonSubtitleNoTeacher,
                state.selectedDefaultLesson?.subject ?: ""
            )
            SettingsCategory(
                title = stringResource(id = R.string.addHomework_general),
            ) {
                SettingsSetting(
                    icon = Icons.Default.School,
                    title = stringResource(id = R.string.addHomework_lesson),
                    subtitle =
                    if (state.selectedDefaultLesson == null) stringResource(id = R.string.addHomework_notSelected)
                    else if (state.selectedDefaultLesson.teacher == null) withoutTeacher
                    else stringResource(
                        id = R.string.addHomework_lessonSubtitle,
                        state.selectedDefaultLesson.subject,
                        state.selectedDefaultLesson.teacher.acronym
                    ),
                    type = SettingsType.SELECT,
                    doAction = onOpenDefaultLessonDialog,
                    customContent = {
                        AnimatedVisibility(
                            visible = state.selectedDefaultLesson != null,
                            enter = expandVertically(tween(250)),
                            exit = shrinkVertically(tween(250))
                        ) {
                            AssistChip(
                                modifier = Modifier.padding(start = 56.dp),
                                onClick = { onSetDefaultLesson(null) },
                                label = { Text(text = stringResource(id = R.string.addHomework_removeSubject)) },
                                leadingIcon = { Icon(imageVector = Icons.Default.Cancel, contentDescription = null) }
                            )
                        }
                    }
                )
                SettingsSetting(
                    icon = Icons.Default.AccessTime,
                    title = stringResource(id = R.string.addHomework_until),
                    subtitle =
                    if (state.until == null) stringResource(id = R.string.addHomework_notSelected)
                    else state.until.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")),
                    type = SettingsType.SELECT,
                    doAction = onOpenDateDialog,
                    customContent = {
                        LazyRow {
                            items(4) { i ->
                                val date = LocalDate.now().plusDays(i + 1L)
                                Box(
                                    modifier = if (i == 0) Modifier.padding(start = 24.dp) else Modifier
                                ) {
                                    DateChip(
                                        date = date,
                                        selected = state.until == date,
                                    ) { onSetDate(date) }
                                }
                            }
                        }
                    }
                )

                if (state.canUseCloud) SettingsSetting(
                    icon = Icons.Default.CloudQueue,
                    title = stringResource(id = R.string.addHomework_storeInCloudTitle),
                    subtitle = stringResource(id = R.string.addHomework_storeInCloudText),
                    checked = state.storeInCloud,
                    type = SettingsType.TOGGLE,
                    doAction = onToggleStoreInCloud
                )

                SettingsSetting(
                    icon = Icons.Default.People,
                    title = stringResource(id = R.string.addHomework_shareTitle),
                    subtitle =
                    if (!state.canUseCloud)
                        stringResource(id = R.string.addHomework_shareSubtitleOnlyLocal)
                    else if (state.selectedDefaultLesson?.teacher != null)
                        stringResource(
                            id = R.string.addHomework_shareSubtitleWithSubjectAndTeacher,
                            state.selectedDefaultLesson.subject,
                            state.selectedDefaultLesson.teacher.acronym
                        )
                    else if (state.selectedDefaultLesson != null)
                        stringResource(
                            id = R.string.addHomework_shareSubtitleWithSubject,
                            state.selectedDefaultLesson.subject
                        )
                    else
                        stringResource(id = R.string.addHomework_shareSubtitleWithoutSubject),
                    type = SettingsType.CHECKBOX,
                    enabled = state.canUseCloud && state.storeInCloud,
                    checked = state.isForAll && state.canUseCloud,
                    doAction = onToggleForAll
                )
            }

            AnimatedVisibility(
                visible = !state.canUseCloud && state.canShowCloudInfoBanner,
                enter = expandVertically(tween(200)),
                exit = shrinkVertically(tween(200))
            ) {
                InfoCard(
                    imageVector = Icons.Default.NoAccounts,
                    title = stringResource(id = R.string.addHomework_noVppIdTitle),
                    text = stringResource(id = R.string.addHomework_noVppIdText),
                    modifier = Modifier.padding(16.dp),
                    buttonText1 = stringResource(id = R.string.hideForever),
                    buttonAction1 = onHideBannerForever,
                    buttonText2 = stringResource(id = R.string.addHomework_noVppIdButtonOpenSettings),
                    buttonAction2 = onOpenVppIdSettings
                )
            }

            SettingsCategory(
                title = stringResource(id = R.string.addHomework_tasks),
            ) {
                state.tasks.forEach { task ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = task,
                            onValueChange = { onModifyTask(task, it) },
                            placeholder = {
                                Text(stringResource(id = R.string.addHomework_newTask))
                            },
                            modifier = Modifier.weight(1f, true)
                        )
                        IconButton(
                            onClick = { onModifyTask(task, "") },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.delete),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.newTask,
                        onValueChange = onChangeNewTask,
                        placeholder = {
                            Text(stringResource(id = R.string.addHomework_newTask))
                        },
                        modifier = Modifier.weight(1f, true)
                    )
                    IconButton(
                        onClick = onAddTask,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = state.result == HomeworkModificationResult.FAILED,
                enter = expandVertically(tween(200)),
                exit = shrinkVertically(tween(200))
            ) {
                InfoCard(
                    modifier = Modifier.padding(16.dp),
                    imageVector = Icons.Default.Error,
                    title = stringResource(id = R.string.something_went_wrong),
                    text =
                    stringResource(id = R.string.addHomework_saveFailedText) +
                            if (state.canUseCloud) " " + stringResource(id = R.string.addHomework_saveFailedOnlineText)
                            else "",
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AddHomeworkScreenPreview() {
    AddHomeworkContent(
        state = AddHomeworkState(
            username = "John Doe",
            isLessonDialogOpen = false,
            isUntilDialogOpen = false,
            tasks = listOf("Task 1", "Task 2", "Task 3"),
            canUseCloud = true,
            canShowCloudInfoBanner = true
        )
    )
}