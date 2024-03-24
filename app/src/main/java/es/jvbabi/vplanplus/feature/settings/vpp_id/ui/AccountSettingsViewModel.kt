@file:Suppress("UNCHECKED_CAST")

package es.jvbabi.vplanplus.feature.settings.vpp_id.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.AccountSettingsUseCases
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
                    accountSettingsUseCases.getVppIdServerUseCase()
                )
            ) { data ->
                val accounts = data[0] as List<VppId>
                val server = data[1] as String

                _state.value.copy(
                    accounts = accounts.associateWith { null },
                    server = server
                )
            }.collect {
                _state.value = it

                stateJobs.forEach { job -> job.cancel() }
                _state.value.accounts?.forEach { (vppId, _) ->
                    stateJobs.add(viewModelScope.launch testAccount@{
                        val response = accountSettingsUseCases.testAccountUseCase(vppId)
                        _state.value =
                            _state.value.copy(accounts = _state.value.accounts?.plus(vppId to response))
                    })
                }
            }
        }
    }
}

data class AccountSettingsState(
    val accounts: Map<VppId, Boolean?>? = null,
    val server: String = ""
)