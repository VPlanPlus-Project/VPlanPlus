package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components

import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun DayInfoCard(
    info: String?
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = info != null,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        es.jvbabi.vplanplus.ui.common.InfoCard(
            imageVector = Icons.Default.Campaign,
            title = stringResource(id = R.string.calendar_infoTitle),
            text = info ?: ""
        )
    }
}

@Composable
@Preview
private fun DayInfoCardPreview() {
    DayInfoCard(
        info = "Info"
    )
}