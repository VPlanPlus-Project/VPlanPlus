package es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase

import es.jvbabi.vplanplus.domain.repository.MessageRepository
import kotlinx.coroutines.flow.flow

class HasUnreadNewsUseCase(
    private val messageRepository: MessageRepository
) {
    operator fun invoke() = flow {
        messageRepository.getUnreadMessages().collect {
            emit(it.isNotEmpty())
        }
    }
}