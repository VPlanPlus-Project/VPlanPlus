package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.add.ui.components.due_to.SetDueToModal
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.blendColor
import es.jvbabi.vplanplus.util.toTransparent
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.DueToCard(
    until: LocalDate?,
    onUpdateDueDate: (LocalDate) -> Unit,
    isEditModeActive: Boolean
) {
    val context = LocalContext.current
    val blendValue =
        animateFloatAsState(targetValue = if (isEditModeActive) 1f else 0f, label = "blendValue")
    val color = blendColor(
        MaterialTheme.colorScheme.surfaceVariant.toTransparent(),
        MaterialTheme.colorScheme.surfaceVariant,
        blendValue.value
    )

    var isUntilSheetOpen by rememberSaveable { mutableStateOf(false) }
    val untilSheetState = rememberModalBottomSheetState(true)
    LaunchedEffect(key1 = isEditModeActive) { if (!isEditModeActive) isUntilSheetOpen = false }

    if (isUntilSheetOpen) SetDueToModal(
        sheetState = untilSheetState,
        selectedDate = until,
        onSelectDate = { onUpdateDueDate(it) },
        onDismiss = { isUntilSheetOpen = false }
    )

    Box(
        modifier = Modifier
            .height(80.dp)
            .weight(1f, true)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .then(if (isEditModeActive) Modifier.clickable {
                isUntilSheetOpen = true
            } else Modifier)
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BigCard(
            modifier = Modifier
                .fillMaxWidth()
                .scale(1f - (0.1f * blendValue.value)),
            icon = Icons.Default.AccessTime,
            title = stringResource(id = R.string.homework_detailViewDueTo),
            subtitle = if (until != null) DateUtils.localizedRelativeDate(context, until) else ""
        )
    }
}

@Composable
@Preview
private fun DueToCardPreview() {
    Row {
        DueToCard(
            until = LocalDate.now(),
            onUpdateDueDate = {},
            isEditModeActive = false
        )
    }
}

@Composable
@Preview
private fun DueToCardPreviewEditMode() {
    Row {
        DueToCard(
            until = LocalDate.now(),
            onUpdateDueDate = {},
            isEditModeActive = true
        )
    }
}