package es.jvbabi.vplanplus.feature.main_homework.view.ui.components.visibility

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    isOwner: Boolean,
    isShownOrShared: Boolean,
    onShowOrShare: () -> Unit,
    onHideOrPrivate: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val onDoAction = { action: () -> Unit ->
        scope.launch {
            action()
            sheetState.hide()
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(Modifier.padding(16.dp)) {
            if (isOwner) OwnCloudHomeworkSheetContent(isShownOrShared, { onDoAction(onShowOrShare); onDismiss() }, { onDoAction(onHideOrPrivate); onDismiss() })
            else ForeignCloudHomeworkSheetContent(!isShownOrShared, { onDoAction(onHideOrPrivate); onDismiss() }, { onDoAction(onShowOrShare); onDismiss() })
        }
    }
}