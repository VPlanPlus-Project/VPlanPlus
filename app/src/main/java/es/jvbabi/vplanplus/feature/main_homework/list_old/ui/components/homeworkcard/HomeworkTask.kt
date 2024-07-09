package es.jvbabi.vplanplus.feature.main_homework.list_old.ui.components.homeworkcard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeworkTask(
    modifier: Modifier = Modifier,
    content: String,
    isLoading: Boolean,
    isDone: Boolean,
    userCanEditThisTask: Boolean,
    onToggleDone: () -> Unit,
    onDeleteClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    var menuVisible by remember { mutableStateOf(false) }
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .padding(4.dp)
            .combinedClickable(
                onClick = onToggleDone,
                onLongClick = { menuVisible = true }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        } else Checkbox(
            checked = isDone,
            onCheckedChange = { onToggleDone() })
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(4.dp)
        )
    }
    DropdownMenu(
        expanded = menuVisible,
        onDismissRequest = { menuVisible = false }) {
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.delete)) },
            onClick = { menuVisible = false; onDeleteClicked() },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            },
            enabled = userCanEditThisTask && !isLoading
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.homework_edit)) },
            onClick = { menuVisible = false; onEditClicked() },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            },
            enabled = userCanEditThisTask && !isLoading
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeworkTaskPreview() {
    HomeworkTask(
        content = "Do your homework",
        isLoading = false,
        isDone = false,
        userCanEditThisTask = true,
        onToggleDone = {},
        onDeleteClicked = {},
        onEditClicked = {},
    )
}