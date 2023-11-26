package es.jvbabi.vplanplus.ui.screens.home.search.room

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.RoomUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomSearchViewModel @Inject constructor(
    profileUseCases: ProfileUseCases,
    roomUseCases: RoomUseCases
) : ViewModel() {

    private val _state = mutableStateOf(RoomSearchState())
    val state: State<RoomSearchState> = _state

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                currentSchool = profileUseCases.getSchoolFromProfileId(profileUseCases.getActiveProfile()!!.id),
                rooms = roomUseCases.getRoomAvailabilityMap(profileUseCases.getSchoolFromProfileId(profileUseCases.getActiveProfile()!!.id)).mapKeys { it.key.name },
                loading = false
            )
        }
    }
}

data class RoomSearchState(
    val currentSchool: School? = null,
    val rooms: Map<String, List<Boolean>> = emptyMap(),
    val loading: Boolean = true
)

