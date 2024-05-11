package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class GetRoomMapUseCase(
    private val roomRepository: RoomRepository,
    private val lessonRepository: LessonRepository,
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(
        identity: Identity,
        date: LocalDate = LocalDate.now()
    ): Map<Room, List<Lesson>> {
        if (identity.school == null) return emptyMap()
        val rooms = roomRepository.getRooms(identity.school.schoolId).sortedBy { it.name }
        val version = keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong()
        val lessons = lessonRepository.getLessonsForSchoolByDate(identity.school.schoolId.toInt(), date, version).first()

        val result = mutableMapOf<Room, List<Lesson>>()
        rooms.forEach { room -> result[room] = emptyList() }
        lessons.forEach { lesson ->
            lesson.rooms.forEach room@{ roomName ->
                val room = rooms.firstOrNull { it.name == roomName } ?: return@room
                result[room] = (result[room] ?: emptyList()) + lesson
            }
        }

        return result
    }
}