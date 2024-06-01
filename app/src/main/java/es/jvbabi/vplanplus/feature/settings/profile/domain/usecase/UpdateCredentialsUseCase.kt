package es.jvbabi.vplanplus.feature.settings.profile.domain.usecase

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository

class UpdateCredentialsUseCase(
    private val schoolRepository: SchoolRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        school: School,
        username: String,
        password: String
    ) {
        schoolRepository.updateCredentials(school, username, password)
        schoolRepository.updateCredentialsValid(school, true)
        notificationRepository.dismissNotification(NotificationRepository.CHANNEL_SYSTEM_NOTIFICATION_ID + 100 + school.schoolId.toInt())
    }
}