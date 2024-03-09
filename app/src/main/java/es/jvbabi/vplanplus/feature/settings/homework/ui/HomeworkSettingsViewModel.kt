@file:Suppress("UNCHECKED_CAST")

package es.jvbabi.vplanplus.feature.settings.homework.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.PreferredHomeworkNotificationTime
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.HomeworkSettingsUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeworkSettingsViewModel @Inject constructor(
    private val homeworkSettingsUseCases: HomeworkSettingsUseCases
) : ViewModel() {
    val state = mutableStateOf(HomeworkSettingsState())
    init {
        viewModelScope.launch {
            combine(
                listOf(
                    homeworkSettingsUseCases.isShowNotificationOnNewHomeworkUseCase(),
                    homeworkSettingsUseCases.isRemindOnUnfinishedHomeworkUseCase(),
                    homeworkSettingsUseCases.getDefaultNotificationTimeUseCase(),
                    homeworkSettingsUseCases.getPreferredHomeworkNotificationTimeUseCase()
                )
            ) { data ->
                val notificationOnNewHomework = data[0] as Boolean
                val remindUserOnUnfinishedHomework = data[1] as Boolean
                val defaultNotificationTime = data[2] as LocalDateTime
                val exceptions = data[3] as List<PreferredHomeworkNotificationTime>

                state.value.copy(
                    notificationOnNewHomework = notificationOnNewHomework,
                    remindUserOnUnfinishedHomework = remindUserOnUnfinishedHomework,
                    defaultNotificationTime = defaultNotificationTime,
                    exceptions = exceptions
                )
            }.collect {
                state.value = it
            }
        }
    }

    fun onToggleNotificationOnNewHomework() {
        viewModelScope.launch {
            homeworkSettingsUseCases.setShowNotificationOnNewHomeworkUseCase(!state.value.notificationOnNewHomework)
        }
    }

    fun onToggleRemindUserOnUnfinishedHomework() {
        viewModelScope.launch {
            homeworkSettingsUseCases.setRemindOnUnfinishedHomeworkUseCase(!state.value.remindUserOnUnfinishedHomework)
        }
    }

    fun onSetDefaultRemindTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            homeworkSettingsUseCases.setDefaultNotificationTimeUseCase(hour, minute)
        }
    }

    fun onToggleException(dayOfWeek: DayOfWeek) {
        viewModelScope.launch {
            if (state.value.exceptions.any { it.dayOfWeek == dayOfWeek }) {
                homeworkSettingsUseCases.removePreferredHomeworkNotificationTimeUseCase(dayOfWeek)
            } else {
                homeworkSettingsUseCases.setPreferredHomeworkNotificationTimeUseCase(dayOfWeek, state.value.defaultNotificationTime.hour, state.value.defaultNotificationTime.minute)
            }
        }
    }
}

data class HomeworkSettingsState(
    val notificationOnNewHomework: Boolean = false,
    val remindUserOnUnfinishedHomework: Boolean = false,
    val defaultNotificationTime: LocalDateTime = LocalDateTime.now(),
    val exceptions: List<PreferredHomeworkNotificationTime> = emptyList()
)