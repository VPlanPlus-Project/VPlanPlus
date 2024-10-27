package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.add.ui.SaveType
import es.jvbabi.vplanplus.ui.common.Badge
import es.jvbabi.vplanplus.ui.common.Option
import es.jvbabi.vplanplus.ui.common.OptionCustomText
import es.jvbabi.vplanplus.ui.common.OptionTextTitle
import es.jvbabi.vplanplus.ui.stringResource
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExamStorageSection(
    currentState: SaveType?,
    vppIdName: String,
    groupName: String,
    onTypeSelected: (type: SaveType) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isTypeModalOpen by rememberSaveable { mutableStateOf(false) }
    if (isTypeModalOpen) {
        val typeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { isTypeModalOpen = false },
            sheetState = typeSheetState,
        ) {
            Column(Modifier.padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())) {
                Text(text = stringResource(id = R.string.examsNew_savePlaceholder), modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                ) options@{
                    Option(
                        title = OptionTextTitle(stringResource(id = R.string.examsNew_saveDevice)),
                        icon = Icons.Default.PhoneAndroid,
                        state = currentState == SaveType.LOCAL,
                        enabled = true,
                        modifier = Modifier.border(width = .25.dp, color = MaterialTheme.colorScheme.outline),
                        onClick = {
                            onTypeSelected(SaveType.LOCAL)
                            scope.launch { typeSheetState.hide(); isTypeModalOpen = false }
                        }
                    )
                    HorizontalDivider()
                    Option(
                        title = OptionCustomText(stringResource(id = R.string.examsNew_saveVppId)) { Badge(MaterialTheme.colorScheme.secondary, "Coming soon") },
                        subtitle = vppIdName,
                        icon = Icons.Default.CloudQueue,
                        state = currentState == SaveType.CLOUD,
                        enabled = false,
                        modifier = Modifier.border(width = .25.dp, color = MaterialTheme.colorScheme.outline),
                        onClick = {
                            onTypeSelected(SaveType.CLOUD)
                            scope.launch { typeSheetState.hide(); isTypeModalOpen = false }
                        }
                    )
                    HorizontalDivider()
                    Option(
                        title = OptionCustomText(stringResource(id = R.string.examsNew_saveShared)) { Badge(MaterialTheme.colorScheme.secondary, "Coming soon") },
                        subtitle = stringResource(id = R.string.examsNew_saveVppIdSharedText, vppIdName, groupName),
                        icon = Icons.Default.CloudQueue,
                        state = currentState == SaveType.SHARED,
                        enabled = false,
                        modifier = Modifier.border(width = .25.dp, color = MaterialTheme.colorScheme.outline),
                        onClick = {
                            onTypeSelected(SaveType.SHARED)
                            scope.launch { typeSheetState.hide(); isTypeModalOpen = false }
                        }
                    )
                }
            }
        }
    }
    AddExamItem(
        icon = {
            AnimatedContent(
                targetState = currentState,
                label = "storage icon"
            ) { state ->
                Icon(
                    imageVector = when (state) {
                        SaveType.LOCAL -> Icons.Default.PhoneAndroid
                        SaveType.CLOUD -> Icons.Default.CloudQueue
                        SaveType.SHARED -> Icons.Default.Share
                        null -> Icons.Outlined.Save
                    },
                    contentDescription = null,
                    tint = if (currentState == null) Color.Gray else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { isTypeModalOpen = true }
                .padding(start = 8.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            AnimatedContent(
                targetState = currentState,
                label = "storage text"
            ) { displayState ->
                if (displayState == null) Text(
                    text = stringResource(R.string.examsNew_savePlaceholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                ) else Text(
                    text = buildString {
                        append(stringResource(id = displayState.stringResource()))
                        if (displayState == SaveType.SHARED) append(" ($groupName)")
                   },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}