package es.jvbabi.vplanplus.ui.screens.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.ViewType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSwitcher(
    viewType: ViewType,
    onViewModeChanged: (ViewType) -> Unit,
) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = viewType == ViewType.WEEK,
            onClick = { onViewModeChanged(ViewType.WEEK) },
            shape = MaterialTheme.shapes.small,
        ) {
            Icon(
                imageVector = Icons.Default.ViewWeek,
                contentDescription = null
            )
        }
        SegmentedButton(
            selected = viewType == ViewType.DAY,
            onClick = { onViewModeChanged(ViewType.DAY) },
            shape = MaterialTheme.shapes.small,
        ) {
            Icon(imageVector = Icons.Default.ViewDay, contentDescription = null)
        }
    }
}

@Preview
@Composable
private fun ViewSwitcherPreview() {
    ViewSwitcher(
        viewType = ViewType.WEEK,
        onViewModeChanged = {}
    )
}