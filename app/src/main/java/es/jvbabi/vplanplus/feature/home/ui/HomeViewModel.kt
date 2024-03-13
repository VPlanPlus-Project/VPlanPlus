package es.jvbabi.vplanplus.feature.home.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.home.domain.usecase.HomeUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCases: HomeUseCases
) : ViewModel() {
    val state = mutableStateOf(HomeState())

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    homeUseCases.getProfilesUseCase(),
                    homeUseCases.getCurrentIdentityUseCase()
                )
            ) { data ->
                val profiles = data[0] as List<Profile>
                val currentIdentity = data[1] as Identity?

                state.value.copy(
                    profiles = profiles,
                    currentIdentity = currentIdentity
                )
            }.collect {
                state.value = it
            }
        }
    }

    fun onMenuOpenedChange(opened: Boolean) {
        state.value = state.value.copy(menuOpened = opened)
    }
}

data class HomeState(
    val profiles: List<Profile> = emptyList(),
    val currentIdentity: Identity? = null,
    val todayDay: Day? = null,
    val tomorrowDay: Day? = null,
    val menuOpened: Boolean = false,
)