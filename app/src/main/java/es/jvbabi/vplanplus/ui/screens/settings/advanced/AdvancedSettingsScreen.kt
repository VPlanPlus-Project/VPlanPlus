package es.jvbabi.vplanplus.ui.screens.settings.advanced

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.settings.advanced.components.DeletePlanDataDialog
import es.jvbabi.vplanplus.ui.screens.settings.advanced.components.VppIdServerDialog
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AdvancedSettingsScreen(
    navHostController: NavHostController,
    viewModel: AdvancedSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    AdvancedSettingsScreenContent(
        onBack = { navHostController.navigateUp() },
        onLogsClicked = { navHostController.navigate(Screen.SettingsAdvancedLogScreen.route) },
        state = state,
        onDeletePlansClicked = { viewModel.showDeleteCacheDialog() },
        onDeletePlansYes = { viewModel.deleteCache() },
        onDeletePlansNo = { viewModel.closeDeleteCacheDialog() },
        onVppIdDialogStateChange = viewModel::showVppIdDialog,
        onSetServer = viewModel::setVppIdServer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedSettingsScreenContent(
    onBack: () -> Unit = {},
    onLogsClicked: () -> Unit = {},
    state: AdvancedSettingsState,
    onDeletePlansClicked: () -> Unit = {},
    onDeletePlansYes: () -> Unit = {},
    onDeletePlansNo: () -> Unit = {},
    onVppIdDialogStateChange: (Boolean) -> Unit = {},
    onSetServer: (String?) -> Unit = {}
) {
    if (state.showDeleteCacheDialog) DeletePlanDataDialog(
        { onDeletePlansYes() },
        { onDeletePlansNo() }
    )

    if (state.showVppIdServerDialog) VppIdServerDialog(
        selectedServer = state.selectedVppIdServer,
        onSetServer = onSetServer,
        onDismiss = { onVppIdDialogStateChange(false) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.advancedSettings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        BackIcon()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            SettingsCategory(
                title = stringResource(id = R.string.advancedSettings_systemTitle)
            ) {
                SettingsSetting(
                    icon = Icons.Outlined.FormatListNumbered,
                    title = stringResource(id = R.string.advancedSettings_logsTitle),
                    subtitle = stringResource(id = R.string.advancedSettings_logsSubtitle),
                    type = SettingsType.FUNCTION,
                    doAction = onLogsClicked
                )
                SettingsSetting(
                    icon = Icons.Outlined.DeleteForever,
                    title = stringResource(id = R.string.advancedSettings_clearCacheTitle),
                    subtitle = stringResource(id = R.string.advancedSettings_clearCacheText),
                    type = SettingsType.FUNCTION,
                    doAction = { onDeletePlansClicked() }
                )
                SettingsSetting(
                    painter = painterResource(id = R.drawable.database),
                    title = stringResource(id = R.string.advancedSettings_settingsServerTitle),
                    subtitle =
                    if (state.selectedVppIdServer == Keys.VPPID_SERVER_DEFAULT) {
                        stringResource(id = R.string.advancedSettings_settingsServerDefault)
                    } else state.selectedVppIdServer,
                    type = SettingsType.FUNCTION,
                    doAction = { onVppIdDialogStateChange(true) },
                    enabled = state.canChangeVppIdServer
                )
            }
            SettingsCategory(
                title = stringResource(id = R.string.advancedSettings_userTitle)
            ) {
                SettingsSetting(
                    icon = Icons.Outlined.AccountCircle,
                    title = stringResource(id = R.string.advancedSettings_infoProfileTitle),
                    subtitle = state.currentProfileText,
                    enabled = false,
                    type = SettingsType.DISPLAY,
                    doAction = {},
                )
                SettingsSetting(
                    icon = null,
                    title = stringResource(id = R.string.advancedSettings_infoCurrentLessonTitle),
                    subtitle = state.currentLessonText,
                    type = SettingsType.DISPLAY,
                    enabled = false,
                    doAction = {}
                )
            }

            SettingsCategory(
                title = stringResource(id = R.string.advancedSettings_timeTitle)
            ) {
                SettingsSetting(
                    icon = Icons.Default.MoreTime,
                    title = stringResource(id = R.string.advancedSettings_timezoneTitle),
                    subtitle =
                    ZoneId.systemDefault().id + " $DOT " + ZoneId.systemDefault()
                        .getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    type = SettingsType.DISPLAY,
                    enabled = false,
                    doAction = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun AdvancedSettingsScreenPreview() {
    AdvancedSettingsScreenContent(
        state = AdvancedSettingsState(
            currentProfileText = "Loading...",
            currentLessonText = "3"
        )
    )
}