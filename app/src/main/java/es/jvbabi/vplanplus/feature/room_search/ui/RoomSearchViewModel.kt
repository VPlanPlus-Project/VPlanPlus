package es.jvbabi.vplanplus.feature.room_search.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.RoomSearchUseCases
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomSearchViewModel @Inject constructor(
    private val roomSearchUseCases: RoomSearchUseCases
) : ViewModel() {

    val state = mutableStateOf(RoomSearchState())

    init {
        viewModelScope.launch {
            val identity =  roomSearchUseCases.getCurrentIdentityUseCase().first() ?: return@launch
            state.value = state.value.copy(currentIdentity = identity)

            val map = roomSearchUseCases.getRoomMapUseCase(identity)
            state.value = state.value.copy(map = map)
        }
    }
}

data class RoomSearchState(
    val currentIdentity: Identity? = null,
    val map: Map<Room, List<Lesson>> = emptyMap()
)