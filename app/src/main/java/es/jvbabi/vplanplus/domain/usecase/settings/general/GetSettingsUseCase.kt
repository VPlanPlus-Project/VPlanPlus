package es.jvbabi.vplanplus.domain.usecase.settings.general

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.usecase.home.Colors
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetSettingsUseCase(
    private val keyValueRepository: KeyValueRepository,
    val getColorsUseCase: GetColorsUseCase,
) {
    operator fun invoke(isDark: Boolean) = flow {
        combine(
            keyValueRepository.getFlow(Keys.SETTINGS_SYNC_DAY_DIFFERENCE),
            keyValueRepository.getFlow(Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE),
            keyValueRepository.getFlow(Keys.COLOR),
            keyValueRepository.getFlowOrDefault(Keys.GRADES_BIOMETRIC_ENABLED, "false"),
        ) { syncDayDifference, showNotification, _, gradesProtected ->
            GeneralSettings(
                daysAheadSync = syncDayDifference?.toInt() ?: Keys.SETTINGS_SYNC_DAY_DIFFERENCE_DEFAULT,
                showNotificationsIfAppIsVisible = showNotification?.toBoolean() ?: false,
                colorScheme = getColorsUseCase(isDark),
                isBiometricEnabled = gradesProtected.toBoolean()
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
    val isBiometricEnabled: Boolean,
)