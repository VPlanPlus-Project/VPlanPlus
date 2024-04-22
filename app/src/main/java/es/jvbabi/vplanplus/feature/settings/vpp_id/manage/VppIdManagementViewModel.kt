package es.jvbabi.vplanplus.feature.settings.vpp_id.manage

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model.Session
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.AccountSettingsUseCases
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VppIdManagementViewModel @Inject constructor(
    private val accountSettingsUseCases: AccountSettingsUseCases
): ViewModel() {

    val state = mutableStateOf(VppIdManagementState())

    fun init(id: Int) {
        viewModelScope.launch {
            val account = accountSettingsUseCases
                .getAccountsUseCase()
                .first()
                .firstOrNull {
                    it.id == id
                } ?: return@launch
            state.value = state.value.copy(
                vppId = account,
                profiles = accountSettingsUseCases.getProfilesWhichCanBeUsedForVppIdUseCase(account)
            )
            fetchSessions()
        }
    }

    fun onSetLinkedProfiles(profiles: Map<Profile, Boolean>) {
        viewModelScope.launch {
            accountSettingsUseCases.setProfileVppIdUseCase(profiles, state.value.vppId ?: return@launch)
            state.value = state.value.copy(profiles = accountSettingsUseCases.getProfilesWhichCanBeUsedForVppIdUseCase(state.value.vppId ?: return@launch))
        }
    }

    fun fetchSessionsFromUi() {
        viewModelScope.launch {
            fetchSessions()
        }
    }

    private suspend fun fetchSessions() {
        state.value = state.value.copy(
            sessionsState = SessionState.LOADING
        )
        val sessions = accountSettingsUseCases.getSessionsUseCase(state.value.vppId!!)
        if (sessions.response != HttpStatusCode.OK || sessions.data == null) {
            state.value = state.value.copy(
                sessionsState = SessionState.ERROR
            )
        } else {
            state.value = state.value.copy(
                sessions = sessions.data,
                sessionsState = SessionState.SUCCESS
            )
        }
    }

    fun openLogoutDialog() {
        state.value = state.value.copy(
            logoutDialog = true
        )
    }

    fun closeLogoutDialog() {
        state.value = state.value.copy(
            logoutDialog = false
        )
    }

    fun logout() {
        viewModelScope.launch {
            val result = accountSettingsUseCases.deleteAccountUseCase(state.value.vppId!!)
            state.value = state.value.copy(
                logoutSuccess = result
            )
            closeLogoutDialog()
        }
    }

    fun closeSession(session: Session) {
        if (session.isCurrent) {
            logout()
            return
        }

        viewModelScope.launch {
            state.value = state.value.copy(sessions = state.value.sessions.filter { it != session })
            if (!accountSettingsUseCases.closeSessionUseCase(session, state.value.vppId!!)) fetchSessions()
        }
    }
}

data class VppIdManagementState(
    val vppId: VppId? = null,
    val logoutDialog: Boolean = false,
    val logoutSuccess: Boolean? = null,
    val profiles: List<Profile> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val sessionsState: SessionState = SessionState.LOADING
)

enum class SessionState {
    LOADING,
    ERROR,
    SUCCESS
}