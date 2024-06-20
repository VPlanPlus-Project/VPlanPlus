package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate

@Composable
fun RowScope.DueToCard(
    until: LocalDate?
) {
    val context = LocalContext.current
    BigCard(
        modifier = Modifier.weight(1f, true),
        icon = Icons.Default.AccessTime,
        title = stringResource(id = R.string.homework_detailViewDueTo),
        subtitle = if (until != null) DateUtils.localizedRelativeDate(context, until) else ""
    )
}

@Composable
@Preview
private fun DueToCardPreview() {
    Row {
        DueToCard(until = LocalDate.now())
    }
}