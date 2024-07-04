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
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
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
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.LocalHomework
import es.jvbabi.vplanplus.feature.main_homework.view.ui.components.BigCard
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.rememberModalBottomSheetStateWithoutFullExpansion
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.util.blendColor
import es.jvbabi.vplanplus.util.toTransparent
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.VisibilityCard(
    isEditModeActive: Boolean,
    homework: Homework,
    isOwner: Boolean = false
) {
    val isLocal = homework is LocalHomework
    val blendValue =
        animateFloatAsState(targetValue = if (isEditModeActive && !isLocal) 1f else 0f, label = "blendValue")
    val color = blendColor(
        MaterialTheme.colorScheme.surfaceVariant.toTransparent(),
        MaterialTheme.colorScheme.surfaceVariant,
        blendValue.value
    )

    val sheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    var isSheetVisible by rememberSaveable { mutableStateOf(false) }
    if (isSheetVisible) Sheet(
        sheetState = sheetState,
        onDismiss = { isSheetVisible = false },
        isOwner = isOwner,
        isShownOrShared = when (homework) {
            is CloudHomework -> if (isOwner) homework.isPublic else !homework.isHidden
            else -> false
        },
        onShowOrShare = { /*TODO*/ },
        onHideOrPrivate = { /*TODO*/ }
    )

    Box(
        modifier = Modifier
            .height(80.dp)
            .weight(1f, true)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .then(if (isEditModeActive && !isLocal) Modifier.clickable { isSheetVisible = true } else Modifier)
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BigCard(
            modifier = Modifier
                .fillMaxWidth()
                .scale(1f - (0.1f * blendValue.value)),
            icon = when (homework) {
                is CloudHomework -> if (homework.isHidden) Icons.Default.VisibilityOff else if (homework.isPublic) Icons.Default.Share else Icons.Default.Cloud
                else -> Icons.Default.PhoneAndroid
            },
            title = stringResource(id = R.string.homework_detailViewVisibility),
            subtitle = when (homework) {
                is CloudHomework ->
                    if (homework.isPublic) {
                        stringResource(id = R.string.homework_detailViewVisibilityPublic) + if (homework.isHidden) " $DOT " + stringResource(id = R.string.homework_detailViewVisibilityHidden) else ""
                    }
                    else stringResource(id = R.string.homework_detailViewVisibilityPrivate)
                else -> stringResource(id = R.string.homework_detailViewVisibilityLocal)
            }
        )
    }
}

@Composable
@Preview
fun VisibilityCardPrivatePreview() {
    val group = GroupPreview.generateGroup()
    val creator = VppIdPreview.generateVppId(group)
    Row {
        VisibilityCard(
            isEditModeActive = true,
            homework = CloudHomework(
                id = 1,
                group = group,
                until = ZonedDateTime.now(),
                documents = emptyList(),
                createdAt = ZonedDateTime.now(),
                isPublic = false,
                isHidden = false,
                defaultLesson = null,
                createdBy = creator,
                tasks = emptyList()
            ),
            isOwner = true
        )
    }
}

@Composable
@Preview(showBackground = true)
fun VisibilityCardPublicPreview() {
    val group = GroupPreview.generateGroup()
    val creator = VppIdPreview.generateVppId(group)
    Row {
        VisibilityCard(
            isEditModeActive = false,
            homework = CloudHomework(
                id = 1,
                group = group,
                until = ZonedDateTime.now(),
                documents = emptyList(),
                createdAt = ZonedDateTime.now(),
                isPublic = true,
                isHidden = false,
                defaultLesson = null,
                createdBy = creator,
                tasks = emptyList()
            ),
            isOwner = true
        )
    }
}

@Composable
@Preview(showBackground = true)
fun VisibilityCardPublicHiddenPreview() {
    val group = GroupPreview.generateGroup()
    val creator = VppIdPreview.generateVppId(group)
    Row {
        VisibilityCard(
            isEditModeActive = false,
            homework = CloudHomework(
                id = 1,
                group = group,
                until = ZonedDateTime.now(),
                documents = emptyList(),
                createdAt = ZonedDateTime.now(),
                isPublic = true,
                isHidden = true,
                defaultLesson = null,
                createdBy = creator,
                tasks = emptyList()
            ),
            isOwner = true
        )
    }
}

@Composable
@Preview(showBackground = true)
fun VisibilityCardLocalPreview() {
    val group = GroupPreview.generateGroup()
    val creator = ProfilePreview.generateClassProfile(group)
    Row {
        VisibilityCard(
            isEditModeActive = false,
            homework = LocalHomework(
                id = 1,
                group = group,
                until = ZonedDateTime.now(),
                documents = emptyList(),
                createdAt = ZonedDateTime.now(),
                defaultLesson = null,
                tasks = emptyList(),
                profile = creator
            ),
            isOwner = false
        )
    }
}