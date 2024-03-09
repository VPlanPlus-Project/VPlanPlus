package es.jvbabi.vplanplus.feature.settings.homework.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType

@Composable
fun HomeworkSettingsScreen(
    navHostController: NavHostController,
    viewModel: HomeworkSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    HomeworkSettingsContent(
        onBack = { navHostController.navigateUp() },
        onToggleNotificationOnNewHomework = viewModel::onToggleNotificationOnNewHomework,
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkSettingsContent(
    onBack: () -> Unit,
    onToggleNotificationOnNewHomework: () -> Unit,
    state : HomeworkSettingsState
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.settingsHomework_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        BackIcon()
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSetting(
                icon = Icons.Default.NotificationAdd,
                title = stringResource(id = R.string.settingsHomework_showNotificationOnNewHomeworkTitle),
                subtitle = stringResource(id = R.string.settingsHomework_showNotificationOnNewHomeworkSubtitle),
                type = SettingsType.TOGGLE,
                checked = state.notificationOnNewHomework,
                doAction = onToggleNotificationOnNewHomework
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeworkSettingsScreenPreview() {
    HomeworkSettingsContent(
        onBack = {},
        onToggleNotificationOnNewHomework = {},
        state = HomeworkSettingsState(
            notificationOnNewHomework = true
        )
    )
}