package es.jvbabi.vplanplus.ui.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    schoolUseCases: SchoolUseCases,
    profileUseCases: ProfileUseCases
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    init {
        profileUseCases.atLeastOneProfileExists().onEach {
            _state.value = state.value.copy(hasProfiles = it)
        }.launchIn(viewModelScope)
        schoolUseCases.atLeastOneSchoolExists().onEach {
            _state.value = state.value.copy(hasSchool = it)
        }.launchIn(viewModelScope)
    }
}

data class HomeState(
    val hasProfiles: Boolean = true,
    val hasSchool: Boolean = true
)