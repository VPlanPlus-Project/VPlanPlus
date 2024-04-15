package es.jvbabi.vplanplus.ui.common.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    items: List<BarChartData>,
    scrollable: Boolean = false,
    showValueInBars: ((item: BarChartData) -> String)? = null,
    labeling: ((item: BarChartData) -> String)? = null
) {
    val highestValue = items.maxOf { it.value }

    Row(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (scrollable) Modifier.horizontalScroll(rememberScrollState()) else Modifier
            ),
        verticalAlignment = Alignment.Bottom,
    ) {
        items.forEachIndexed { i, item ->
            var value by rememberSaveable { mutableFloatStateOf(0f) }
            val heightModifier by animateFloatAsState(targetValue = value / highestValue, animationSpec = tween(250), label = "bar")
            LaunchedEffect(key1 = item.value) {
                delay(100L * i)
                value = item.value
            }
            Column(
                modifier = Modifier
                    .padding(start = if (i == 0) 0.dp else 4.dp)
                    .then(if (scrollable) Modifier.width(50.dp) else Modifier.weight(1f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (showValueInBars != null && item.value < highestValue / 5) Text(
                    text = showValueInBars(item),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight(heightModifier * 0.9f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                ){
                    if (showValueInBars != null && item.value >= highestValue / 5) Text(
                        text = showValueInBars(item),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.TopCenter),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                if (labeling != null) Text(
                    text = labeling(item),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BarChartPreview() {
    BarChart(
        items = listOf(
            BarChartData("A", 4f),
            BarChartData("B", 20f),
            BarChartData("C", 30f),
            BarChartData("D", 40f),
            BarChartData("E", 50f),
        ),
        scrollable = false,
        showValueInBars = { it.value.toInt().toString() },
        labeling = { it.group }
    )
}

data class BarChartData(
    val group: String,
    val value: Float
)