package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.isAfterOrEqual
import es.jvbabi.vplanplus.util.DateUtils.isBeforeOrEqual
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class GetLessonTimesUseCase(
    private val lessonTimeRepository: LessonTimeRepository,
    private val lessonRepository: LessonRepository,
    private val roomRepository: RoomRepository,
    private val keyValueRepository: KeyValueRepository
) {
    /**
     * @return A map of [LessonTime] to a boolean indicating if the room is available at that time.
     */
    suspend operator fun invoke(profile: ClassProfile, room: Room, date: LocalDate = LocalDate.now()): Map<LessonTime, CurrentRoomState> {

        val bookings = roomRepository
            .getRoomBookings(date = date)
            .filter { it.room == room && it.from.toLocalDate() == date && it.to.toLocalDate() == date }

        val lessons = lessonRepository.getLessonsForRoom(room.roomId, date, keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong()).first() ?: emptyList()

        return lessonTimeRepository.getLessonTimesByGroup(profile.group).values.associateWith { lessonTime ->
            val lessonTimeFrom = lessonTime.start.atBeginningOfTheWorld()
            val lessonTimeTo = lessonTime.end.atBeginningOfTheWorld()
            return@associateWith if (bookings.any { booking ->
                    val bookingFrom = booking.from.atBeginningOfTheWorld()
                    val bookingTo = booking.to.atBeginningOfTheWorld().minusSeconds(1)

                    (bookingFrom.isAfterOrEqual(lessonTimeFrom) && bookingFrom.isBefore(lessonTimeTo)) ||
                            (bookingTo.isAfterOrEqual(lessonTimeFrom) && bookingTo.isBefore(lessonTimeTo)) ||
                            (bookingFrom.isBeforeOrEqual(lessonTimeFrom) && bookingTo.isAfterOrEqual(lessonTimeTo))
                }) CurrentRoomState.BOOKED
            else if (lessons.none { lesson ->
                    val lessonFrom = lesson.start.atBeginningOfTheWorld()
                    val lessonTo = lesson.end.atBeginningOfTheWorld()

                    (lessonFrom.isAfterOrEqual(lessonTimeFrom) && lessonFrom.isBefore(lessonTimeTo)) ||
                            (lessonTo.isAfterOrEqual(lessonTimeFrom) && lessonTo.isBefore(lessonTimeTo)) ||
                            (lessonFrom.isBeforeOrEqual(lessonTimeFrom) && lessonTo.isAfterOrEqual(lessonTimeTo))
                }) CurrentRoomState.AVAILABLE
            else CurrentRoomState.HAS_LESSON
        }
    }
}

enum class CurrentRoomState {
    AVAILABLE, BOOKED, HAS_LESSON
}