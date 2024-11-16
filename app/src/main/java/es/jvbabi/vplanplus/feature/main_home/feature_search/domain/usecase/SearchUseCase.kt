package es.jvbabi.vplanplus.feature.main_home.feature_search.domain.usecase

import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.SearchResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

class SearchUseCase(
    private val groupRepository: GroupRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val planRepository: PlanRepository,
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(query: String, date: LocalDate): List<SearchResult> {
        val version = keyValueRepository.get(Keys.LESSON_VERSION_NUMBER)?.toLongOrNull() ?: 0L
        val classes = groupRepository
            .getAll()
            .filter { it.name.lowercase().contains(query.lowercase()) }
            .sortedBy { it.name }

        val teachers = teacherRepository
            .getAll()
            .filter { it.acronym.lowercase().contains(query.lowercase()) }
            .sortedBy { it.acronym }

        val rooms = roomRepository
            .getAll()
            .filter { it.name.lowercase().contains(query.lowercase()) }
            .sortedBy { it.name }

        val firstClass = classes.firstOrNull()
        val firstTeacher = teachers.firstOrNull()
        val firstRoom = rooms.firstOrNull()

        val firstClassPlan = if (firstClass != null) planRepository.getDayForGroup(
            firstClass.groupId,
            date,
            version
        ).firstOrNull()?.lessons else null

        val firstTeacherPlan = if (firstTeacher != null) planRepository.getDayForTeacher(
            firstTeacher.teacherId,
            date,
            version
        ).firstOrNull()?.lessons else null

        val firstRoomPlan = if (firstRoom != null) planRepository.getDayForRoom(
            firstRoom.roomId,
            date,
            version
        ).firstOrNull()?.lessons else null

        val firstClassResult = if (firstClass != null) SearchResult(
            firstClass.name,
            ProfileType.STUDENT,
            firstClass.school.name,
            firstClassPlan,
            roomRepository.getRoomBookings(date).filter { it.bookedBy?.group?.groupId == firstClass.groupId && it.from.toLocalDate().isEqual(date) }
        ) else null

        val firstTeacherResult = if (firstTeacher != null) SearchResult(
            firstTeacher.acronym,
            ProfileType.TEACHER,
            firstTeacher.school.name,
            firstTeacherPlan,
            emptyList()
        ) else null

        val firstRoomResult = if (firstRoom != null) SearchResult(
            firstRoom.name,
            ProfileType.ROOM,
            firstRoom.school.name,
            firstRoomPlan,
            roomRepository.getRoomBookings(date).filter { it.room == firstRoom && it.from.toLocalDate().isEqual(date) }
        ) else null

        return listOfNotNull(firstClassResult, firstTeacherResult, firstRoomResult) + classes.drop(1).map {
            SearchResult(it.name, ProfileType.STUDENT, it.school.name, null, emptyList())
        } + teachers.drop(1).map {
            SearchResult(it.acronym, ProfileType.TEACHER, it.school.name, null, emptyList())
        } + rooms.drop(1).map {
            SearchResult(it.name, ProfileType.ROOM, it.school.name, null, emptyList())
        }
    }
}