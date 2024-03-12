package es.jvbabi.vplanplus.feature.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.ui.common.ComposableDialog
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun VersionHintsInformation(
    currentVersion: String,
    hints: List<VersionHints>,
    onCloseUntilNextTime: () -> Unit = {},
    onCloseUntilNextVersion: () -> Unit = {}
) {
    if (hints.isEmpty()) return

    ComposableDialog(
        icon = Icons.Default.SystemUpdate,
        title = stringResource(id = R.string.homeVersionHints_title),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.8f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(
                        id = R.string.homeVersionHints_description,
                        currentVersion
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(10.dp))
                hints.forEachIndexed { i, hint ->
                    Text(text = hint.header, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")
                            .format(hint.createdAt),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = hint.content,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (i != hints.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        onCancel = onCloseUntilNextTime,
        cancelString = stringResource(id = R.string.homeVersionHints_showLater),
        onDismiss = onCloseUntilNextVersion,
        onOk = onCloseUntilNextVersion
    )
}

@Composable
@Preview(showBackground = true)
private fun VersionHintsInformationPreview() {
    VersionHintsInformation(
        "v1.1",
        listOf(
            VersionHints(
                "Version A",
                "This is some <b>HTML</b> content",
                200,
                ZonedDateTime.now().minusDays(3)
            )
        )
    )
}