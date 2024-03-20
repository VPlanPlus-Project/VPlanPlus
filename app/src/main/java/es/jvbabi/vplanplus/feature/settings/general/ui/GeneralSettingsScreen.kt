package es.jvbabi.vplanplus.feature.settings.general.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.home.Colors
import es.jvbabi.vplanplus.feature.settings.general.domain.data.AppThemeMode
import es.jvbabi.vplanplus.ui.common.InputDialog
import es.jvbabi.vplanplus.ui.common.SegmentedButtonItem
import es.jvbabi.vplanplus.ui.common.SegmentedButtons
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType

@Composable
fun GeneralSettingsScreen(
    navHostController: NavHostController,
    generalSettingsViewModel: GeneralSettingsViewModel = hiltViewModel()
) {
    val state = generalSettingsViewModel.state.value
    val dark = isSystemInDarkTheme()
    val fragmentActivity = LocalContext.current as FragmentActivity
    LaunchedEffect(key1 = dark, block = {
        generalSettingsViewModel.init(dark)
    })
    if (state.settings == null) return
    GeneralSettingsContent(
        onBackClicked = { navHostController.navigateUp() },
        state = state,
        onShowNotificationsOnAppOpenedClicked = {
            generalSettingsViewModel.onShowNotificationsOnAppOpenedClicked(!state.settings.showNotificationsIfAppIsVisible)
        },
        onSyncDaysAheadSet = generalSettingsViewModel::onSyncDaysAheadSet,
        onColorSchemeChanged = generalSettingsViewModel::onColorSchemeChanged,
        onAppThemeModeChanged = generalSettingsViewModel::onAppThemeModeChanged,
        onSetProtectGrades = { generalSettingsViewModel.onToggleGradeProtection(fragmentActivity) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsContent(
    onBackClicked: () -> Unit = {},
    state: GeneralSettingsState,
    onShowNotificationsOnAppOpenedClicked: () -> Unit = {},
    onSyncDaysAheadSet: (Int) -> Unit = {},
    onColorSchemeChanged: (Colors) -> Unit = {},
    onAppThemeModeChanged: (AppThemeMode) -> Unit = {},
    onSetProtectGrades: () -> Unit = {}
) {
    if (state.settings == null) return
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
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCategory(title = stringResource(id = R.string.settings_generalNotificationsTitle)) {
                SettingsSetting(
                    icon = Icons.Outlined.Notifications,
                    title = stringResource(id = R.string.settings_generalNotificationsOnAppOpenedTitle),
                    subtitle = stringResource(
                        id = R.string.settings_generalNotificationsOnAppOpenedSubtitle
                    ),
                    type = SettingsType.TOGGLE,
                    checked = state.settings.showNotificationsIfAppIsVisible,
                    doAction = { onShowNotificationsOnAppOpenedClicked() }
                )
            }
            SettingsCategory(title = stringResource(id = R.string.settings_generalPersonalization)) {
                SettingsSetting(
                    icon = Icons.Outlined.Brush,
                    title = stringResource(id = R.string.settings_generalPersonalizationThemeTitle),
                    type = SettingsType.FUNCTION,
                    doAction = {},
                    clickable = false
                ) {
                    LazyRow(Modifier.padding(bottom = 8.dp)) {
                        item {
                            Spacer(modifier = Modifier.size(30.dp))
                        }

                        items(state.settings.colorScheme.toList().sortedBy { it.first.ordinal }) { (color, record) ->
                            val surface = MaterialTheme.colorScheme.surface
                            val onSurface = MaterialTheme.colorScheme.onSurface
                            val factor = animateFloatAsState(
                                targetValue = if (record.active) 1f else 0.0f,
                                label = "checkmark"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(record.primary ?: onSurface)
                                    .drawWithContent {
                                        if (record.active) {
                                            drawCircle(
                                                color = surface.copy(alpha = factor.value),
                                                radius = 22.dp.toPx(),
                                                center = center,
                                            )
                                            drawCircle(
                                                color = record.primary ?: onSurface,
                                                radius = 20.dp.toPx(),
                                                center = center,
                                            )
                                        }
                                        drawContent()
                                    }
                                    .clickable { onColorSchemeChanged(color) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size((20*factor.value).dp)
                                )
                                if (!record.active && color == Colors.DYNAMIC) {
                                    Icon(
                                        imageVector = Icons.Outlined.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.size((20*(1-factor.value)).dp)
                                    )

                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.size(12.dp))
                        }
                    }
                }
                
                SettingsSetting(
                    icon = Icons.Default.BrightnessAuto,
                    title = stringResource(id = R.string.settingsGeneral_appThemeTitle),
                    subtitle = stringResource(id = R.string.settingsGeneral_appThemeSubtitle),
                    type = SettingsType.DISPLAY,
                    checked = false,
                    clickable = false,
                    doAction = {}
                ) {
                    SegmentedButtons(Modifier.padding(start = 56.dp, end = 8.dp)) {
                        SegmentedButtonItem(
                            icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null) },
                            label = { Text(stringResource(id = R.string.settingsGeneral_appThemeAuto)) },
                            selected = state.settings.appThemeMode == AppThemeMode.SYSTEM,
                            onClick = { onAppThemeModeChanged(AppThemeMode.SYSTEM) }
                        )
                        SegmentedButtonItem(
                            icon = { Icon(imageVector = Icons.Default.WbSunny, contentDescription = null) },
                            label = { Text(stringResource(id = R.string.settingsGeneral_appThemeLight)) },
                            selected = state.settings.appThemeMode == AppThemeMode.LIGHT,
                            onClick = { onAppThemeModeChanged(AppThemeMode.LIGHT) }
                        )
                        SegmentedButtonItem(
                            icon = { Icon(imageVector = Icons.Default.DarkMode, contentDescription = null) },
                            label = { Text(stringResource(id = R.string.settingsGeneral_appThemeDark)) },
                            selected = state.settings.appThemeMode == AppThemeMode.DARK,
                            onClick = { onAppThemeModeChanged(AppThemeMode.DARK) }
                        )

                    }
                }
            }
            SettingsCategory(title = stringResource(id = R.string.settings_generalSync)) {
                SettingsSetting(
                    icon = Icons.Outlined.Sync,
                    type = SettingsType.NUMERIC_INPUT,
                    title = stringResource(id = R.string.settings_generalSyncDayDifference),
                    subtitle = stringResource(
                        id = R.string.settings_generalSyncDayDifferenceSubtitle,
                        state.settings.daysAheadSync
                    ),
                    doAction = {
                        dialogCall = {
                            InputDialog(
                                icon = Icons.Default.Sync,
                                title = stringResource(id = R.string.settings_generalSyncDaysTitle),
                                value = state.settings.daysAheadSync.toString(),
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
            SettingsCategory(title = stringResource(id = R.string.settings_generalGrades)) {
                SettingsSetting(
                    icon = Icons.Default.Fingerprint,
                    title = stringResource(id = R.string.settings_generalGradesProtectTitle),
                    subtitle = stringResource(id = R.string.settings_generalGradesProtectSubtitle),
                    type = SettingsType.TOGGLE,
                    doAction = onSetProtectGrades,
                    checked = state.settings.isBiometricEnabled
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