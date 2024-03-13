package es.jvbabi.vplanplus.feature.home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.TimeRepository

class GetCurrentTimeUseCase(
    private val timeRepository: TimeRepository
) {
    operator fun invoke() = timeRepository.getTime()
}