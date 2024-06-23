package es.jvbabi.vplanplus.domain.usecase.profile

import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository

class GetLessonTimesForClassUseCase(
    private val lessonTimeRepository: LessonTimeRepository
) {
    suspend operator fun invoke(`class`: Group): Map<Int, LessonTime> {
        return lessonTimeRepository.getLessonTimesByGroup(`class`)
    }
}