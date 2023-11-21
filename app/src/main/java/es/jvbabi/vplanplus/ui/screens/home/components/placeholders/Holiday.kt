package es.jvbabi.vplanplus.ui.screens.home.components.placeholders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun Holiday(
    compactMode: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (compactMode) Arrangement.SpaceBetween else Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.BeachAccess,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (compactMode) stringResource(id = R.string.home_holiday).split("").joinToString("\n") else stringResource(id = R.string.home_holiday),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            if (!compactMode) Text(
                text = stringResource(id = R.string.home_holidayText),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun HolidayPreview() {
    Holiday(false)
}

@Composable
@Preview(showBackground = true)
private fun HolidayCompactPreview() {
    Holiday(compactMode = true)
}