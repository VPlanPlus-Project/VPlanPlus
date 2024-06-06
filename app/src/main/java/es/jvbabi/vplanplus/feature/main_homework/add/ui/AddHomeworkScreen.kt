package es.jvbabi.vplanplus.feature.main_homework.add.ui

import android.content.Context
import android.util.DisplayMetrics
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.StoreSaveModal
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.SelectDialog
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeworkSheet(
    navHostController: NavHostController,
    viewModel: AddHomeworkViewModel = hiltViewModel(),
    vpId: Long? = null,
    onDismissRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val state = viewModel.state.value
    LaunchedEffect(vpId, state.initDone) {
        if (vpId == null) return@LaunchedEffect
        viewModel.setDefaultLesson(state.defaultLessons.firstOrNull { it.vpId == vpId })
    }

    LaunchedEffect(key1 = state.result) {
        if (
            state.result == HomeworkModificationResult.SUCCESS_OFFLINE ||
            state.result == HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        ) {
            scope.launch { sheetState.hide() }
        }
    }

    LaunchedEffect(key1 = Unit) {
        sheetState.show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
    ) {
        AddHomeworkContent(
            onBack = { scope.launch { sheetState.hide() } },
            onOpenDefaultLessonDialog = { viewModel.setLessonDialogOpen(true) },
            onCloseDefaultLessonDialog = { viewModel.setLessonDialogOpen(false) },
            onOpenDateDialog = { viewModel.setUntilDialogOpen(true) },
            onCloseDateDialog = { viewModel.setUntilDialogOpen(false) },
            onSetDefaultLesson = { viewModel.setDefaultLesson(it) },
            onSetDate = { viewModel.setUntil(it) },
            onOpenVppIdSettings = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
            onSave = viewModel::requestSave,
            onAction = viewModel::onUiAction,
            state = state
        )
    }
}

@Composable
@Deprecated("Use AddHomeworkSheet instead")
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
        onOpenVppIdSettings = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
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
    onOpenVppIdSettings: () -> Unit = {},
    onSave: () -> Unit = {},
    onAction: (action: AddHomeworkUiEvent) -> Unit = { _ -> },
    state: AddHomeworkState
) {
    var isSaveTypeSheetOpen by rememberSaveable { mutableStateOf(false) }
    val saveTypeSheetState = rememberModalBottomSheetState(true)
    if (isSaveTypeSheetOpen) StoreSaveModal(
        canUseVppId = state.canUseCloud,
        allowNoVppIdBanner = state.canShowCloudInfoBanner,
        sheetState = saveTypeSheetState,
        currentState = state.saveType ?: SaveType.LOCAL,
        onSubmit = { onAction(UpdateSaveType(it)) },
        onDismissRequest = { isSaveTypeSheetOpen = false },
        onOpenVppIdSettings = onOpenVppIdSettings,
        onHideBannerForever = { onAction(HideNoVppIdBanner) }
    )

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
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(TopAppBarDefaults.topAppBarColors().containerColor), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                    .width(32.dp)
                    .height(6.dp)
                )
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
        },
        modifier = Modifier.imePadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            val colorScheme = MaterialTheme.colorScheme
            val focusRequester = remember { FocusRequester() }
            var taskIndexToFocus: Int? by remember { mutableStateOf(null) }
            state.tasks.forEachIndexed { i, task ->
                var selection: TextRange by remember { mutableStateOf(TextRange.Zero) }
                var textFieldValueState by remember(i, task, selection) { mutableStateOf(TextFieldValue(text = task, selection = selection)) }

                TextField(
                    value = textFieldValueState,
                    onValueChange = {
                        selection = it.selection
                        onAction(UpdateTask(i, it.text))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (i == taskIndexToFocus) Modifier.focusRequester(focusRequester) else Modifier),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = { Text(text = "Aufgabe $i") },
                    leadingIcon = {
                        Box(
                            Modifier
                                .drawWithContent {
                                    drawCircle(
                                        color = colorScheme.outline,
                                        center = center,
                                        radius = 10.dp.toPx()
                                    )
                                    drawCircle(
                                        color = colorScheme.background,
                                        center = center,
                                        radius = 8.dp.toPx()
                                    )
                                }
                        ) {}
                    },
                    trailingIcon = {
                        IconButton(onClick = { onAction(DeleteTask(i)) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                )
                LaunchedEffect(key1 = taskIndexToFocus) {
                    if (taskIndexToFocus != i) return@LaunchedEffect
                    focusRequester.requestFocus()
                    textFieldValueState = TextFieldValue(text = task, selection = TextRange(1))
                    taskIndexToFocus = null
                }
            }
            TextField(
                value = "",
                onValueChange = {
                    if (it.isBlank()) return@TextField
                    onAction(CreateTask(it))
                    taskIndexToFocus = state.tasks.size
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text(text = "Neue Aufgabe") }, // todo string resource
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                },
            )

            HorizontalDivider()

            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOpenDefaultLessonDialog() }
                    .padding(bottom = 16.dp)
            ) {
                RowVerticalCenter(Modifier.padding(start = 12.dp, top = 16.dp)) {
                    Box(modifier = Modifier.size(24.dp)) icon@{
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null
                        )
                    }
                    Column(Modifier.padding(start = 16.dp, end = 4.dp)) {
                        Text(
                            text = stringResource(id = R.string.addHomework_lesson),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text =
                            if (state.selectedDefaultLesson == null) stringResource(id = R.string.addHomework_notSelected)
                            else if (state.selectedDefaultLesson.teacher == null) stringResource(id = R.string.addHomework_lessonSubtitleNoTeacher, state.selectedDefaultLesson.subject)
                            else stringResource(
                                id = R.string.addHomework_lessonSubtitle,
                                state.selectedDefaultLesson.subject,
                                state.selectedDefaultLesson.teacher.acronym
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

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

            HorizontalDivider()

            Box(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOpenDateDialog() }
            ) {
                RowVerticalCenter(Modifier.padding(start = 12.dp, top = 16.dp, bottom = 16.dp)) {
                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
                    Box(Modifier.padding(start = 8.dp), contentAlignment = Alignment.CenterStart) {
                        var realSize by remember { mutableFloatStateOf(0f) }
                        val sizeText by animateFloatAsState(targetValue = realSize, animationSpec = tween(250), label = "padding row")
                        val context = LocalContext.current
                        Column(
                            Modifier
                                .padding(start = 8.dp, end = 4.dp)
                                .onSizeChanged {
                                    realSize = it.width
                                        .toFloat()
                                        .pxToDp(context)
                                }
                        ) {
                            Text(
                                text = stringResource(id = R.string.addHomework_until),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = if (state.until == null) stringResource(id = R.string.addHomework_notSelected) else state.until.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        LazyRow {
                            items(4) { i ->
                                val date = LocalDate.now().plusDays(i + 1L)
                                Box(
                                    modifier = Modifier
                                        .then(if (i == 0) Modifier.padding(start = 16.dp + sizeText.dp) else Modifier)
                                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    DateChip(
                                        date = date,
                                        selected = state.until == date,
                                    ) { onSetDate(date) }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { isSaveTypeSheetOpen = true }
            ) {
                RowVerticalCenter(Modifier.padding(start = 12.dp, top = 16.dp, bottom = 16.dp)) {
                    Box(modifier = Modifier.size(24.dp)) icon@{
                        Icon(
                            imageVector = when (state.saveType) {
                                SaveType.LOCAL -> Icons.Default.PhoneAndroid
                                SaveType.CLOUD -> Icons.Default.CloudQueue
                                SaveType.SHARED -> Icons.Default.Share
                                null -> return@icon
                            },
                            contentDescription = null
                        )
                    }
                    Column(Modifier.padding(start = 16.dp, end = 4.dp)) {
                        Text(
                            text = stringResource(id = R.string.addHomework_storeTitle),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text =
                            when (state.saveType) {
                                SaveType.LOCAL -> stringResource(id = R.string.addHomework_storeOnThisDevice)
                                SaveType.CLOUD -> stringResource(id = R.string.addHomework_storeInCloud)
                                SaveType.SHARED -> stringResource(id = R.string.addHomework_storeInCloud) + " $DOT " + stringResource(id = R.string.addHomework_shareWithClass)
                                null -> ""
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
//
//            return@Column
//
//        AnimatedVisibility(
//            visible = state.result == HomeworkModificationResult.FAILED,
//            enter = expandVertically(tween(200)),
//            exit = shrinkVertically(tween(200))
//        ) {
//            InfoCard(
//                modifier = Modifier.padding(16.dp),
//                imageVector = Icons.Default.Error,
//                title = stringResource(id = R.string.something_went_wrong),
//                text =
//                stringResource(id = R.string.addHomework_saveFailedText) +
//                        if (state.canUseCloud) " " + stringResource(id = R.string.addHomework_saveFailedOnlineText)
//                        else "",
//            )}}
//        }
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

fun Float.pxToDp(context: Context): Float =
    (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))