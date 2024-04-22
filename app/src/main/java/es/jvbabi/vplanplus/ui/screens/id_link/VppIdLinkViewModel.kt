package es.jvbabi.vplanplus.ui.screens.id_link

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.usecase.vpp_id.VppIdLinkUseCases
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VppIdLinkViewModel @Inject constructor(
    private val vppIdLinkUseCases: VppIdLinkUseCases,
) : ViewModel() {

    var state by mutableStateOf(VppIdLinkState())
        private set

    private var token: String = ""

    fun init(token: String?) {
        Log.i("vpp.ID Link", "Init with $token")
        if (token == null) {
            state = state.copy(isLoading = false)
            return
        }
        this.token = token
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = false)
            val response = vppIdLinkUseCases.getVppIdDetailsUseCase(token)
            if (response.data == null) {
                Log.d("vpp.ID Link", "Something went wrong: ${response.response}")
                state = state.copy(error = true, isLoading = false)
                return@launch
            }
            if (response.data.classes == null) {
                // class does not exists
                state = state.copy(
                    isLoading = false,
                    classNotFound = true,
                    error = true,
                    vppId = response.data,
                )
                return@launch
            }
            val profiles = vppIdLinkUseCases.getProfilesWhichCanBeUsedForVppIdUseCase(response.data)

            if (profiles.size == 1) vppIdLinkUseCases.setProfileVppIdUseCase(mapOf(profiles.first() to true), response.data)

            state = state.copy(
                isLoading = false,
                vppId = response.data,
                response = response.response,
                selectedProfiles = profiles.associateWith { profiles.size == 1 },
                selectedProfileFoundAtStart = profiles.size == 1
            )
        }
    }

    fun onProceed() {
        viewModelScope.launch {
            if (state.selectedProfiles.isEmpty()) vppIdLinkUseCases.updateMissingLinksStateUseCase()
            else vppIdLinkUseCases.setProfileVppIdUseCase(state.selectedProfiles, state.vppId!!)
        }
    }

    fun onToggleProfileState(profile: Profile) {
        state = state.copy(selectedProfiles = state.selectedProfiles.plus(profile to !(state.selectedProfiles[profile] ?: true)))
    }
}

data class VppIdLinkState(
    val vppId: VppId? = null,
    val response: HttpStatusCode? = null,
    val isLoading: Boolean = true,
    val error: Boolean = false,
    val classNotFound: Boolean = false,
    val selectedProfiles: Map<Profile, Boolean> = emptyMap(),
    val selectedProfileFoundAtStart: Boolean? = null
)