package es.jvbabi.vplanplus.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.util.blendColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberModalBottomSheetStateWithoutFullExpansion(): SheetState {
    return rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { it != SheetValue.Expanded }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModal(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column {
            content()
            Spacer8Dp()
        }
    }
}

data class ModalOption(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val isEnabled: Boolean,
    val isSelected: Boolean,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modal(
    sheetState: SheetState,
    entries: List<ModalOption>,
    onDismiss: () -> Unit
) {
    CustomModal(sheetState, onDismiss) {
        entries.forEach { entry ->
            Option(
                title = entry.title,
                subtitle = entry.subtitle,
                icon = entry.icon,
                state = entry.isSelected,
                enabled = entry.isEnabled,
                onClick = entry.onClick
            )
        }
    }
}

@Composable
fun Option(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    icon: ImageVector?,
    state: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val factor by animateFloatAsState(targetValue = if (state) 1f else 0f, label = "factor")
    val background = blendColor(Color.Transparent, MaterialTheme.colorScheme.primaryContainer, factor)
    val contentColor = blendColor(MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.onPrimaryContainer, factor)
    val disabledContentColor = MaterialTheme.colorScheme.outline
    RowVerticalCenter(
        modifier
            .fillMaxWidth()
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .height(56.dp)
            .background(background)
            .padding(vertical = 8.dp, horizontal = 16.dp)) {
        if (icon != null) Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) contentColor else disabledContentColor,
            modifier = Modifier.size(24.dp)
        ) else Spacer(Modifier.size(24.dp))
        Column(Modifier.padding(start = 16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = if (enabled) contentColor else disabledContentColor)
            if (!subtitle.isNullOrBlank()) Text(text = subtitle, style = MaterialTheme.typography.labelMedium, color = if (enabled) contentColor else disabledContentColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OptionPreview() {
    Option(
        title = stringResource(id = R.string.addHomework_saveVppId),
        subtitle = stringResource(id = R.string.addHomework_saveVppIdNoVppId),
        icon = Icons.Default.CloudQueue,
        state = true,
        enabled = true,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SmallDragHandler() {
    Box(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 10.dp)
                .width(32.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}