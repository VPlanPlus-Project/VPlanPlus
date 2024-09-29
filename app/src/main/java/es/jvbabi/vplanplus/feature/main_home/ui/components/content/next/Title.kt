package es.jvbabi.vplanplus.feature.main_home.ui.components.content.next

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NextWeek
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun Title(
    nextSchoolDayDate: LocalDate,
    start: LocalTime?,
    end: LocalTime?
) {
    Row(verticalAlignment = Alignment.CenterVertically) title@{
        Spacer4Dp()
        Icon(
            imageVector = Icons.AutoMirrored.Default.NextWeek,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
        )
        Spacer8Dp()
        Column {
            Text(
                text = run {
                    val localizedRelativeDate = DateUtils.localizedRelativeDate(LocalContext.current, nextSchoolDayDate)
                    if (localizedRelativeDate == null) stringResource(R.string.home_nextDayTitle)
                    else stringResource(R.string.home_nextDayTitleWithDate, localizedRelativeDate)
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
            )
            Text(
                text = buildString {
                    append(nextSchoolDayDate.format(DateTimeFormatter.ofPattern("EEE, dd. MMM yyyy")))
                    if (start == null || end == null) return@buildString
                    append(" $DOT ")
                    append(start.format(DateTimeFormatter.ofPattern("HH:mm")))
                    append(" - ")
                    append(end.format(DateTimeFormatter.ofPattern("HH:mm")))
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}