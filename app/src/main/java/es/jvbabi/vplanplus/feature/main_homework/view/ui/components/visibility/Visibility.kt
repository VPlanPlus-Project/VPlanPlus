package es.jvbabi.vplanplus.feature.main_homework.view.ui.components.visibility

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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.BigCard
import es.jvbabi.vplanplus.ui.common.rememberModalBottomSheetStateWithoutFullExpansion
import es.jvbabi.vplanplus.util.blendColor
import es.jvbabi.vplanplus.util.toTransparent

/**
 * A card that displays the visibility of a [Homework].
 * @param isEditModeActive Whether the user is currently in edit mode.
 * @param isCurrentlyVisibleOrPublic Whether the [Homework] is currently visible or public.
 * @param willBeVisibleOrPublic Whether the [Homework] will be visible or public after applying changes made in edit mode.
 * @param canModifyOrigin Whether the user can modify the sharing status or just the local visibility.
 * @param onChangeVisibility A callback that is called when the visibility is changed during edit mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.VisibilityCard(
    isEditModeActive: Boolean,
    isCurrentlyVisibleOrPublic: Boolean,
    willBeVisibleOrPublic: Boolean? = null,
    canModifyOrigin: Boolean = false,
    onChangeVisibility: (isPublicOrVisible: Boolean) -> Unit
) {
    val blendValue =
        animateFloatAsState(targetValue = if (isEditModeActive) 1f else 0f, label = "blendValue")
    val color = blendColor(
        MaterialTheme.colorScheme.surfaceVariant.toTransparent(),
        MaterialTheme.colorScheme.surfaceVariant,
        blendValue.value
    )

    val displayIsPublicOrShared = if (isEditModeActive) willBeVisibleOrPublic ?: isCurrentlyVisibleOrPublic else isCurrentlyVisibleOrPublic

    val sheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    var isSheetVisible by rememberSaveable { mutableStateOf(false) }
    if (isSheetVisible) Sheet(
        sheetState = sheetState,
        onDismiss = { isSheetVisible = false },
        isOwner = canModifyOrigin,
        isShownOrShared = displayIsPublicOrShared,
        onShowOrShare = { onChangeVisibility(true) },
        onHideOrPrivate = { onChangeVisibility(false) }
    )

    Box(
        modifier = Modifier
            .height(80.dp)
            .weight(1f, true)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .then(if (isEditModeActive) Modifier.clickable { isSheetVisible = true } else Modifier)
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BigCard(
            modifier = Modifier
                .fillMaxWidth()
                .scale(1f - (0.1f * blendValue.value)),
            icon = when (canModifyOrigin) {
                true -> if (displayIsPublicOrShared) Icons.Default.Share else Icons.Default.Cloud
                false -> if (displayIsPublicOrShared) Icons.Default.Visibility else Icons.Default.VisibilityOff
            },
            title = stringResource(id = R.string.homework_detailViewVisibility),
            subtitle = when (canModifyOrigin) {
                true -> if (displayIsPublicOrShared) stringResource(id = R.string.homework_detailViewVisibilityPublic) else stringResource(id = R.string.homework_detailViewVisibilityPrivate)
                false -> if (displayIsPublicOrShared) stringResource(id = R.string.homework_detailViewVisibilityVisible) else stringResource(id = R.string.homework_detailViewVisibilityHidden)
            }
        )
    }
}

@Composable
@Preview
fun VisibilityCardPrivatePreview() {
    Row {
        VisibilityCard(
            isEditModeActive = true,
            isCurrentlyVisibleOrPublic = false,
            willBeVisibleOrPublic = false,
            onChangeVisibility = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
fun VisibilityCardPublicPreview() {
    Row {
        VisibilityCard(
            isEditModeActive = false,
            isCurrentlyVisibleOrPublic = true,
            willBeVisibleOrPublic = null,
            onChangeVisibility = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
fun VisibilityCardPublicHiddenPreview() {
    Row {
        VisibilityCard(
            isEditModeActive = false,
            isCurrentlyVisibleOrPublic = true,
            willBeVisibleOrPublic = false,
            onChangeVisibility = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
fun VisibilityCardLocalPreview() {
    Row {
        VisibilityCard(
            isEditModeActive = false,
            isCurrentlyVisibleOrPublic = false,
            willBeVisibleOrPublic = null,
            onChangeVisibility = {}
        )
    }
}