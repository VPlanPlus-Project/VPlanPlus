package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ExamType
import es.jvbabi.vplanplus.ui.common.Option
import es.jvbabi.vplanplus.ui.stringResource

@Composable
fun TypeSection(
    selectedType: ExamType?,
    isVisible: Boolean = true,
    isContentExpanded: Boolean,
    onHeaderClicked: () -> Unit,
    onTypeSelected: (type: ExamType) -> Unit,
) {
    Section(
        title = {
            TitleRow(
                title = stringResource(R.string.examsNew_type),
                subtitle = stringResource(selectedType?.stringResource() ?: R.string.examsNew_noType),
                icon = Icons.Default.Category,
                onClick = onHeaderClicked
            )
        },
        isVisible = isVisible,
        isContentExpanded = isContentExpanded,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(8.dp)
                )
                .verticalScroll(rememberScrollState())
        ) {
            ExamType.entries.forEach { type ->
                Option(
                    title = stringResource(type.stringResource()),
                    subtitle = null,
                    icon = null,
                    state = type == selectedType,
                    enabled = true,
                    onClick = { onTypeSelected(type) }
                )
                HorizontalDivider()
            }
        }
    }
}