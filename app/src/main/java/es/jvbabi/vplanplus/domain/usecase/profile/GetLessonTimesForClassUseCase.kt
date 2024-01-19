package es.jvbabi.vplanplus.domain.usecase.profile

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository

class GetLessonTimesForClassUseCase(
    private val lessonTimeRepository: LessonTimeRepository
) {
    suspend operator fun invoke(`class`: Classes): Map<Int, LessonTime> {
        return lessonTimeRepository.getLessonTimesByClass(`class`)
    }
}