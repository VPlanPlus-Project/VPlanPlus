package es.jvbabi.vplanplus.feature.main_homework.add.ui

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.DocumentRecord
import es.jvbabi.vplanplus.ui.common.BasicInputField
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.util.DateUtils.getRelativeStringResource
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
@Preview(showBackground = true)
fun AddHomeworkSheetContent(
    tasks: List<String> = emptyList(),
    onAddTask: (task: String) -> Unit = {},
    onModifyTask: (index: Int, task: String) -> Unit = { _, _ -> },
    onRemoveTask: (index: Int) -> Unit = {},

    until: LocalDate? = null,
    onUntilClicked: () -> Unit = {},

    selectedDefaultLesson: DefaultLesson? = null,
    onSelectDefaultLessonClicked: () -> Unit = {},

    canUseCloud: Boolean = true,
    saveType: SaveType = SaveType.SHARED,
    onSaveTypeClicked: () -> Unit = {},

    documents: Map<Uri, HomeworkDocumentType> = emptyMap(),
    onAddDocumentClicked: () -> Unit = {},
    onAddPhotoClicked: () -> Unit = {},
    onDeleteDocumentClicked: (uri: Uri) -> Unit = {},

    canSave: Boolean = false,
    isLoading: Boolean = false,
    onSave: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) tasks@{
            for (i in 0..tasks.size) {
                BasicInputField(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    value = tasks.getOrElse(i) { "" },
                    onValueChange = {
                        if (it.isBlank()) return@BasicInputField
                        if (tasks.getOrNull(i) == null) onAddTask(it)
                        else onModifyTask(i, it)
                    },
                    placeholder = { Text(text = stringResource(id = R.string.homework_addTask)) },
                    trailingIcon = {
                        if (tasks.getOrNull(i) != null) IconButton(onClick = { onRemoveTask(i) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    focusRequester = if (i == 0 && tasks.isEmpty()) focusRequester else null
                )
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            item { Spacer4Dp() }
            item {
                FilterChip(
                    selected = until != null && until.isAfter(LocalDate.now().minusDays(1L)),
                    onClick = onUntilClicked,
                    label = {
                        Text(text = if (until == null) stringResource(id = R.string.addHomework_until) else stringResource(id = R.string.homework_dueTo, until.getRelativeStringResource().run {
                            if (this == null) return@run until.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                            return@run stringResource(id = this)
                        }))
                    },
                    leadingIcon = { Icon(imageVector = Icons.Default.AccessTime, contentDescription = null) },
                    trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null) }
                )
            }
            item {
                FilterChip(
                    selected = selectedDefaultLesson != null,
                    onClick = onSelectDefaultLessonClicked,
                    label = { Text(text = selectedDefaultLesson?.subject ?: stringResource(id = R.string.addHomework_lesson)) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Tag, contentDescription = null) },
                    trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null) }
                )
            }
            if (canUseCloud) {
                item { VerticalDivider(Modifier.height(32.dp)) }
                item {
                    AssistChip(
                        onClick = onSaveTypeClicked,
                        label = { Text(text = when (saveType) {
                            SaveType.LOCAL -> stringResource(id = R.string.addHomework_saveThisDevice)
                            SaveType.CLOUD -> stringResource(id = R.string.addHomework_saveVppId)
                            SaveType.SHARED -> stringResource(id = R.string.addHomework_saveVppIdSharedTitle)
                        }) },
                        leadingIcon = {
                            Icon(imageVector = when (saveType) {
                                SaveType.LOCAL -> Icons.Default.PhoneAndroid
                                SaveType.CLOUD -> Icons.Default.CloudQueue
                                SaveType.SHARED -> Icons.Default.Share
                            }, contentDescription = null)
                        },
                        trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null) }
                    )
                }
            }
        }

        RowVerticalCenterSpaceBetweenFill(modifier = Modifier.padding(8.dp)) {
            RowVerticalCenter {
                IconButton(onClick = onAddDocumentClicked) {
                    Icon(imageVector = Icons.Outlined.FileOpen, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                }
                IconButton(onClick = onAddPhotoClicked) {
                    Icon(imageVector = Icons.Outlined.AddPhotoAlternate, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                }
            }
            if (documents.isEmpty()) SaveButton(canSave = canSave, isLoading = isLoading, onSave = onSave)
        }
        Column {
            documents.entries.forEach { (uri, type) ->
                DocumentRecord(
                    uri = uri,
                    type = type,
                    isEditing = true,
                    onRemove = { onDeleteDocumentClicked(uri) }
                )
            }
        }
        if (documents.isNotEmpty()) Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            SaveButton(canSave = canSave, isLoading = isLoading, onSave = onSave)
        }
    }

    LaunchedEffect(key1 = Unit) { focusRequester.requestFocus() }
}

@Composable
private fun SaveButton(
    canSave: Boolean,
    isLoading: Boolean,
    onSave: () -> Unit
) {
    TextButton(
        onClick = onSave,
        enabled = canSave
    ) {
        val alpha = animateFloatAsState(targetValue = if (isLoading) 1f else 0f, label = "loading_alpha")
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(id = R.string.save), modifier = Modifier.alpha(1-alpha.value))
            CircularProgressIndicator(
                Modifier
                    .size(24.dp)
                    .alpha(alpha.value))
        }
    }
}