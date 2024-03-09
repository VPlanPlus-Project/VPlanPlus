package es.jvbabi.vplanplus.feature.settings.homework.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.HomeworkSettingsUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
                    homeworkSettingsUseCases.isShowNotificationOnNewHomeworkUseCase()
                )
            ) { data ->
                val notificationOnNewHomework = data[0] as Boolean

                state.value.copy(
                    notificationOnNewHomework = notificationOnNewHomework
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
}

data class HomeworkSettingsState(
    val notificationOnNewHomework: Boolean = false
)