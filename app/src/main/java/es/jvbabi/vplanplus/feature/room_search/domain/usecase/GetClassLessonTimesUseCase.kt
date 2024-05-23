package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository

class GetClassLessonTimesUseCase(
    private val lessonTimeRepository: LessonTimeRepository,
    private val classRepository: ClassRepository
) {
    suspend operator fun invoke(profile: Profile): Map<Int, LessonTime> {
        if (profile.type != ProfileType.STUDENT) return emptyMap()
        val `class` = classRepository.getClassById(profile.referenceId) ?: return emptyMap()
        return lessonTimeRepository.getLessonTimesByClass(`class`)
    }
}