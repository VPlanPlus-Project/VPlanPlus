package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Grid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    content: List<@Composable () -> Unit>
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val rows = content.chunked(columns)
        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (item in row) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        item()
                    }
                }
            }
        }
    }
}