package es.jvbabi.vplanplus.feature.home.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LastSyncText(modifier: Modifier = Modifier, lastSync: ZonedDateTime?) {
    var text = lastSync.toText()
    text = if (text == null) stringResource(id = R.string.home_lastSyncNever)
    else stringResource(id = R.string.home_lastSync, text)
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
    )
}

private fun ZonedDateTime?.toText(): String? {
    return if (this == null) null
    else if (this.toZonedLocalDateTime().toLocalDate()
            .isEqual(LocalDate.now())
    ) DateTimeFormatter.ofPattern("HH:mm").format(this)
    else DateTimeFormatter.ofPattern("EE, dd.MM.yyyy").format(this)
}