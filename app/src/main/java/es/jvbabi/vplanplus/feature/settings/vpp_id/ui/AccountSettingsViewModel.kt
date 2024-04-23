@file:Suppress("UNCHECKED_CAST")

package es.jvbabi.vplanplus.feature.settings.vpp_id.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.AccountSettingsUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val accountSettingsUseCases: AccountSettingsUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AccountSettingsState())
    val state: State<AccountSettingsState> = _state

    private val stateJobs: MutableList<Job> = mutableListOf()

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    accountSettingsUseCases.getAccountsUseCase(),
                    accountSettingsUseCases.getVppIdServerUseCase(),
                    accountSettingsUseCases.getProfilesUseCase()
                )
            ) { data ->
                val accounts = data[0] as List<VppId>
                val server = data[1] as String
                val profiles = data[2] as List<Profile>

                _state.value.copy(
                    accounts = accounts.map { vppId -> VppIdSettingsRecord(vppId = vppId, linkedProfiles = profiles.filter { it.vppId == vppId }) },
                    server = server
                )
            }.collect {
                _state.value = it

                stateJobs.forEach { job -> job.cancel() }
                _state.value.accounts.forEach { (vppId, _) ->
                    stateJobs.add(viewModelScope.launch testAccount@{
                        val response = accountSettingsUseCases.testAccountUseCase(vppId)
                        val accounts = _state.value.accounts.toMutableList()
                        accounts.replaceAll { account -> if (account.vppId == vppId) account.copy(hasActiveSession = response) else account }
                        _state.value = _state.value.copy(accounts = accounts)
                    })
                }
            }
        }
    }
}

data class AccountSettingsState(
    val accounts: List<VppIdSettingsRecord> = emptyList(),
    val server: String = ""
)

data class VppIdSettingsRecord(
    val vppId: VppId,
    val hasActiveSession: Boolean? = null,
    val linkedProfiles: List<Profile>
)