package es.jvbabi.vplanplus.ui.screens.home.components.home

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import java.time.LocalDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Greeting(
    time: LocalDateTime,
    vppId: VppId?
) {

    val greeting = stringResource(
        id = when (time.hour) {
            in 0..4 -> R.string.greeting_hello
            in 5..<10 -> R.string.greeting_morning
            in 10..11 -> R.string.greeting_day
            in 12..17 -> R.string.greeting_afternoon
            in 18..23 -> R.string.greeting_evening
            else -> R.string.greeting_hello
        }
    )
    val style = MaterialTheme.typography.headlineSmall.copy(
        brush = Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary
            )
        )
    ).toSpanStyle()
    val text = buildAnnotatedString {
        withStyle(style.copy(fontWeight = FontWeight.Bold)) {
            append(greeting)
            if (vppId != null) append(",")
        }
    }

    FlowRow {
        Text(text, modifier = Modifier.padding(4.dp))
        if (vppId != null) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style.copy(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                    ) {
                        append(vppId.name)
                    }
                },
                modifier = Modifier.padding(4.dp),
                maxLines = 1,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MorningWithVppId() {
    Greeting(
        time = LocalDateTime.of(2022, 1, 1, 8, 0, 0),
        vppId = VppIdPreview.generateVppId(ClassesPreview.generateClass(null))
    )
}