package es.jvbabi.vplanplus.ui.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val classUseCases: ClassUseCases,
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var activeProfile: Profile? = null

    suspend fun init() {
        activeProfile = profileUseCases.getActiveProfile()
        _state.value = _state.value.copy(initDone = true, activeProfileFound = activeProfile != null)
        if (activeProfile != null) {
            if (activeProfile!!.type == 0) {
                val profileClass = classUseCases.getClassById(activeProfile!!.referenceId)
                _state.value = _state.value.copy(activeProfileShortText = profileClass.className)
            }
        }
    }
}

data class HomeState(
    val initDone: Boolean = false,
    val activeProfileFound: Boolean = false,
    val activeProfileShortText: String = ""
)