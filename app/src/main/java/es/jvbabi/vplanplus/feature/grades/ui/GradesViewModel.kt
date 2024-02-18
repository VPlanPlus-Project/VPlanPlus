package es.jvbabi.vplanplus.feature.grades.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

//@HiltViewModel
class GradesViewModel : ViewModel() {

    private val _state = mutableStateOf(GradesState())
    val state: State<GradesState> = _state
}

data class GradesState(
    val enabled: Boolean? = null
)