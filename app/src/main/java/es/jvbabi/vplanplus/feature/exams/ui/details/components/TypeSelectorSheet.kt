package es.jvbabi.vplanplus.feature.exams.ui.details.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.ui.common.Option
import es.jvbabi.vplanplus.ui.stringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeSelectorSheet(
    currentType: ExamCategory,
    sheetState: SheetState,
    onTypeSelected: (type: ExamCategory) -> Unit,
    onDismiss: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .verticalScroll(rememberScrollState())
        ) {
            ExamCategory.values.forEach {
                Option(
                    title = stringResource(it.stringResource()),
                    subtitle = null,
                    onClick = {
                        onTypeSelected(it)
                        scope.launch { sheetState.hide(); onDismiss() }
                    },
                    state = it == currentType,
                    enabled = true,
                    icon = null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun TypeSelectorSheetPreview() {
    TypeSelectorSheet(currentType = ExamCategory.Project, rememberModalBottomSheetState(), {})
}