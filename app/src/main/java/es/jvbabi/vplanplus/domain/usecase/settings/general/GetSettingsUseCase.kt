package es.jvbabi.vplanplus.domain.usecase.settings.general

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.usecase.home.Colors
import es.jvbabi.vplanplus.feature.settings.general.domain.data.AppThemeMode
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetSettingsUseCase(
    private val keyValueRepository: KeyValueRepository,
    val getColorsUseCase: GetColorsUseCase,
) {
    operator fun invoke(isDark: Boolean) = flow {
        combine(
            listOf(
                keyValueRepository.getFlowOrDefault(Keys.SETTINGS_SYNC_DAY_DIFFERENCE, Keys.SETTINGS_SYNC_DAY_DIFFERENCE_DEFAULT.toString()),
                keyValueRepository.getFlowOrDefault(Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE, "false"),
                keyValueRepository.getFlow(Keys.COLOR),
                keyValueRepository.getFlowOrDefault(Keys.GRADES_BIOMETRIC_ENABLED, "false"),
                keyValueRepository.getFlowOrDefault(Keys.APP_THEME_MODE, AppThemeMode.SYSTEM.name),
                keyValueRepository.getFlowOrDefault(Keys.HIDE_FINISHED_LESSONS, "true"),
                keyValueRepository.getFlowOrDefault(Keys.SETTINGS_SYNC_INTERVAL, Keys.SETTINGS_SYNC_INTERVAL_DEFAULT.toString())
            )
        ) { data ->
            val syncDayDifference = (data[0] as String).toInt()
            val showNotification = (data[1]).toBoolean()
            val gradesProtected = (data[3]).toBoolean()
            val appThemeMode = AppThemeMode.valueOf(data[4] as String)
            val hideFinishedLessons = (data[5]).toBoolean()
            val syncInterval = (data[6] as String).toInt()

            GeneralSettings(
                daysAheadSync = syncDayDifference,
                showNotificationsIfAppIsVisible = showNotification,
                colorScheme = getColorsUseCase(isDark && (appThemeMode == AppThemeMode.SYSTEM || appThemeMode == AppThemeMode.DARK)),
                isBiometricEnabled = gradesProtected,
                appThemeMode = appThemeMode,
                hideFinishedLessons = hideFinishedLessons,
                syncIntervalMinutes = syncInterval
            )
        }.collect {
            emit(it)
        }
    }
}

data class GeneralSettings(
    val daysAheadSync: Int,
    val showNotificationsIfAppIsVisible: Boolean,
    val colorScheme: Map<Colors, ColorScheme>,
    val appThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val hideFinishedLessons: Boolean = false,
    val syncIntervalMinutes: Int = 15,
    val isBiometricEnabled: Boolean,
)