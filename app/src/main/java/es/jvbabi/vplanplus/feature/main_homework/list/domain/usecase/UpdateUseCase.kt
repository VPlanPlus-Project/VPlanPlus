package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.UpdateHomeworkUseCase

class UpdateUseCase(
    private val updateHomeworkUseCase: UpdateHomeworkUseCase
) {
    suspend operator fun invoke() {
        updateHomeworkUseCase(false)
    }
}