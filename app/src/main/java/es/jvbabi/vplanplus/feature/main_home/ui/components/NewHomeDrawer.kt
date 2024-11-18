package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHomeDrawer(
    onClose: () -> Unit = {},
    onOpenCalendar: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onClose,
        windowInsets = BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom),
        modifier = Modifier.padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(Modifier.padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 16.dp, top = 4.dp)) {
            NewHomeDrawerContent(
                onClose = { scope.launch { state.hide(); onClose() } },
                onOpenCalendar = { scope.launch { state.hide(); onOpenCalendar() } }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun NewHomeDrawerContent(
    onClose: () -> Unit = {},
    onOpenCalendar: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_newHomeDrawer_title),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer16Dp()
        Image(
            painter = painterResource(id = R.drawable.undraw_schedule_re_2vro),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(24.dp)
                .padding(horizontal = 48.dp, vertical = 16.dp)
                .size(128.dp)
                .padding(16.dp)
        )
        Spacer16Dp()
        Text(
            text = stringResource(R.string.home_newHomeDrawer_text),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer8Dp()
        RowVerticalCenter(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.weight(1f)
            ) {
                RowVerticalCenter {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                    )
                    Spacer4Dp()
                    Text(stringResource(R.string.ok))
                }
            }
            Button(
                onClick = onOpenCalendar,
                modifier = Modifier.weight(1f)
            ) {
                RowVerticalCenter {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                    )
                    Spacer4Dp()
                    Text(stringResource(R.string.home_newHomeDrawer_openCalendar))
                }
            }
        }
    }
}