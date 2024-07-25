package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository

class GetClassLessonTimesUseCase(
    private val lessonTimeRepository: LessonTimeRepository,
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(profile: ClassProfile): Map<Int, LessonTime> {
        val `class` = groupRepository.getGroupById(profile.group.groupId) ?: return emptyMap()
        return lessonTimeRepository.getLessonTimesByGroup(`class`)
    }
}