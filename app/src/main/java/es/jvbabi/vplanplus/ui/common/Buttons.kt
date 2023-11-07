package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
@Preview
fun LoadingButtonPreview() {
    LoadingButton(content = { Text(text = "Hier klicken") }, onClick = {}, loading = false)
}