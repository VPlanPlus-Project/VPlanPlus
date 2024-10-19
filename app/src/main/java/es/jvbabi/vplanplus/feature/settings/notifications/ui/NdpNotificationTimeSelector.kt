package es.jvbabi.vplanplus.feature.settings.notifications.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.ui.common.Slider
import es.jvbabi.vplanplus.ui.common.Spacer12Dp
import es.jvbabi.vplanplus.util.Size
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun NdpNotificationTimeSelector() {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DayOfWeek
            .entries
            .filter { it != DayOfWeek.SATURDAY }
            .forEach { dayOfWeek ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = dayOfWeek
                            .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            .uppercase()
                            .substringBefore("."),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer12Dp()
                    Text(
                        text = "00:00",
                        style = MaterialTheme.typography.labelSmall
                    )
                    var fifteenMinutesFromStart by rememberSaveable { mutableFloatStateOf(4 * 16f) }
                    Box(Modifier.height(400.dp)) {
                        Slider(
                            orientation = Orientation.Vertical,
                            currentValue = fifteenMinutesFromStart,
                            range = 0f..(24 * 60 / 15f),
                            steps = 1f,
                            trackThickness = 8.dp,
                            puckSize = Size(60.dp, 20.dp),
                            puckContent = { fifteenMinutesFromStartBlocks ->
                                val time =
                                    LocalTime.ofSecondOfDay(((fifteenMinutesFromStartBlocks / 4) * 60 * 60).toLong())
                                Text(
                                    text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            onValueChange = { fifteenMinutesFromStart = it }
                        )
                    }
                    Text(
                        text = "23:59",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

            }
    }
}

@Composable
@Preview(showBackground = true)
private fun NdpNotificationTimeSelectorPreview() {
    NdpNotificationTimeSelector()
}