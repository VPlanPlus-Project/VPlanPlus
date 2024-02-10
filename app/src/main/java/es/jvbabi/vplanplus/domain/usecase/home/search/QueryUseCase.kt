package es.jvbabi.vplanplus.domain.usecase.home.search

import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.UUID

class QueryUseCase(
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val schoolRepository: SchoolRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val lessonRepository: LessonRepository,
    private val keyValueRepository: KeyValueRepository
) {

    suspend operator fun invoke(
        rawQuery: String,
        selectedClassIds: List<UUID>,
        selectedTeacherIds: List<UUID>,
        selectedRoomIds: List<UUID>
    ): List<ResultGroup> {
        val query = rawQuery.lowercase()
        val currentSchool = getCurrentIdentityUseCase().first()?.school?: return emptyList()
        val date = LocalDate.now()
        val version = keyValueRepository.get(Keys.LESSON_VERSION_NUMBER)?.toLongOrNull()?: 0L
        val results = mutableListOf<ResultGroup>()
        schoolRepository.getSchools().forEach {  school ->
            val classes = classRepository.getClassesBySchool(school).filter { it.name.lowercase().contains(query) }
            val rooms = roomRepository.getRoomsBySchool(school).filter { it.name.lowercase().contains(query) }
            val teacherRepository = teacherRepository.getTeachersBySchoolId(school.schoolId).filter { it.acronym.lowercase().contains(query) }

            val searchResults = mutableListOf<SearchResult>()
            classes.forEachIndexed { index, it ->
                searchResults.add(
                    SearchResult(
                        id = it.classId,
                        name = it.name,
                        type = SchoolEntityType.CLASS,
                        lessons = lessonRepository.getLessonsForClass(it.classId, date, version).first()?: emptyList(),
                        detailed = selectedClassIds.contains(it.classId) || (selectedClassIds.isEmpty() && index == 0)
                    )
                )
            }
            teacherRepository.forEachIndexed { index, it ->
                searchResults.add(
                    SearchResult(
                        id = it.teacherId,
                        name = it.acronym,
                        type = SchoolEntityType.TEACHER,
                        lessons = lessonRepository.getLessonsForTeacher(it.teacherId, date, version).first()?: emptyList(),
                        detailed = selectedTeacherIds.contains(it.teacherId) || (selectedTeacherIds.isEmpty() && index == 0)
                    )
                )
            }
            rooms.forEachIndexed { index, it ->
                searchResults.add(
                    SearchResult(
                        id = it.roomId,
                        name = it.name,
                        type = SchoolEntityType.ROOM,
                        lessons = lessonRepository.getLessonsForRoom(it.roomId, date, version).first()?: emptyList(),
                        detailed = selectedRoomIds.contains(it.roomId) || (selectedRoomIds.isEmpty() && index == 0)
                    )
                )
            }

            results.add(ResultGroup(
                school = school,
                searchResults = searchResults,
                selectedClassId = selectedClassIds.firstOrNull { id -> searchResults.any { it.id == id } },
                selectedTeacherId = selectedTeacherIds.firstOrNull { id -> searchResults.any { it.id == id } },
                selectedRoomId = selectedRoomIds.firstOrNull { id -> searchResults.any { it.id == id } }
            ))
        }

        return results.sortedBy { currentSchool == it.school }
    }
}

data class ResultGroup(
    val school: School,
    val searchResults: List<SearchResult>,
    var selectedClassId: UUID? = null,
    var selectedTeacherId: UUID? = null,
    var selectedRoomId: UUID? = null
)

data class SearchResult(
    val id: UUID,
    val name: String,
    val type: SchoolEntityType,
    val lessons: List<Lesson> = emptyList(),
    val detailed: Boolean = false
)