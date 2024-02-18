package es.jvbabi.vplanplus.feature.grades.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseCases
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GradesViewModel @Inject constructor(
    private val gradeUseCases: GradeUseCases
) : ViewModel() {

    private val _state = mutableStateOf(GradesState())
    val state: State<GradesState> = _state

    init {
        viewModelScope.launch {
            gradeUseCases.isEnabledUseCase().collect {
                _state.value = _state.value.copy(enabled = it)
            }
        }
    }
}

data class GradesState(
    val enabled: GradeUseState? = null
)