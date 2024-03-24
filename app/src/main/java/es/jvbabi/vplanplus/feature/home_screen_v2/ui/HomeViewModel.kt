package es.jvbabi.vplanplus.feature.home_screen_v2.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.HomeUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCases: HomeUseCases
): ViewModel() {
    var state by mutableStateOf(HomeState())

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    homeUseCases.getCurrentIdentityUseCase()
                )
            ) { data ->
                val currentIdentity = data[0] as Identity
                state.copy(
                    currentIdentity = currentIdentity
                )
            }.collect {
                state = it
            }
        }
    }

    fun setSearchState(to: Boolean) { state = state.copy(isSearchExpanded = to) }
}

data class HomeState(
    val currentIdentity: Identity? = null,

    // search
    val isSearchExpanded: Boolean = false
)