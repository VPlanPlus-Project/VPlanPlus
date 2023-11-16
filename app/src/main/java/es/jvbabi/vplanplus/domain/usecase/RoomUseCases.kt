package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class RoomUseCases(
    private val roomRepository: RoomRepository,
    private val lessonUseCases: LessonUseCases
) {
    suspend fun getRoomAvailabilityMap(school: School): Map<Room, List<Boolean>> {
        val rooms = roomRepository.getRoomsBySchool(school)
        val map = mutableMapOf<Room, List<Boolean>>()
        rooms.forEach { room ->
            val lessons = lessonUseCases.getLessonsForRoom(room, LocalDate.now()).first()
            val availability = List(12) { true }.toMutableList()
            lessons.lessons.filter { it.changedSubject != "-" }.forEach { lesson ->
                try {
                    availability[lesson.lessonNumber] = false
                } catch (_: IndexOutOfBoundsException) {}
            }
            map[room] = availability
        }
        return map
    }
}