package es.jvbabi.vplanplus.ui.screens.logs

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.LogRecord
import es.jvbabi.vplanplus.domain.repository.LogRecordRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val logRecordRepository: LogRecordRepository
): ViewModel() {
    private val _state = mutableStateOf(LogsState())
    val state: State<LogsState> = _state
    init {
        viewModelScope.launch {
            logRecordRepository.getLogs().collect {
                _state.value = LogsState(it)
            }
        }
    }
}

data class LogsState(
    val logs: List<LogRecord> = emptyList()
)