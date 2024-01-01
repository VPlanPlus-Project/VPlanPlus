package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun InfoCard(
    imageVector: ImageVector,
    title: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier
            .shadow(5.dp, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Icon(imageVector = imageVector, contentDescription = null, modifier = Modifier.size(30.dp))
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoCardPreview() {
    InfoCard(
        imageVector = Icons.Default.Info,
        title = "Title",
        text = "Text\nA very big one"
    )
}