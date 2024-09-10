package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer4Dp

@Composable
@Preview
fun CalendarFloatingActionButton(
    onClick: () -> Unit = {},
) {
    ExtendedFloatingActionButton(onClick = onClick) {
        RowVerticalCenter {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer4Dp()
            Text(text = stringResource(id = R.string.home_quickActionsNewHomework))
        }
    }
}