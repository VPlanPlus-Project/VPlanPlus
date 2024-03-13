package es.jvbabi.vplanplus.feature.home.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.customStickyHeader(modifier: Modifier = Modifier, text: @Composable () -> Unit) {
    stickyHeader {
        Box(
            modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface,
                            Color.Transparent
                        )
                    )
                )
                .padding(8.dp)
        ) {
            text()
        }
    }
}