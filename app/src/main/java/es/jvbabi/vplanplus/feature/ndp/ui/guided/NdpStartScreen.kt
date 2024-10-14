package es.jvbabi.vplanplus.feature.ndp.ui.guided

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun NdpStartScreenContent(
    date: LocalDate,
    enabled: Boolean,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Let's prepare for the next school day!")
        Text(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)))
        Spacer16Dp()
        Button(
            onClick = onStart,
            enabled = enabled
        ) { Text("Let's go!") }
    }
}

@Preview
@Composable
private fun NdpStartScreenPreview() {
    NdpStartScreenContent(
        date = LocalDate.now().plusDays(1),
        enabled = false
    ) {}
}