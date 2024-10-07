package es.jvbabi.vplanplus.feature.main_home.ui.components.content.today

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.toLocalizedString

@Composable
fun CurrentOrNextTitle(
    isCurrent: Boolean,
    lessonNumber: Int,
) {
    Row(verticalAlignment = Alignment.CenterVertically) title@{
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .size(20.dp)
        )
        Column {
            Text(
                text = if (isCurrent) stringResource(R.string.home_todayCurrentLessonNow) else stringResource(R.string.home_todayCurrentLessonNext),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
            )
            Text(
                text = stringResource(R.string.home_lessonNumber, lessonNumber.toLocalizedString()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}