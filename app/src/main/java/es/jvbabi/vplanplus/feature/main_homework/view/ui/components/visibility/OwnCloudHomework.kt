package es.jvbabi.vplanplus.feature.main_homework.view.ui.components.visibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
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
fun OwnCloudHomeworkSheetContent(
    isShared: Boolean,
    onShare: () -> Unit,
    onPrivate: () -> Unit
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = R.string.homework_detailViewVisibilityCloudSharedSheetTitle),
            style = MaterialTheme.typography.titleMedium
        )

        SegmentedButtons {
            SegmentedButtonItem(
                selected = isShared,
                onClick = onShare,
                icon = { Icon(imageVector = Icons.Default.Share, contentDescription = null) },
                label = { Text(text = stringResource(id = R.string.homework_detailViewVisibilityCloudSharedSheetShare)) }
            )
            SegmentedButtonItem(
                selected = !isShared,
                onClick = onPrivate,
                icon = { Icon(imageVector = Icons.Default.VisibilityOff, contentDescription = null) },
                label = { Text(text = stringResource(id = R.string.homework_detailViewVisibilityCloudSharedSheetPrivate)) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun OwnCloudHomeworkSheetContentPreview() {
    OwnCloudHomeworkSheetContent(
        isShared = false,
        onShare = {},
        onPrivate = {}
    )
}

@Composable
@Preview(showBackground = true)
private fun OwnCloudHomeworkSheetContentPreviewShared() {
    OwnCloudHomeworkSheetContent(
        isShared = true,
        onShare = {},
        onPrivate = {}
    )
}