package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first

class ToggleHomeworkHiddenStateUseCase(
    private val homeworkRepository: HomeworkRepository,
) {

    suspend operator fun invoke(homeworkProfilePersonalizedHomework: PersonalizedHomework.CloudHomework) {
        val actual = (homeworkRepository.getProfileHomeworkById(homeworkProfilePersonalizedHomework.homework.id, homeworkProfilePersonalizedHomework.profile).first() as? PersonalizedHomework.CloudHomework) ?: return
        homeworkRepository.changeHomeworkVisibilityDb(homeworkProfilePersonalizedHomework, !actual.isHidden)
    }
}