package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class SetRemindOnUnfinishedHomeworkUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(remind: Boolean) {
        keyValueRepository.set(Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK, remind.toString())
    }
}