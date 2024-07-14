package es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun NoDataAvailable() {
    Column(
        modifier = Modifier.padding(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(id = R.string.onboarding_defaultLessonsNoData),
            textAlign = TextAlign.Center
        )
    }
}