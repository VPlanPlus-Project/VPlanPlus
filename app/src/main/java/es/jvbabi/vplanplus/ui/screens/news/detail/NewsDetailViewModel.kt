package es.jvbabi.vplanplus.ui.screens.news.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Message
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    private val _state = mutableStateOf(NewsDetailState())
    val state: State<NewsDetailState> = _state

    fun init(messageId: String) {
        viewModelScope.launch {
            messageRepository.markMessageAsRead(messageId)
            messageRepository.getMessage(messageId).collect { message ->
                _state.value = _state.value.copy(
                    message = message,
                )
            }
        }
    }
}

data class NewsDetailState(
    val message: Message? = null,
)