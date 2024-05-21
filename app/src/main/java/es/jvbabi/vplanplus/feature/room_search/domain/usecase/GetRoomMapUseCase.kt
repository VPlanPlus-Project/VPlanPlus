package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.util.TimeSpan
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
    ): List<RoomState> {
        if (identity.school == null) return emptyList()
        val rooms = roomRepository.getRooms(identity.school.schoolId).sortedBy { it.name }
        val version = keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong()
        val lessons = lessonRepository.getLessonsForSchoolByDate(identity.school.schoolId.toInt(), date, version).first()
        val bookings = roomRepository.getRoomBookings(date).filter { it.`class`.school == identity.school }

        val result = mutableListOf<RoomState>()
        rooms.forEach { room -> result.add(RoomState(room, emptyList(), emptyList())) }
        lessons.forEach { lesson ->
            lesson.rooms.forEach room@{ roomName ->
                val room = rooms.firstOrNull { it.name == roomName } ?: return@room
                val roomState = result.first { it.room == room }
                val index = result.indexOf(roomState)
                result[index] = roomState.copy(lessons = roomState.lessons + lesson)
            }
        }

        bookings.forEach { booking ->
            val room = rooms.firstOrNull { it == booking.room } ?: return@forEach
            val roomState = result.first { it.room == room }
            val index = result.indexOf(roomState)
            result[index] = roomState.copy(bookings = roomState.bookings + booking)
        }

        return result
    }
}

data class RoomState(
    val room: Room,
    val lessons: List<Lesson>,
    val bookings: List<RoomBooking>,
    val isExpanded: Boolean = true
) {
    fun getOccupiedTimes(): List<TimeSpan> {
        return lessons.map { lesson ->
            TimeSpan(lesson.start, lesson.end)
        }.plus(
            bookings.map { booking ->
                TimeSpan(booking.from, booking.to)
            }
        )
    }
}