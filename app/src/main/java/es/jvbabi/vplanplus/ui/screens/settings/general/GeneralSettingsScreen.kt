package es.jvbabi.vplanplus.ui.screens.settings.general

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.InputDialog
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType

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
                    checked = state.notificationShowNotificationIfAppIsVisible,
                    doAction = { onShowNotificationsOnAppOpenedClicked() }
                )
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

@Preview
@Composable
fun GeneralSettingsPreview() {
    GeneralSettingsContent({}, GeneralSettingsState())
}