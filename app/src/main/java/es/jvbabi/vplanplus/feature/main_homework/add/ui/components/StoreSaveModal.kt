package es.jvbabi.vplanplus.feature.main_homework.add.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.add.ui.SaveType
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.util.blendColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreSaveModal(
    canUseVppId: Boolean,
    allowNoVppIdBanner: Boolean,
    sheetState: SheetState,
    currentState: SaveType,
    onSubmit: (SaveType) -> Unit,
    onDismissRequest: () -> Unit,
    onOpenVppIdSettings: () -> Unit,
    onHideBannerForever: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = { onDismissRequest() }, sheetState = sheetState) {
        Column(Modifier.padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())) {
            Text(text = stringResource(id = R.string.addHomework_storeTitle), modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            ) {
                Option(
                    title = stringResource(id = R.string.addHomework_saveThisDevice),
                    subtitle = null,
                    icon = Icons.Default.PhoneAndroid,
                    state = currentState == SaveType.LOCAL,
                    enabled = true,
                    onClick = { onSubmit(SaveType.LOCAL) }
                )
                Divider()
                Option(
                    title = stringResource(id = R.string.addHomework_saveVppId),
                    subtitle = if (!canUseVppId) stringResource(id = R.string.addHomework_saveVppIdNoVppId) else null,
                    icon = Icons.Default.CloudQueue,
                    state = currentState == SaveType.CLOUD,
                    enabled = canUseVppId,
                    onClick = { onSubmit(SaveType.CLOUD) }
                )
                Divider()
                Option(
                    title = stringResource(id = R.string.addHomework_saveVppIdSharedTitle),
                    subtitle = if (!canUseVppId) stringResource(id = R.string.addHomework_saveVppIdNoVppId) else stringResource(id = R.string.addHomework_saveVppIdSharedDescription),
                    icon = Icons.Default.Share,
                    state = currentState == SaveType.SHARED,
                    enabled = canUseVppId,
                    onClick = { onSubmit(SaveType.SHARED) }
                )
            }
            AnimatedVisibility(
                visible = !canUseVppId && allowNoVppIdBanner,
                enter = expandVertically(tween(200)),
                exit = shrinkVertically(tween(200)),
                modifier = Modifier.padding(16.dp)
            ) {
                InfoCard(
                    imageVector = Icons.Default.NoAccounts,
                    title = stringResource(id = R.string.addHomework_noVppIdTitle),
                    text = stringResource(id = R.string.addHomework_noVppIdText),
                    buttonText1 = stringResource(id = R.string.hideForever),
                    buttonAction1 = onHideBannerForever,
                    buttonText2 = stringResource(id = R.string.addHomework_noVppIdButtonOpenSettings),
                    buttonAction2 = onOpenVppIdSettings
                )
            }
        }
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
}

@Composable
private fun Option(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    state: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val factor by animateFloatAsState(targetValue = if (state) 1f else 0f, label = "factor")
    val background = blendColor(Color.Transparent, MaterialTheme.colorScheme.primaryContainer, factor)
    val contentColor = blendColor(MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.onPrimaryContainer, factor)
    val disabledContentColor = MaterialTheme.colorScheme.outline
    RowVerticalCenter(
        Modifier
            .fillMaxWidth()
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .height(56.dp)
            .background(background)
            .padding(vertical = 8.dp, horizontal = 16.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) contentColor else disabledContentColor,
            modifier = Modifier.size(24.dp)
        )
        Column(Modifier.padding(start = 16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = if (enabled) contentColor else disabledContentColor)
            if (!subtitle.isNullOrBlank()) Text(text = subtitle, style = MaterialTheme.typography.labelMedium, color = if (enabled) contentColor else disabledContentColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OptionPreview() {
    Option(
        title = stringResource(id = R.string.addHomework_saveVppId),
        subtitle = stringResource(id = R.string.addHomework_saveVppIdNoVppId),
        icon = Icons.Default.CloudQueue,
        state = true,
        enabled = true,
        onClick = {}
    )
}