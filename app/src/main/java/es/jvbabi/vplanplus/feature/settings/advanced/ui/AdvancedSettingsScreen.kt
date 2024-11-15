package es.jvbabi.vplanplus.feature.settings.advanced.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LiveHelp
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.CrashAnalyticsDialog
import es.jvbabi.vplanplus.feature.settings.advanced.domain.data.FcmTokenReloadState
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.DeletePlanDataDialog
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.VppIdServerDialog
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.servers
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.IconSettingsState
import es.jvbabi.vplanplus.ui.common.PainterSettingsState
import es.jvbabi.vplanplus.ui.common.Setting
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.screens.Screen
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
        state = state,
        onBack = { navHostController.navigateUp() },
        onLogsClicked = { navHostController.navigate(Screen.SettingsAdvancedLogScreen.route) },
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedSettingsScreenContent(
    state: AdvancedSettingsState,
    onBack: () -> Unit = {},
    onLogsClicked: () -> Unit = {},
    onEvent: (AdvancedSettingsEvent) -> Unit = {}
) {
    val context = LocalContext.current

    var showCrashAnalyticsDialog by rememberSaveable { mutableStateOf(false) }
    if (showCrashAnalyticsDialog) CrashAnalyticsDialog(
        onAccept = {
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
            showCrashAnalyticsDialog = false
        },
        onDeny = {
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(false)
            showCrashAnalyticsDialog = false
        }
    )

    var showDeleteCacheDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteCacheDialog) DeletePlanDataDialog(
        { showDeleteCacheDialog = false; onEvent(AdvancedSettingsEvent.DeleteCache) },
        { showDeleteCacheDialog = false }
    )

    var showVppIdServerDialog by rememberSaveable { mutableStateOf(false) }
    if (showVppIdServerDialog) VppIdServerDialog(
        selectedServer = state.selectedVppIdServer,
        onSetServer = { onEvent(AdvancedSettingsEvent.SetVppIdServer(it)); showVppIdServerDialog = false },
        onDismiss = { showVppIdServerDialog = false }
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
                    icon = Icons.Outlined.DeveloperMode,
                    title = stringResource(id = R.string.advancedSettings_settingsDeveloperModeTitle),
                    subtitle = stringResource(id = R.string.advancedSettings_settingsDeveloperModeSubtitle),
                    type = SettingsType.TOGGLE,
                    checked = state.isDeveloperModeEnabled,
                    doAction = { onEvent(AdvancedSettingsEvent.ToggleDeveloperMode) }
                )
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
                    doAction = { showDeleteCacheDialog = true }
                )
                Setting(
                    PainterSettingsState(
                        painter = painterResource(id = R.drawable.database),
                        title = stringResource(id = R.string.advancedSettings_settingsServerTitle),
                        subtitle =
                            if (state.selectedVppIdServer.apiHost == servers.first().apiHost) stringResource(id = R.string.advancedSettings_settingsServerDefault)
                            else state.selectedVppIdServer.apiHost,
                        type = SettingsType.FUNCTION,
                        doAction = { showVppIdServerDialog = true }
                    )
                )
                Setting(
                    PainterSettingsState(
                        title = stringResource(id = R.string.advancedSettings_settingsUpdateFCMTokenTitle),
                        subtitle =
                            when (state.fcmTokenReloadState) {
                                FcmTokenReloadState.NONE -> stringResource(id = R.string.advancedSettings_settingsUpdateFCMTokenSubtitle)
                                FcmTokenReloadState.LOADING -> stringResource(id = R.string.advancedSettings_settingsUpdateFCMTokenLoading)
                                FcmTokenReloadState.SUCCESS -> stringResource(id = R.string.advancedSettings_settingsUpdateFCMTokenSuccess)
                                FcmTokenReloadState.ERROR -> stringResource(id = R.string.advancedSettings_settingsUpdateFCMTokenError)
                            },
                        painter = painterResource(id = R.drawable.logo_firebase),
                        type = SettingsType.FUNCTION,
                        doAction = { onEvent(AdvancedSettingsEvent.UpdateFcmToken) },
                        enabled = state.fcmTokenReloadState != FcmTokenReloadState.LOADING,
                        isLoading = state.fcmTokenReloadState == FcmTokenReloadState.LOADING
                    )
                )
                Setting(state = PainterSettingsState(
                    title = stringResource(R.string.advancedSettings_sendFirebaseReportsTitle),
                    subtitle = when (state.hasUnsentCrashLogs){
                        CrashlyticsState.HAS_CRASHES -> stringResource(R.string.advancedSettings_sendFirebaseReportsHasCrashes)
                        CrashlyticsState.LOADING -> stringResource(R.string.loadingData)
                        else -> stringResource(R.string.advancedSettings_sendFirebaseReportsHasNoCrashes)
                    },
                    painter = painterResource(id = R.drawable.destruction),
                    type = SettingsType.FUNCTION,
                    enabled = state.hasUnsentCrashLogs == CrashlyticsState.HAS_CRASHES,
                    isLoading = state.hasUnsentCrashLogs == CrashlyticsState.LOADING,
                    doAction = { onEvent(AdvancedSettingsEvent.SendCrashReports) }
                ))
                Setting(
                    state = IconSettingsState(
                        title = stringResource(id = R.string.advancedSettings_updateCrashlyticsSettingsTitle),
                        subtitle = stringResource(id = R.string.advancedSettings_updateCrashlyticsSettingsSubtitle),
                        imageVector = Icons.Default.Update,
                        type = SettingsType.FUNCTION,
                        doAction = { showCrashAnalyticsDialog = true }
                    )
                )
                Setting(state = IconSettingsState(
                    title =  stringResource(id = R.string.advancedSettings_settingsListenForFCMDebugTitle),
                    subtitle = stringResource(id = R.string.advancedSettings_settingsListenForFCMDebugSubtitle),
                    imageVector = Icons.Default.FilterAlt,
                    type = SettingsType.CHECKBOX,
                    checked = state.isFcmDebugModeEnabled,
                    doAction = { onEvent(AdvancedSettingsEvent.ToggleFcmDebugMode) }
                ))
                Setting(
                    IconSettingsState(
                        title = stringResource(id = R.string.advancedSettings_resetBalloonsTitle),
                        subtitle = stringResource(id = R.string.advancedSettings_resetBalloonsSubtitle),
                        imageVector = Icons.AutoMirrored.Default.LiveHelp,
                        type = SettingsType.FUNCTION,
                        enabled = true,
                        isLoading = false,
                        doAction = {
                            onEvent(AdvancedSettingsEvent.ResetBalloons)
                            Toast.makeText(context, context.getString(R.string.advancedSettings_resetBalloonsSuccess), Toast.LENGTH_SHORT).show()
                        }
                    )
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
            SettingsCategory(title = stringResource(id = R.string.advancedSettings_testingTitle)) {
                SettingsSetting(
                    icon = Icons.Default.Notifications,
                    title = stringResource(id = R.string.advancedSettings_testingRunHomeworkReminderTitle),
                    subtitle = stringResource(id = R.string.advancedSettings_testingRunHomeworkReminderSubtitle),
                    type = SettingsType.FUNCTION,
                    doAction = { onEvent(AdvancedSettingsEvent.TriggerHomeworkReminder) }
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
            currentLessonText = "3",
            isFcmDebugModeEnabled = true
        )
    )
}