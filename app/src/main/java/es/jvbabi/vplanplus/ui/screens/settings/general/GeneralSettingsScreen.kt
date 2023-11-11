package es.jvbabi.vplanplus.ui.screens.settings.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.InputDialog

@Composable
fun GeneralSettingsScreen(
    navHostController: NavHostController,
    generalSettingsViewModel: GeneralSettingsViewModel = hiltViewModel()
) {
    val state = generalSettingsViewModel.state.value
    GeneralSettingsContent(
        onBackClicked = { navHostController.popBackStack() },
        state = state,

        onShowNotificationsOnAppOpenedClicked = {
            generalSettingsViewModel.onShowNotificationsOnAppOpenedClicked(!state.notificationShowNotificationIfAppIsVisible)
        },

        onSyncDaysAheadSet = {
            generalSettingsViewModel.onSyncDaysAheadSet(it)
        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsContent(
    onBackClicked: () -> Unit = {},
    state: GeneralSettingsState,

    onShowNotificationsOnAppOpenedClicked: () -> Unit = {},
    onSyncDaysAheadSet: (Int) -> Unit = {}
) {
    var dialogCall = remember<@Composable () -> Unit> { {} }
    var dialogVisible by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings_generalSettingsTitle)) },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
    ) { paddingValues ->
        if (dialogVisible) {
            dialogCall()
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SettingsCategory(title = stringResource(id = R.string.settings_generalNotificationsTitle)) {
                SettingsSetting(
                    icon = null,
                    title = stringResource(id = R.string.settings_generalNotificationsOnAppOpenedTitle),
                    subtitle = stringResource(
                        id = R.string.settings_generalNotificationsOnAppOpenedSubtitle
                    ),
                    type = SettingsType.TOGGLE,
                    checked = state.notificationShowNotificationIfAppIsVisible
                ) { onShowNotificationsOnAppOpenedClicked() }
            }
            SettingsCategory(title = stringResource(id = R.string.settings_generalSync)) {
                SettingsSetting(
                    icon = null,
                    type = SettingsType.NUMERIC_INPUT,
                    title = stringResource(id = R.string.settings_generalSyncDayDifference),
                    subtitle = stringResource(
                        id = R.string.settings_generalSyncDayDifferenceSubtitle,
                        state.syncDayDifference
                    ),
                    doAction = {
                        dialogCall = {
                            InputDialog(
                                icon = Icons.Default.Sync,
                                title = stringResource(id = R.string.settings_generalSyncDaysTitle),
                                value = state.syncDayDifference.toString(),
                                onOk = {
                                    if (it != null) onSyncDaysAheadSet(it.toInt())
                                    dialogVisible = false
                                }
                            )
                        }
                        dialogVisible = true
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsCategory(title: String, content: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingsSetting(
    icon: ImageVector?,
    title: String,
    subtitle: String? = null,
    type: SettingsType,
    checked: Boolean? = null,
    doAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { doAction() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(modifier = Modifier.weight(1f, false)) {
            Box(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                if (subtitle != null) {
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            when (type) {
                SettingsType.TOGGLE -> {
                    Switch(checked = checked ?: false, onCheckedChange = { doAction() })
                }

                SettingsType.NUMERIC_INPUT -> {
                }

                SettingsType.CHECKBOX -> {
                    // TODO
                }

                SettingsType.FUNCTION -> {
                    // TODO
                }
            }
        }
    }
}

@Preview
@Composable
fun GeneralSettingsPreview() {
    GeneralSettingsContent({}, GeneralSettingsState())
}

enum class SettingsType {
    TOGGLE,
    CHECKBOX,
    FUNCTION,
    NUMERIC_INPUT
}