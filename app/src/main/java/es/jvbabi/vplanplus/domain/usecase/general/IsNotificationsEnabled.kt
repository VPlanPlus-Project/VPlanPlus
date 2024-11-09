package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.repository.SystemRepository

class IsNotificationsEnabledUseCase(
    private val systemRepository: SystemRepository
){
    operator fun invoke() = systemRepository.canSendNotifications()
}