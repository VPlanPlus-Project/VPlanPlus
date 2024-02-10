package es.jvbabi.vplanplus.ui.screens.id_link

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.Response
import es.jvbabi.vplanplus.domain.usecase.vpp_id.VppIdLinkUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VppIdLinkViewModel @Inject constructor(
    private val vppIdLinkUseCases: VppIdLinkUseCases,
) : ViewModel() {

    private val _state = mutableStateOf(VppIdLinkState())
    val state: State<VppIdLinkState> = _state

    private var token: String = ""

    fun init(token: String?) {
        if (token == null) {
            _state.value = _state.value.copy(isLoading = false)
            return
        }
        this.token = token
        viewModelScope.launch {
            val response = vppIdLinkUseCases.getVppIdDetailsUseCase(token)
            _state.value = _state.value.copy(
                isLoading = false,
                vppId = response.data,
                response = response.response,
            )
            if (response.data != null) {
                _state.value = _state.value.copy(
                    classes = vppIdLinkUseCases.getClassUseCase(response.data.schoolId, response.data.className)
                )
            }
        }
    }
}

data class VppIdLinkState(
    val vppId: VppId? = null,
    val response: Response? = null,
    val classes: Classes? = null,
    val isLoading: Boolean = true,
)