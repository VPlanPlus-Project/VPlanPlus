package es.jvbabi.vplanplus.feature.main_homework.list.ui.components.homeworkcard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.util.toBlackAndWhite
import kotlinx.coroutines.delay

@Composable
fun AddHomeworkTask(
    isVisible: Boolean,
    isEnabled: Boolean,
    onCloseClicked: () -> Unit,
    onAddTask: (content: String) -> Unit,
) {
    var content by rememberSaveable { mutableStateOf("") }
    var isEmpty by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(150)
            content = ""
        }
    }
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(tween(150)),
        exit = shrinkVertically(tween(150))
    ) {
        Row(
            modifier = Modifier.height(85.dp),
            verticalAlignment = Alignment.Top
        ) {
            TextField(
                value = content,
                onValueChange = { isEmpty = it.isEmpty(); content = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(id = R.string.homework_addTask)) },
                enabled = isEnabled,
                supportingText = {
                    if (isEmpty) {
                        Text(
                            text = stringResource(id = R.string.homework_emptyTask),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            IconButton(
                onClick = onCloseClicked,
                enabled = isEnabled,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.close)
                )
            }
            IconButton(
                onClick = {
                    if (content.isBlank()) {
                        isEmpty = true
                        return@IconButton
                    }
                    onAddTask(content)
                    onCloseClicked()
                    content = ""
                },
                enabled = isEnabled,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .then(
                        if (isEmpty && content.isNotBlank()) Modifier.background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(50)
                        )
                        else Modifier.background(
                            MaterialTheme.colorScheme.primary.toBlackAndWhite(),
                            RoundedCornerShape(50)
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddHomeworkTaskPreview() {
    AddHomeworkTask(
        isVisible = true,
        isEnabled = true,
        onCloseClicked = {},
        onAddTask = {}
    )
}