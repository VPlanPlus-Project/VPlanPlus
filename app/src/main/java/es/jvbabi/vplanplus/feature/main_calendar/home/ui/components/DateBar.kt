package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.HEADER_STATIC_HEIGHT_DP
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.epoch
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateBar(
    date: LocalDate,
    lastSync: ZonedDateTime?,
    isSubstitutionPlan: Boolean?,
    isLarge: Boolean = false
) {
    RowVerticalCenterSpaceBetweenFill(
        modifier = Modifier
            .then(
                if (isLarge) Modifier
                    .height(HEADER_STATIC_HEIGHT_DP.dp)
                else Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            .padding(horizontal = 16.dp)
    ) title@{
        RowVerticalCenter {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("d. LLL", Locale.getDefault())),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            if (DateUtils.localizedRelativeDate(LocalContext.current, date, false) != null) Text(
                text = " $DOT " + DateUtils.localizedRelativeDate(LocalContext.current, date, false)!!,
                style = MaterialTheme.typography.bodyMedium
            )
            if (isSubstitutionPlan != null) Text(
                text = " $DOT " + if (isSubstitutionPlan) stringResource(id = R.string.calendar_substitutionPlan) else stringResource(id = R.string.calendar_timetable),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (lastSync != null) Text(
            text =
            if (lastSync == epoch()) stringResource(id = R.string.calendar_lastSyncNever)
            else stringResource(id = R.string.calendar_lastSync, lastSync.toLastSyncText()),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun ZonedDateTime.toLastSyncText(): String {
    val time = this.toZonedLocalDateTime()
    return if (time.toLocalDate()
            .isEqual(LocalDate.now())
    ) DateTimeFormatter.ofPattern("HH:mm").format(time)
    else DateTimeFormatter.ofPattern("EE, dd.MM.yyyy").format(time)
}

@Composable
@Preview(showBackground = true)
private fun DateBarPreview() {
    DateBar(
        date = LocalDate.now().plusDays(1),
        lastSync = ZonedDateTime.now(),
        isSubstitutionPlan = true
    )
}

@Composable
@Preview(showBackground = true)
private fun DateBarNoSyncPreview() {
    DateBar(
        date = LocalDate.now().plusDays(1),
        lastSync = null,
        isSubstitutionPlan = null
    )
}

@Composable
@Preview(showBackground = true)
private fun DateBarLargePreview() {
    DateBar(
        date = LocalDate.now().plusDays(1),
        lastSync = ZonedDateTime.now(),
        isSubstitutionPlan = true,
        isLarge = true
    )
}