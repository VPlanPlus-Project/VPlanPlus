package es.jvbabi.vplanplus.feature.settings.vpp_id.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.BsLoginUseCases
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BsLoginViewModel @Inject constructor(
    private val bsLoginUseCases: BsLoginUseCases
) : ViewModel() {
    var state = mutableStateOf(BsLoginState())

    init {
        viewModelScope.launch {
            state.value = state.value.copy(
                server = bsLoginUseCases.getVppIdServerUseCase().first()
            )
        }
    }
}

data class BsLoginState(
    val server: String = ""
)