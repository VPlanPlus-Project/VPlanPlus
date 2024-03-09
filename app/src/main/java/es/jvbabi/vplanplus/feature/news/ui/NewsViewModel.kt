package es.jvbabi.vplanplus.feature.news.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Message
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.feature.news.domain.usecase.NewsUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val newsUseCases: NewsUseCases
) : ViewModel() {
    private val _state = mutableStateOf(NewsState())
    val state: State<NewsState> = _state

    init {
        _state.value = NewsState(
            news = emptyList(),
            isLoading = false,
            initialized = false
        )
        viewModelScope.launch {
            messageRepository.getMessages().collect {
                _state.value = _state.value.copy(
                    news = it,
                    isLoading = false,
                    initialized = true
                )
            }
        }
        _state.value = _state.value.copy(
            initialized = true
        )
    }

    fun update() {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            newsUseCases.updateMessages()
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}

data class NewsState(
    val news: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val initialized: Boolean = false
)