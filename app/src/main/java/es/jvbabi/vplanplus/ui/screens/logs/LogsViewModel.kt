package es.jvbabi.vplanplus.ui.screens.logs

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.LogRecord
import es.jvbabi.vplanplus.domain.usecase.logs.LogsUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val logsUseCases: LogsUseCases
): ViewModel() {
    private val _state = mutableStateOf(LogsState())
    val state: State<LogsState> = _state
    init {
        viewModelScope.launch {
            logsUseCases.getLogsUseCase().collect {
                _state.value = LogsState(it)
            }
        }
    }

    fun onDeleteLogsClicked() {
        viewModelScope.launch {
            logsUseCases.deleteAllLogsUseCase()
        }

    }
}

data class LogsState(
    val logs: List<LogRecord> = emptyList()
)