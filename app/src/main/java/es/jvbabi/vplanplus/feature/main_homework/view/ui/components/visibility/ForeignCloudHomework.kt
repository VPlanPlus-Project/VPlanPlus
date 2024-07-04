package es.jvbabi.vplanplus.feature.main_homework.view.ui.components.visibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.SegmentedButtonItem
import es.jvbabi.vplanplus.ui.common.SegmentedButtons

@Composable
fun ForeignCloudHomeworkSheetContent(
    isHidden: Boolean,
    onHide: () -> Unit,
    onShow: () -> Unit
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = R.string.homework_detailViewVisibilityLocalSheetTitle),
            style = MaterialTheme.typography.titleMedium
        )

        SegmentedButtons {
            SegmentedButtonItem(
                selected = isHidden,
                onClick = onHide,
                icon = { Icon(imageVector = Icons.Default.VisibilityOff, contentDescription = null) },
                label = { Text(text = stringResource(id = R.string.homework_detailViewVisibilityLocalSheetHide)) }
            )
            SegmentedButtonItem(
                selected = !isHidden,
                onClick = onShow,
                icon = { Icon(imageVector = Icons.Default.Visibility, contentDescription = null) },
                label = { Text(text = stringResource(id = R.string.homework_detailViewVisibilityLocalSheetShow)) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ForeignCloudHomeworkSheetContentPreview() {
    ForeignCloudHomeworkSheetContent(
        isHidden = false,
        onHide = {},
        onShow = {}
    )
}

@Composable
@Preview(showBackground = true)
private fun ForeignCloudHomeworkSheetContentHiddenPreview() {
    ForeignCloudHomeworkSheetContent(
        isHidden = true,
        onHide = {},
        onShow = {}
    )
}