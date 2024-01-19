package es.jvbabi.vplanplus.ui.screens.settings.advanced

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.settings.advanced.components.DeletePlanDataDialog

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
        onDeletePlansClicked = { viewModel.showDeletePlanDataDialog() },
        onDeletePlansYes = { viewModel.deletePlanData() },
        onDeletePlansNo = { viewModel.closeDeletePlanDataDialog() }
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
    onDeletePlansNo: () -> Unit = {}
) {
    if (state.showDeletePlanData) DeletePlanDataDialog(
        { onDeletePlansYes() },
        { onDeletePlansNo() }
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
                .padding(paddingValues)
        ) {
            SettingsSetting(
                icon = Icons.Outlined.FormatListNumbered,
                title = stringResource(id = R.string.advancedSettings_logsTitle),
                subtitle = stringResource(id = R.string.advancedSettings_logsSubtitle),
                type = SettingsType.FUNCTION,
                doAction = onLogsClicked
            )
            Divider()
            SettingsSetting(
                icon = Icons.Outlined.DeleteForever,
                title = stringResource(id = R.string.advancedSettings_clearDataTitle),
                subtitle = stringResource(id = R.string.advancedSettings_clearDataText),
                type = SettingsType.FUNCTION,
                doAction = { onDeletePlansClicked() }
            )
            Divider()
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

@Composable
private fun Divider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
}