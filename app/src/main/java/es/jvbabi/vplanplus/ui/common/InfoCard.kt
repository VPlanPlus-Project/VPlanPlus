package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    text: String,
    buttonText1: String? = null,
    buttonAction1: () -> Unit = {},
    buttonText2: String? = null,
    buttonAction2: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .shadow(5.dp, shape = RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .drawWithContent {
                drawRect(
                    color = colorScheme.primary,
                    topLeft = Offset(0f, 0f),
                    size = Size(32f, size.height)
                )
                drawContent()
            }
            .padding(start = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 16.dp),
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = text)
            }
        }
        if (buttonText1 != null) Row(
            modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.End),
        ) {
            TextButton(onClick = { buttonAction1() }) {
                Text(text = buttonText1)
            }
            if (buttonText2 != null) {
                Spacer(modifier = Modifier.size(8.dp))
                TextButton(onClick = { buttonAction2() }) {
                    Text(text = buttonText2)
                }
            }
        } else {
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoCardPreview() {
    InfoCard(
        imageVector = Icons.Default.Info,
        title = "Title",
        text = "Text\nA very big one",
        buttonText1 = "null",
        buttonText2 = "null"
    )
}