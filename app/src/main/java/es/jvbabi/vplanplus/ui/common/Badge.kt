package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Badge(color: Color, text: String) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, color, RoundedCornerShape(25))
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
@Preview
fun BadgePreview() {
    Badge(Color.Cyan, "Beta")
}