package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import es.jvbabi.vplanplus.R
import java.time.ZonedDateTime

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    time: ZonedDateTime,
    name: String?
) {
    val greeting =
        if (name == null) stringResource(
            id = when (time.hour) {
                in 0..4 -> R.string.greeting_hello
                in 5..<10 -> R.string.greeting_morning
                in 10..11 -> R.string.greeting_day
                in 12..17 -> R.string.greeting_afternoon
                in 18..23 -> R.string.greeting_evening
                else -> R.string.greeting_hello
            }
        )
        else "Hey"

    val text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 36.sp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
        ) {
            append(greeting)
            if (name != null) append(", ${name.split(" ").dropLast(1).joinToString(" ")}")
            append("!")
        }
    }

    Text(text, modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Greeting(
        time = ZonedDateTime.now(),
        name = "John Doe"
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