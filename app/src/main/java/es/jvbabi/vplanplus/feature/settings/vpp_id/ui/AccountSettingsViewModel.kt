package es.jvbabi.vplanplus.feature.settings.vpp_id.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.AccountSettingsUseCases
import kotlinx.coroutines.Job
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
            accountSettingsUseCases.getAccountsUseCase().collect {
                _state.value = _state.value.copy(accounts = it.associateWith { null })
                stateJobs.forEach { job -> job.cancel() }
                _state.value.accounts?.forEach { (vppId, _) ->
                    stateJobs.add(viewModelScope.launch testAccount@{
                        val response = accountSettingsUseCases.testAccountUseCase(vppId)
                        _state.value =
                            _state.value.copy(accounts = _state.value.accounts?.plus(vppId to response.data))
                    })
                }
            }
        }
    }
}

data class AccountSettingsState(
    val accounts: Map<VppId, Boolean?>? = null,
)