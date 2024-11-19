package es.jvbabi.vplanplus.feature.settings.profile.notifications.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.EditNotifications
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.Badge
import es.jvbabi.vplanplus.ui.common.FullSizeLoadingCircle
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.MainToggle
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.common.Slider
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.util.Size
import es.jvbabi.vplanplus.util.nearest
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID


@Composable
fun ProfileNotificationSettingsScreen(
    navHostController: NavHostController,
    profileId: UUID,
    profileNotificationSettingsViewModel: ProfileNotificationSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(profileId) { profileNotificationSettingsViewModel.init(profileId) }
    ProfileNotificationSettingsContent(
        state = profileNotificationSettingsViewModel.state,
        onBackClicked = navHostController::navigateUp,
        openDeviceNotificationSettings = {
            val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            context.startActivity(settingsIntent)
        },
        openChannelSettings = {
            val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                .putExtra(Settings.EXTRA_CHANNEL_ID, "PROFILE_${profileNotificationSettingsViewModel.state?.profile?.id.toString().lowercase()}")
            context.startActivity(settingsIntent)
        },
        doAction = profileNotificationSettingsViewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileNotificationSettingsContent(
    state: ProfileNotificationSettingsState?,
    onBackClicked: () -> Unit = {},
    openDeviceNotificationSettings: () -> Unit = {},
    openChannelSettings: () -> Unit = {},
    doAction: (action: ProfileNotificationSettingsEvent) -> Unit = {},
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.profileNotificationSettings_title)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        BackIcon()
                    }
                }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val canSendProfileNotifications = (state?.profile?.notificationsEnabled != false) && state?.notificationState == ProfileNotificationSettingsNotificationState.ENABLED
        if (state == null) {
            FullSizeLoadingCircle()
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer8Dp()
            if (state.notificationState != ProfileNotificationSettingsNotificationState.ENABLED) Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
            ) {
                when (state.notificationState) {
                    ProfileNotificationSettingsNotificationState.NOTIFICATIONS_DISABLED -> {
                        InfoCard(
                            modifier = Modifier.padding(8.dp),
                            imageVector = Icons.Default.Error,
                            title = stringResource(id = R.string.profileNotificationSettings_notificationsDisabled),
                            text = stringResource(id = R.string.profileNotificationSettings_notificationsDisabledText),
                            buttonText1 = stringResource(R.string.to_settings),
                            buttonAction1 = openDeviceNotificationSettings
                        )
                    }
                    ProfileNotificationSettingsNotificationState.PROFILE_DISABLED -> {
                        InfoCard(
                            modifier = Modifier.padding(8.dp),
                            imageVector = Icons.Default.Error,
                            title = stringResource(id = R.string.profileNotificationSettings_profileDisabled),
                            text = stringResource(id = R.string.profileNotificationSettings_profileDisabledText),
                            buttonText1 = stringResource(R.string.to_settings),
                            buttonAction1 = openChannelSettings
                        )
                    }
                    else -> Unit
                }
            }
            MainToggle(
                modifier = Modifier.padding(horizontal = 16.dp),
                checked = state.profile.notificationsEnabled && state.notificationState != ProfileNotificationSettingsNotificationState.NOTIFICATIONS_DISABLED,
                enabled = state.notificationState != ProfileNotificationSettingsNotificationState.NOTIFICATIONS_DISABLED,
                title = stringResource(R.string.profileNotificationSettings_notificationsEnabled),
                onToggle = { doAction(ProfileNotificationSettingsEvent.ToggleNotificationForProfile(it)) }
            )

            Spacer8Dp()
            SettingsSetting(
                icon = Icons.Default.EditNotifications,
                title = stringResource(R.string.profileNotificationSettings_notificationsSettings),
                subtitle = stringResource(R.string.profileNotificationSettings_notificationsSettingsSubtitle),
                type = SettingsType.FUNCTION,
                enabled = true,
                doAction = { openDeviceNotificationSettings() }
            )

            if (state.profile is ClassProfile) {
                if (state.profile.isHomeworkEnabled) SettingsCategory(
                    title = stringResource(R.string.profileNotificationSettings_homeworkCategoryTitle)
                ) {
                    SettingsSetting(
                        icon = Icons.Default.AddTask,
                        title = stringResource(R.string.profileNotificationSettings_homeworkSettingsOnNewHomeworkTitle),
                        subtitle = stringResource(R.string.profileNotificationSettings_homeworkSettingsSubtitle),
                        type = SettingsType.TOGGLE,
                        enabled = canSendProfileNotifications,
                        checked = state.profile.notificationsEnabled && state.profile.notificationSettings.onNewHomeworkNotificationSetting.isEnabled(),
                        doAction = {
                            doAction(ProfileNotificationSettingsEvent.ToggleNotificationOnNewHomework(state.profile.notificationSettings.onNewHomeworkNotificationSetting.isEnabled().not()))
                        }
                    )
                }

                if (state.profile.isAssessmentsEnabled) SettingsCategory(title = stringResource(R.string.settingsNotifications_newAssessmentCategoryTitle)) {
                    SettingsSetting(
                        icon = Icons.Default.NotificationsActive,
                        title = stringResource(R.string.settingsNotifications_newAssessmentSettingsTitle),
                        subtitle = stringResource(R.string.settingsNotifications_newAssessmentSettingsSubtitle, state.profile.displayName),
                        type = SettingsType.TOGGLE,
                        checked = state.profile.notificationsEnabled && state.profile.notificationSettings.onNewAssessmentSetting.isEnabled(),
                        doAction = { doAction(ProfileNotificationSettingsEvent.ToggleNotificationOnNewAssessment(state.profile.notificationSettings.onNewAssessmentSetting.isEnabled().not())) },
                        enabled = canSendProfileNotifications
                    )
                }

                SettingsCategory(title = stringResource(R.string.settingsNotifications_dailyReminderCategoryTitle)) {
                    SettingsSetting(
                        icon = Icons.Default.NotificationsActive,
                        title = stringResource(R.string.settingsNotifications_dailyReminderSettingsTitle),
                        subtitle = stringResource(R.string.settingsNotifications_dailyReminderSettingsSubtitle, state.profile.displayName),
                        type = SettingsType.TOGGLE,
                        checked = state.profile.notificationsEnabled && state.profile.notificationSettings.dailyNotificationSetting.isEnabled(),
                        doAction = { doAction(ProfileNotificationSettingsEvent.ToggleDailyReminder(state.profile.notificationSettings.dailyNotificationSetting.isEnabled().not())) },
                        enabled = canSendProfileNotifications
                    )

                    AnimatedVisibility(
                        visible = state.profile.notificationsEnabled && state.profile.notificationSettings.dailyNotificationSetting.isEnabled(),
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            Row(Modifier.padding(vertical = 8.dp)) sliders@{
                                val stepMinutes = 15

                                val getTimeFromMinuteOffset: (minutes: Int) -> LocalTime = { minute ->
                                    LocalTime.of(0, 0).plusMinutes(minute.toLong())
                                }

                                val getMinutesFromTime: (time: LocalTime) -> Int = { time ->
                                    time.minute + (time.hour * 60)
                                }

                                repeat(5) { dayOffset ->
                                    val day = DayOfWeek.of(((dayOffset + 6) % 7) + 1)
                                    val time = state.dailyReminderTimesForProfile[day] ?: LocalTime.of(15, 30)
                                    var isDragging by remember { mutableStateOf(false) }
                                    var draggingTime by remember { mutableStateOf(time) }
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .animateContentSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(day.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                                        Column(Modifier.height(300.dp)) slider@{
                                            Slider(
                                                orientation = Orientation.Vertical,
                                                steps = stepMinutes,
                                                puckContent = {
                                                    Text(
                                                        text = getTimeFromMinuteOffset(it.toInt().nearest(15)).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
                                                        color = MaterialTheme.colorScheme.onPrimary,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                },
                                                range = getMinutesFromTime(LocalTime.of(13, 0)).toFloat()..60*24f,
                                                puckSize = Size(40.dp, 20.dp),
                                                onValueChange = { doAction(ProfileNotificationSettingsEvent.SetDailyReminderTime(day, getTimeFromMinuteOffset(it.toInt().nearest(15)))) },
                                                currentValue = getMinutesFromTime(time).toFloat(),
                                                trackThickness = 2.dp,
                                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                                puckColor = MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.outline,
                                                puckPadding = 2.dp,
                                                onDragChange = { isDraggingSlider, value ->
                                                    isDragging = isDraggingSlider
                                                    draggingTime = getTimeFromMinuteOffset(value.toInt().nearest(15))
                                                }
                                            )
                                        }
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = isDragging,
                                            enter = scaleIn() + fadeIn(),
                                            exit = scaleOut() + fadeOut(),
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.primary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = draggingTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (state.isDeveloperModeEnabled) SettingsSetting(
                                icon = Icons.Default.PlayArrow,
                                title = stringResource(R.string.settingsNotifications_triggerTitle),
                                titleBadge = { Badge(MaterialTheme.colorScheme.error, "Developer") },
                                doAction = { doAction(ProfileNotificationSettingsEvent.TriggerNotification) },
                                enabled = true,
                                type = SettingsType.FUNCTION,
                            )
                        }
                    }
                }
            }
            SettingsCategory(
                title = stringResource(R.string.settingsNotifications_planChangedCategory)
            ) {
                SettingsSetting(
                    icon = Icons.Default.TableChart,
                    title = stringResource(R.string.settingsNotifications_planChangedTitle),
                    subtitle = stringResource(R.string.settingsNotifications_planChangedSubtitle, state.profile.displayName),
                    type = SettingsType.TOGGLE,
                    checked = state.profile.notificationsEnabled && state.profile.notificationSettings.newPlanNotificationSetting.isEnabled(),
                    doAction = { doAction(ProfileNotificationSettingsEvent.ToggleNotificationOnNewPlan(state.profile.notificationSettings.newPlanNotificationSetting.isEnabled().not())) },
                    enabled = canSendProfileNotifications
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileNotificationSettingsScreenPreview() {
    val profile = ProfilePreview.generateClassProfile(GroupPreview.generateGroup())
    ProfileNotificationSettingsContent(
        state = ProfileNotificationSettingsState(
            profile = profile,
            notificationState = ProfileNotificationSettingsNotificationState.ENABLED
        )
    )
}