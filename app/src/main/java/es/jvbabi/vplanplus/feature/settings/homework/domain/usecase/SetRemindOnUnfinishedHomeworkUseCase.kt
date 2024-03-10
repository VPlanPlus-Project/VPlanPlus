package es.jvbabi.vplanplus.feature.settings.homework.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.usecase.home.SetUpUseCase

class SetRemindOnUnfinishedHomeworkUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val setUpUseCase: SetUpUseCase
) {
    suspend operator fun invoke(remind: Boolean) {
        keyValueRepository.set(Keys.SETTINGS_REMIND_OF_UNFINISHED_HOMEWORK, remind.toString())
        setUpUseCase.createHomeworkReminder()
    }
}