package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.util.TimeSpan
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class GetRoomMapUseCase(
    private val roomRepository: RoomRepository,
    private val lessonRepository: LessonRepository,
    private val keyValueRepository: KeyValueRepository
) {

    suspend operator fun invoke(
        profile: Profile,
        date: LocalDate = LocalDate.now()
    ): List<RoomState> {
        val rooms = roomRepository.getRooms(profile.getSchool().id).sortedBy { it.name }
        val version = keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong()
        val lessons = lessonRepository.getLessonsForSchoolByDate(profile.getSchool(), date, version).first()
        val bookings = roomRepository.getRoomBookings(date).filter { it.bookedBy?.group?.school == profile.getSchool() }

        val result = mutableListOf<RoomState>()
        rooms.forEach { room -> result.add(RoomState(room, emptyList(), emptyList())) }
        lessons.forEach { lesson ->
            lesson.rooms.forEach room@{ room ->
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
    val lessons: List<Lesson> = emptyList(),
    val bookings: List<RoomBooking> = emptyList(),
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