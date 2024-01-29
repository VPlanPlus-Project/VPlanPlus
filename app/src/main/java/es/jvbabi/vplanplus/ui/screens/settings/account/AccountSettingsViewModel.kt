package es.jvbabi.vplanplus.ui.screens.settings.account

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.usecase.settings.vpp_id.AccountSettingsUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val accountSettingsUseCases: AccountSettingsUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AccountSettingsState())
    val state: State<AccountSettingsState> = _state

    init {
        viewModelScope.launch {
            accountSettingsUseCases.getAccountsUseCase().collect {
                _state.value = AccountSettingsState(it)
            }
        }
    }
}

data class AccountSettingsState(
    val accounts: List<VppId>? = null,
)