package es.jvbabi.vplanplus.feature.main_homework.add.ui

import android.os.Parcelable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.unsaved_changes_dialog.Dialog
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.UUID

@Parcelize
data class AddHomeworkSheetInitialValues(
    val defaultLessonId: UUID? = null,
    val until: LocalDate? = null,
) : Parcelable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeworkSheet(
    onClose: () -> Unit,
    initialValues: AddHomeworkSheetInitialValues = AddHomeworkSheetInitialValues(),
) {
    var hasChanged by rememberSaveable { mutableStateOf(false) }

    var isDismissDialogOpen by rememberSaveable { mutableStateOf(false) }
    if (isDismissDialogOpen) {
        Dialog(onCancel = { isDismissDialogOpen = false }, onDiscard = onClose)
    }

    val modalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = {
            if (it == SheetValue.Hidden) {
                if (hasChanged) {
                    isDismissDialogOpen = true
                    return@rememberModalBottomSheetState false
                }
            }
            true
        },
    )
    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = modalSheetState,
    ) {
        AddHomeworkSheetContent(
            onClose = onClose,
            onChanged = { hasChanged = true },
            initialValues = initialValues,
        )
    }
}