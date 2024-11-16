package es.jvbabi.vplanplus.feature.main_homework.list.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.CustomModal
import es.jvbabi.vplanplus.ui.common.ModalOption
import es.jvbabi.vplanplus.ui.common.Option
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisibilityFilterSheet(
    sheetState: SheetState,
    state: Boolean?,
    onDismiss: () -> Unit,
    onUpdateState: (Boolean?) -> Unit
) {
    CustomModal(
        sheetState = sheetState,
        onDismiss = onDismiss
    ) {
        val scope = rememberCoroutineScope()
        val hideDrawer = { scope.launch { sheetState.hide(); onDismiss() } }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
        ) {
            Option(
                title = stringResource(id = R.string.homework_filterSheetAll),
                icon = Icons.Outlined.Visibility,
                enabled = true,
                state = state == null,
                onClick = { onUpdateState(null); hideDrawer() }
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .verticalScroll(rememberScrollState())
        ) {
            listOf(
                ModalOption(
                    title = stringResource(id = R.string.homework_filterVisibilitySheetVisible),
                    icon = Icons.Default.Visibility,
                    isEnabled = true,
                    isSelected = state == true,
                    onClick = { onUpdateState(true); hideDrawer() }
                ),
                ModalOption(
                    title = stringResource(id = R.string.homework_filterVisibilitySheetHidden),
                    icon = Icons.Default.VisibilityOff,
                    isEnabled = true,
                    isSelected = state == false,
                    onClick = { onUpdateState(false); hideDrawer() }
                )
            ).forEach { entry ->
                Option(
                    title = entry.title,
                    icon = entry.icon,
                    state = entry.isSelected,
                    enabled = entry.isEnabled,
                    onClick = entry.onClick
                )
            }
        }
    }
}