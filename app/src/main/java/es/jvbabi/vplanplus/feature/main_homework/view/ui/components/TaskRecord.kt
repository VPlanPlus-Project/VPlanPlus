package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.grayScale
import es.jvbabi.vplanplus.util.blendColor
import es.jvbabi.vplanplus.util.toTransparent

@Composable
fun TaskRecord(
    id: Long,
    task: String,
    isDone: Boolean,
    isEditing: Boolean,
    isNewTask: Boolean,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    onUpdateTask: (content: String) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    var isLoading by rememberSaveable(id) { mutableStateOf(false) }
    var shallBeDeleted by rememberSaveable(id) { mutableStateOf(false) }
    var textFieldValueState by remember(id) {
        mutableStateOf(
            TextFieldValue(
                text = task,
            )
        )
    }
    LaunchedEffect(key1 = isNewTask) {
        if (isNewTask) {
            focusRequester.requestFocus()
            textFieldValueState = textFieldValueState.copy(selection = TextRange(1))
        }
    }
    LaunchedEffect(key1 = isDone, key2 = task) {
        isLoading = false
    }
    val colorScheme = MaterialTheme.colorScheme
    val loadingBlendValue by animateFloatAsState(
        targetValue = if (isDone) 0f else 1f,
        label = "blendValue"
    )
    val editingBlendValue by animateFloatAsState(
        targetValue = if (isEditing) 1f else 0f,
        label = "blendValue"
    )

    LaunchedEffect(key1 = isEditing) {
        if (!isEditing && shallBeDeleted) onDelete()
        else if (!isEditing && (textFieldValueState.text != task || isNewTask)) onUpdateTask(textFieldValueState.text)
    }

    val deleteBlendValue by animateFloatAsState(
        targetValue = if (shallBeDeleted) 1f else 0f,
        label = "blendValue"
    )
    RowVerticalCenter(
        modifier = Modifier
            .fillMaxWidth()
            .grayScale(1 - deleteBlendValue)
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                blendColor(
                    colorScheme.surfaceVariant.toTransparent(),
                    colorScheme.surfaceVariant,
                    editingBlendValue
                )
            )
            .then(if (!isEditing) Modifier.clickable {
                isLoading = true; onClick()
            } else Modifier)
    ) {
        Box(
            modifier = Modifier.size(48.dp)
        ) action@{
            androidx.compose.animation.AnimatedVisibility(
                visible = !isEditing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .drawWithContent {
                            drawCircle(
                                color = blendColor(
                                    colorScheme.primary,
                                    colorScheme.outline,
                                    loadingBlendValue
                                ),
                                radius = 14.dp.toPx(),
                            )
                            drawCircle(
                                color = blendColor(
                                    colorScheme.background.copy(alpha = 0f),
                                    colorScheme.background,
                                    loadingBlendValue
                                ),
                                radius = 12.dp.toPx(),
                            )
                            drawContent()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .alpha(
                                animateFloatAsState(
                                    targetValue = if (isLoading) 1f else 0f,
                                    label = "Alpha"
                                ).value
                            )
                            .size(30.dp),
                        strokeWidth = 4.dp
                    )
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = colorScheme.onPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(1 - loadingBlendValue),
                    )
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = isEditing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .drawWithContent {
                            drawCircle(
                                color = colorScheme.error,
                                radius = 14.dp.toPx(),
                            )
                            drawLine(
                                color = colorScheme.onError,
                                start = Offset(16.dp.toPx(), size.height / 2),
                                end = Offset(
                                    lerp(16.dp.toPx(), 32.dp.toPx(), editingBlendValue),
                                    size.height / 2
                                ),
                                strokeWidth = 2.dp.toPx(),
                            )
                            drawContent()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = {
                        if (isNewTask) onDelete()
                        else shallBeDeleted = !shallBeDeleted
                    }) {
                        
                    }
                }
            }
        }

        Column {
            if (isEditing) BasicTextField(
                value = textFieldValueState,
                onValueChange = { textFieldValueState = it },
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .focusRequester(focusRequester)
            )
            else Text(
                text = task,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface
            )
            AnimatedVisibility(
                visible = shallBeDeleted,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = stringResource(id = R.string.homework_detailViewTaskMarkedAsDelete),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskRecordPreview() {
    TaskRecord(
        id = 1,
        task = "Task",
        isDone = false,
        isEditing = false,
        isNewTask = false
    )
}

@Preview(showBackground = true)
@Composable
private fun TaskRecordDoneAndEditingPreview() {
    TaskRecord(
        id = 1,
        task = "Task",
        isDone = true,
        isEditing = true,
        isNewTask = true
    )
}