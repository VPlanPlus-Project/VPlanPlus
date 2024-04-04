package es.jvbabi.vplanplus.feature.main_home.ui.components

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
import es.jvbabi.vplanplus.R
import java.time.ZonedDateTime

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    time: ZonedDateTime,
    name: String?
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

    val text = buildAnnotatedString {
        withStyle(
            MaterialTheme
                .typography
                .displaySmall
                .copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    ),
                    fontWeight = FontWeight.Black
                )
                .toSpanStyle()
        ) {
            append(greeting)
            if (name != null) append("\n")
        }
        if (name != null) withStyle(
            MaterialTheme
                .typography
                .headlineSmall
                .copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.secondary
                        )
                    ),
                    fontWeight = FontWeight.Light
                )
                .toSpanStyle()
        ) {
            append(name)
        }
    }

    Text(text, modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Greeting(
        time = ZonedDateTime.now(),
        name = "Android"
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreviewEmpty() {
    Greeting(
        time = ZonedDateTime.now(),
        name = null
    )
}