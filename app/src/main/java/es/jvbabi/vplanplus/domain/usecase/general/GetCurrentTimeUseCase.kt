package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.repository.TimeRepository

class GetCurrentTimeUseCase(
    private val timeRepository: TimeRepository
) {
    operator fun invoke() = timeRepository.getTime()
}