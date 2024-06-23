package es.jvbabi.vplanplus.domain.usecase.home.search

import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.UUID

class QueryUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val schoolRepository: SchoolRepository,
    private val groupRepository: GroupRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val lessonRepository: LessonRepository,
    private val keyValueRepository: KeyValueRepository
) {

    suspend operator fun invoke(
        rawQuery: String,
        selectedClassIds: List<Int>,
        selectedTeacherIds: List<UUID>,
        selectedRoomIds: List<UUID>
    ): List<ResultGroup> {
        val query = rawQuery.lowercase()
        val currentSchool = getCurrentProfileUseCase().first()?.getSchool()?: return emptyList()
        val date = LocalDate.now()
        val version = keyValueRepository.get(Keys.LESSON_VERSION_NUMBER)?.toLongOrNull()?: -1L
        val results = mutableListOf<ResultGroup>()
        schoolRepository.getSchools().forEach {  school ->
            val classes = groupRepository.getGroupsBySchool(school).filter { it.name.lowercase().contains(query) }
            val rooms = roomRepository.getRoomsBySchool(school).filter { it.name.lowercase().contains(query) }
            val teacherRepository = teacherRepository.getTeachersBySchoolId(school.id).filter { it.acronym.lowercase().contains(query) }

            val searchResults = mutableListOf<SearchResult>()
            classes.forEach {
                searchResults.add(
                    GroupSearchResult(
                        id = it.groupId,
                        name = it.name,
                        lessons = lessonRepository.getLessonsForGroup(it.groupId, date, version).first()?: emptyList(),
                    )
                )
            }
            teacherRepository.forEach {
                searchResults.add(
                    TeacherSearchResult(
                        id = it.teacherId,
                        name = it.acronym,
                        lessons = lessonRepository.getLessonsForTeacher(it.teacherId, date, version).first()?: emptyList(),
                    )
                )
            }
            rooms.forEach {
                searchResults.add(
                    RoomSearchResult(
                        id = it.roomId,
                        name = it.name,
                        lessons = lessonRepository.getLessonsForRoom(it.roomId, date, version).first()?: emptyList(),
                    )
                )
            }

            results.add(ResultGroup(
                school = school,
                searchResults = searchResults,
                selectedClassId = selectedClassIds.firstOrNull { id -> searchResults.filterIsInstance<GroupSearchResult>().any { it.id == id } },
                selectedTeacherId = selectedTeacherIds.firstOrNull { id -> searchResults.filterIsInstance<TeacherSearchResult>().any { it.id == id } },
                selectedRoomId = selectedRoomIds.firstOrNull { id -> searchResults.filterIsInstance<RoomSearchResult>().any { it.id == id } }
            ))
        }

        return results.sortedBy { currentSchool == it.school }
    }
}

data class ResultGroup(
    val school: School,
    val searchResults: List<SearchResult>,
    var selectedClassId: Int? = null,
    var selectedTeacherId: UUID? = null,
    var selectedRoomId: UUID? = null
)

sealed class SearchResult(
    val name: String,
    val lessons: List<Lesson> = emptyList(),
)

class GroupSearchResult(
    val id: Int,
    name: String,
    lessons: List<Lesson>,
) : SearchResult(name, lessons)

class TeacherSearchResult(
    val id: UUID,
    name: String,
    lessons: List<Lesson>,
) : SearchResult(name, lessons)

class RoomSearchResult(
    val id: UUID,
    name: String,
    lessons: List<Lesson>,
) : SearchResult(name, lessons)