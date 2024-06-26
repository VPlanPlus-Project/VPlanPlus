package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository

class SetBalloonUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(balloon: Balloon, value: Boolean) {
        keyValueRepository.set("BALLOON_" + balloon.name, value.toString())
    }
}