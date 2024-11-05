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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType

@Composable
fun NotificationSettingsScreen(
    navController: NavHostController,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    NotificationSettingsContent(
        onBack = { navController.navigateUp() },
        onOpenSystemNotificationSettings = {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            context.startActivity(intent)
        },
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsContent(
    onBack: () -> Unit,
    onOpenSystemNotificationSettings: () -> Unit,
    state: NotificationSettingsState
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.settingsNotifications_title)) },
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
            androidx.compose.animation.AnimatedVisibility(
                visible = !state.canSendNotifications,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                InfoCard(
                    imageVector = Icons.Default.NotificationsOff,
                    title = stringResource(R.string.settingsNotifications_notificationsDisabledBannerTitle),
                    text = stringResource(R.string.settingsNotifications_notificationsDisabledBannerText),
                    buttonText1 = stringResource(R.string.to_settings),
                    buttonAction1 = onOpenSystemNotificationSettings,
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
                    title = stringResource(R.string.settingsNotifications_notificationCategoriesTitle),
                    subtitle = stringResource(R.string.settingsNotifications_notificationCategoriesSubtitle),
                    type = SettingsType.FUNCTION,
                    doAction = onOpenSystemNotificationSettings
                )
            }
        }
    }
}