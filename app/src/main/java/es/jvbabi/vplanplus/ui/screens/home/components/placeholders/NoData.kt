package es.jvbabi.vplanplus.ui.screens.home.components.placeholders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOff
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
fun NoData(
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
                imageVector = Icons.Default.GridOff,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (compactMode) stringResource(id = R.string.home_noData).split("").joinToString("\n") else stringResource(id = R.string.home_noData),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            if (!compactMode) Text(
                text = stringResource(id = R.string.home_noDataText),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoDataPreview() {
    NoData(false)
}

@Preview(showBackground = true)
@Composable
fun NoDataCompactPreview() {
    NoData(compactMode = true)
}