package es.jvbabi.vplanplus.feature.settings.vpp_id.manage.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LaptopWindows
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model.Session
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model.SessionType
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import java.time.format.DateTimeFormatter

@Composable
fun SessionEntry(
    session: Session,
    onSessionClick: (Session) -> Unit
) {
    SettingsSetting(
        icon = if (session.type == SessionType.VPLANPLUS) Icons.Default.Smartphone else Icons.Default.LaptopWindows,
        title = session.name,
        subtitle = buildSubtitle(session),
        type = SettingsType.FUNCTION,
        enabled = true,
        clickable = true,
        titleOverflow = TextOverflow.Ellipsis,
        doAction = { onSessionClick(session) }
    )
}

@Composable
private fun buildSubtitle(session: Session): String {
    val builder = StringBuilder()
    builder.append(
        stringResource(
            id = R.string.vppIdSettingsManagement_sessionsDate,
            session.createAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"))
        )
    )
    if (session.isCurrent) {
        builder.append(" $DOT ")
        builder.append(stringResource(id = R.string.vppIdSettingsManagement_sessionsCurrent))
    }
    return builder.toString()
}