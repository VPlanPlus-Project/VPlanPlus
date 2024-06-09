package es.jvbabi.vplanplus.feature.main_homework.add.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
@Preview(showBackground = true)
fun ImageButton(
    modifier: Modifier = Modifier,
    uri: Uri = Uri.EMPTY,
    onDelete: () -> Unit = {}
) {
    Box(modifier = modifier
        .size(192.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onDelete,
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        }
    }
}