package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun TopicSection(
    currentTopic: String?,
    isVisible: Boolean = true,
    isContentExpanded: Boolean,
    onHeaderClicked: () -> Unit,
    onTopicSelected: (topic: String) -> Unit,
) {
    Section(
        title = {
            TitleRow(
                title = stringResource(R.string.examsNew_topic),
                subtitle = currentTopic ?: stringResource(R.string.examsNew_topic_noTopic),
                icon = Icons.Default.Topic,
                onClick = onHeaderClicked
            )
        },
        isVisible = isVisible,
        isContentExpanded = isContentExpanded,
    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            var value by rememberSaveable { mutableStateOf(currentTopic ?: "") }
            TextField(
                value = value,
                onValueChange = { value = it },
                label = { Text(stringResource(R.string.examsNew_topic)) },
                placeholder = { Text(stringResource(R.string.examsNew_topic_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onTopicSelected(value) }),
                singleLine = true
            )
        }
    }
}