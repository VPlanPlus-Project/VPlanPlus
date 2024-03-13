package es.jvbabi.vplanplus.feature.home.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.home.domain.usecase.Date
import es.jvbabi.vplanplus.feature.home.domain.usecase.HomeUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
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
                    homeUseCases.getCurrentIdentityUseCase(),
                    homeUseCases.getDayForCurrentProfileUseCase(Date.TODAY),
                    homeUseCases.getDayForCurrentProfileUseCase(Date.NEXT),
                    homeUseCases.getLastSyncUseCase(),
                    homeUseCases.getCurrentTimeUseCase()
                )
            ) { data ->
                val profiles = data[0] as List<Profile>
                val currentIdentity = data[1] as Identity?
                val todayDay = data[2] as Day?
                val tomorrowDay = data[3] as Day?
                val lastSync = data[4] as ZonedDateTime?
                val time = data[5] as ZonedDateTime

                state.value.copy(
                    profiles = profiles,
                    currentIdentity = currentIdentity,
                    todayDay = todayDay,
                    tomorrowDay = tomorrowDay,
                    lastSync = lastSync,
                    time = time
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
    val lastSync: ZonedDateTime? = null,
    val time: ZonedDateTime = ZonedDateTime.now()
)