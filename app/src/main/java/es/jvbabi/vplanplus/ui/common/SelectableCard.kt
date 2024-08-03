package es.jvbabi.vplanplus.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.util.blendColor

@Composable
fun SelectableCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onToggleSelected: (to: Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    val selectedModifier by animateFloatAsState(targetValue = if (isSelected) 1f else 0f, label = "selectedModifier")
    val borderColor = blendColor(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.outline, selectedModifier)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, borderColor, shape = RoundedCornerShape(8.dp))
            .clickable { onToggleSelected(!isSelected) }
            .then(modifier)
    ) {
        content()
    }
}

@Composable
@Preview
private fun SelectableCardPreview() {
    SelectableCard(
        modifier = Modifier.padding(8.dp),
        isSelected = true,
        onToggleSelected = {}
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text("Selected")
        }
    }
}

@Composable
@Preview
private fun SelectableCardNotSelectedPreview() {
    SelectableCard(
        modifier = Modifier.padding(8.dp),
        isSelected = false,
        onToggleSelected = {}
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text("Not selected")
        }
    }
}