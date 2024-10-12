package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp

@Composable
fun DetailsSection(
    currentDetails: String?,
    isVisible: Boolean = true,
    isContentExpanded: Boolean,
    onHeaderClicked: () -> Unit,
    onDetailsSelected: (details: String?) -> Unit,
) {
    Section(
        title = {
            TitleRow(
                title = stringResource(R.string.examsNew_details),
                subtitle = currentDetails ?: stringResource(R.string.examsNew_details_noDetails),
                icon = Icons.AutoMirrored.Default.ShortText,
                onClick = onHeaderClicked
            )
        },
        isVisible = isVisible,
        isContentExpanded = isContentExpanded,
    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            var value by rememberSaveable { mutableStateOf(currentDetails ?: "") }
            TextField(
                value = value,
                onValueChange = { value = it },
                label = { Text(stringResource(R.string.examsNew_details)) },
                placeholder = { Text(stringResource(R.string.examsNew_details_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 2,
                maxLines = 4
            )
            Spacer8Dp()
            RowVerticalCenter {
                AnimatedContent(
                    value.isBlank(),
                    label = "primary_action"
                ) { isBlank ->
                    if (isBlank) OutlinedButton(
                        onClick = { onDetailsSelected(value.ifBlank { null }) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SkipNext, null)
                        Spacer4Dp()
                        Text(stringResource(R.string.examsNew_details_skip))
                    } else Button(
                        onClick = { onDetailsSelected(value.ifBlank { null }) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer4Dp()
                        Text(stringResource(R.string.examsNew_details_save))
                    }
                }
            }
        }
    }
}