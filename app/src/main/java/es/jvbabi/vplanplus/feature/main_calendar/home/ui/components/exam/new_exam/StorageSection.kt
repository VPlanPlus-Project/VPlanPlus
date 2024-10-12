package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.add.ui.SaveType
import es.jvbabi.vplanplus.ui.common.SegmentedButtonItem
import es.jvbabi.vplanplus.ui.common.SegmentedButtons
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.stringResource

@Composable
fun StorageSection(
    currentState: SaveType?,
    isVisible: Boolean = true,
    isContentExpanded: Boolean,
    onHeaderClicked: () -> Unit,
    onTypeSelected: (type: SaveType) -> Unit
) {
    Section(
        title = {
            TitleRow(
                title = stringResource(R.string.examsNew_save),
                subtitle = stringResource(currentState?.stringResource() ?: R.string.examsNew_noType),
                icon = Icons.Default.Share,
                onClick = onHeaderClicked
            )
        },
        isVisible = isVisible,
        isContentExpanded = isContentExpanded
    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            Spacer8Dp()
            SegmentedButtons{
                SegmentedButtonItem(
                    selected = currentState == SaveType.LOCAL,
                    label = { Text(stringResource(R.string.saveType_local)) },
                    onClick = { onTypeSelected(SaveType.LOCAL) }
                )
                SegmentedButtonItem(
                    selected = currentState == SaveType.CLOUD,
                    label = { Text(stringResource(R.string.saveType_cloud)) },
                    onClick = { onTypeSelected(SaveType.CLOUD) }
                )
                SegmentedButtonItem(
                    selected = currentState == SaveType.SHARED,
                    label = { Text(stringResource(R.string.saveType_shared)) },
                    onClick = { onTypeSelected(SaveType.SHARED) }
                )
            }
        }
    }
}