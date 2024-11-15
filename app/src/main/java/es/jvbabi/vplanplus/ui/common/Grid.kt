package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Grid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    padding: Dp = 0.dp,
    content: List<@Composable (column: Int, row: Int, index: Int) -> Unit>
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(padding)
    ) {
        val rows = content.chunked(columns)
        var i = 0
        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(padding)
            ) {
                for (item in row) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        item(i % columns, i / columns, i)
                        i++
                    }
                }
            }
        }
    }
}