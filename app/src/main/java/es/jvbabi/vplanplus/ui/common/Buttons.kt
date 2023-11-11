package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun LoadingButton(
    content: @Composable () -> Unit,
    onClick: () -> Unit,
    loading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick() },
        enabled = !loading,
        modifier = modifier,
        content = {
            if (loading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .padding(6.dp)
                )
            } else {
                content()
            }
        }
    )
}

@Composable
fun BackIcon() {
    Icon(
        imageVector = Icons.AutoMirrored.Default.ArrowBack,
        contentDescription = stringResource(id = R.string.back)
    )
}

@Composable
@Preview
fun LoadingButtonPreview() {
    LoadingButton(content = { Text(text = "Hier klicken") }, onClick = {}, loading = false)
}

@Composable
fun BigButtonGroup(buttons: List<BigButton>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row {
            buttons.forEachIndexed { i, button ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .weight(1f, false)
                        .clickable { button.onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(imageVector = button.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(text = button.text, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp), color = MaterialTheme.colorScheme.primary)
                    }
                }
                if (i != buttons.size - 1) {
                    VerticalDivider()
                }
            }
        }
    }
}

@Composable
@Preview
fun BigButtonGroupPreview() {
    BigButtonGroup(
        listOf(
            BigButton(icon = Icons.Outlined.Delete, text = "Delete", onClick = {}),
            BigButton(icon = Icons.Outlined.Edit, text = "Rename", onClick = {}),
            BigButton(icon = Icons.Outlined.Share, text = "Share", onClick = {}),
        )
    )
}

data class BigButton(
    val icon: ImageVector,
    val text: String,
    val onClick: () -> Unit
)