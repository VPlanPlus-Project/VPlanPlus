package es.jvbabi.vplanplus.ui.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val schoolUseCases: SchoolUseCases,
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var activeProfile: Profile? = null

    suspend fun init() {
        activeProfile = profileUseCases.getActiveProfile()
        _state.value = _state.value.copy(initDone = true, activeProfileFound = activeProfile != null)
    }
}

data class HomeState(
    val initDone: Boolean = false,
    val activeProfileFound: Boolean = false
)