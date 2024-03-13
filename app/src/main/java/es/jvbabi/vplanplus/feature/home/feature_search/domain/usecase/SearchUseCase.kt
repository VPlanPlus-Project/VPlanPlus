package es.jvbabi.vplanplus.feature.home.feature_search.domain.usecase

import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.feature.home.feature_search.ui.SearchResult
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class SearchUseCase(
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val planRepository: PlanRepository,
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(query: String): List<SearchResult> {
        val version = keyValueRepository.get(Keys.LESSON_VERSION_NUMBER)?.toLongOrNull() ?: 0L
        val classes = classRepository
            .getAll()
            .filter { it.name.lowercase().contains(query.lowercase()) }

        val teachers = teacherRepository
            .getAll()
            .filter { it.acronym.lowercase().contains(query.lowercase()) }

        val rooms = roomRepository
            .getAll()
            .filter { it.name.lowercase().contains(query.lowercase()) }

        val firstClass = classes.firstOrNull()
        val firstTeacher = teachers.firstOrNull()
        val firstRoom = rooms.firstOrNull()

        val firstClassPlan = if (firstClass != null) planRepository.getDayForClass(
            firstClass.classId,
            LocalDate.now(),
            version
        ).first().lessons else null

        val firstTeacherPlan = if (firstTeacher != null) planRepository.getDayForTeacher(
            firstTeacher.teacherId,
            LocalDate.now(),
            version
        ).first().lessons else null

        val firstRoomPlan = if (firstRoom != null) planRepository.getDayForRoom(
            firstRoom.roomId,
            LocalDate.now(),
            version
        ).first().lessons else null

        val firstClassResult = if (firstClass != null) SearchResult(
            firstClass.name,
            firstClass.school.name,
            firstClassPlan
        ) else null

        val firstTeacherResult = if (firstTeacher != null) SearchResult(
            firstTeacher.acronym,
            firstTeacher.school.name,
            firstTeacherPlan
        ) else null

        val firstRoomResult = if (firstRoom != null) SearchResult(
            firstRoom.name,
            firstRoom.school.name,
            firstRoomPlan
        ) else null

        return listOfNotNull(firstClassResult, firstTeacherResult, firstRoomResult) + classes.drop(1).map {
            SearchResult(it.name, it.school.name, null)
        } + teachers.drop(1).map {
            SearchResult(it.acronym, it.school.name, null)
        } + rooms.drop(1).map {
            SearchResult(it.name, it.school.name, null)
        }
    }
}