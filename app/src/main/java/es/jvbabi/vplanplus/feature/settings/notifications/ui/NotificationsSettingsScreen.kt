package es.jvbabi.vplanplus.feature.settings.notifications.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EditNotifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.Link
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType

@Composable
fun NotificationsSettingsScreen(
    navHostController: NavHostController,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    NotificationsSettingsContent(
        state = viewModel.state,
        onBack = remember { { navHostController.navigateUp() } },
        onAction = viewModel::doAction,
        onOpenNotificationSettings = remember { {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            context.startActivity(intent)
        } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsSettingsContent(
    state: NotificationSettingsState,
    onBack: () -> Unit = {},
    onAction: (action: NotificationSettingsEvent) -> Unit = {},
    onOpenNotificationSettings: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "Notifications")
                },
                navigationIcon = {
                    IconButton(onBack) { BackIcon() }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = !state.canSendNotifications,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                InfoCard(
                    imageVector = Icons.Default.NotificationsOff,
                    title = "Notifications are disabled",
                    text = "You can enable notifications in the app settings.",
                    buttonText1 = "Open settings",
                    buttonAction1 = onOpenNotificationSettings,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = state.canSendNotifications,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                SettingsSetting(
                    icon = Icons.Default.EditNotifications,
                    title = "Device notifications",
                    subtitle = "Enable or disable notifications for different events.",
                    type = SettingsType.FUNCTION,
                    doAction = onOpenNotificationSettings
                )
            }
            SettingsCategory(title = "Preparation") {
                SettingsSetting(
                    icon = Icons.Default.AutoAwesome,
                    title = "Automatic reminder time",
                    subtitle = "Automatically determine when the reminder time is based on your schedule and usage behavior.",
                    type = SettingsType.TOGGLE,
                    customContentAutoPadding = true,
                    doAction = { onAction(NotificationSettingsEvent.ToggleAutomaticReminderTime) },
                    checked = state.isAutomaticReminderTimeEnabled,
                    enabled = state.canSendNotifications,
                    customContent = {
                        Link("","Learn more") // TODO: add link
                    }
                )
                if (state.isDeveloperModeEnabled) {
                    SettingsSetting(
                        title = "Update automatic times",
                        subtitle = "Update automatic times based on schedule and drop user behaviour information.",
                        type = SettingsType.FUNCTION,
                        doAction = { onAction(NotificationSettingsEvent.UpdateAutomaticTimes) }
                    )
                    SettingsSetting(
                        title = "Trigger NDP reminder notification",
                        subtitle = "Trigger NDP reminder notification.",
                        type = SettingsType.FUNCTION,
                        doAction = { onAction(NotificationSettingsEvent.TriggerNdpReminderNotification) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun NotificationsSettingsScreenPreview() {
    NotificationsSettingsContent(
        state = NotificationSettingsState(
            canSendNotifications = true,
            isDeveloperModeEnabled = true
        )
    )
}