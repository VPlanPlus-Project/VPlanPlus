package es.jvbabi.vplanplus.ui.screens.overlay.vpp_web_auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.vpp_id.WebAuthTask
import es.jvbabi.vplanplus.domain.usecase.vpp_id.web_auth.WebAuthTaskUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VppIdAuthViewModel @Inject constructor(
    val webAuthTaskUseCases: WebAuthTaskUseCases
): ViewModel() {
    var state by mutableStateOf<VppIdAuthState?>(null)
        private set

    fun init(task: WebAuthTask) {
        state = VppIdAuthState(task)
    }

    fun selectEmoji(emoji: String) {
        viewModelScope.launch {
            state = state?.copy(loadingEmoji = emoji, error = false, result = null)
            val result = webAuthTaskUseCases.pickEmojiUseCase(state!!.task, emoji)
            if (result == null) state = state?.copy(error = true)
            else state = state?.copy(result = result)
        }
    }
}

/**
 * @param result null if no result yet, true if successful, false if failed
 */
data class VppIdAuthState(
    val task: WebAuthTask,
    val loadingEmoji: String? = null,
    val result: Boolean? = null,
    val error: Boolean = false
) {
    val isLoading: Boolean
        get() = loadingEmoji != null
}