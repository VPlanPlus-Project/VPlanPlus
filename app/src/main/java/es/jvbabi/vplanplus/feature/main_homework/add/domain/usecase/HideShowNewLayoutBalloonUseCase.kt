package es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class HideShowNewLayoutBalloonUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.ADD_HOMEWORK_SHOW_NEW_LAYOUT_BALLOON, "false")
    }
}