package es.jvbabi.vplanplus.feature.main_home.ui.components.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R

@Composable
fun Weekend() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = if (MainActivity.isAppInDarkMode.value) painterResource(id = R.drawable.weekend_dark) else painterResource(
                id = R.drawable.weekend
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(0.8f),
        )
        Text(
            text = stringResource(id = R.string.home_activeDayWeekendTitle),
            style = MaterialTheme.typography.headlineMedium.copy(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
            textAlign = TextAlign.Center
        )
        Text(text = stringResource(id = R.string.home_activeDayWeekendSubtitle), textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekendPreview() {
    Weekend()
}