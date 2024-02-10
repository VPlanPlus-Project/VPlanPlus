package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Deprecated("This class is not used anymore")
class LessonUseCases(
    private val planRepository: PlanRepository,
) {

    fun getLessonsForRoom(room: Room, date: LocalDate, version: Long): Flow<Day> {
        return planRepository.getDayForRoom(room.roomId, date, version)
    }
}