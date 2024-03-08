package es.jvbabi.vplanplus.feature.news.domain.usecase

import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository

class UpdateMessagesUseCase(
    private val messageRepository: MessageRepository,
    private val schoolRepository: SchoolRepository
) {
    suspend operator fun invoke() {
        val schoolIds = schoolRepository.getSchools().map { it.schoolId }
        messageRepository.updateMessages(null)
        schoolIds.forEach { messageRepository.updateMessages(it) }
    }
}