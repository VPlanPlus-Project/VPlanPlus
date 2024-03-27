package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.shared.data.FakeClassRepository
import es.jvbabi.vplanplus.shared.data.FakeKeyValueRepository
import es.jvbabi.vplanplus.shared.data.FakeRoomRepository
import es.jvbabi.vplanplus.shared.data.FakeSchoolRepository
import es.jvbabi.vplanplus.shared.data.FakeTeacherRepository
import es.jvbabi.vplanplus.shared.data.FakeVppIdRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.UUID
import es.jvbabi.vplanplus.ui.preview.School as PreviewSchool

class ProfilePreviewOptionsUseCaseTest {

    private lateinit var profileOptionsUseCase: ProfileOptionsUseCase
    private lateinit var schoolRepository: SchoolRepository
    private lateinit var classRepository: ClassRepository
    private lateinit var teacherRepository: TeacherRepository
    private lateinit var roomRepository: RoomRepository
    private lateinit var keyValueRepository: KeyValueRepository
    private lateinit var vppIdRepository: VppIdRepository

    private lateinit var school: School
    private lateinit var newSchool: School

    @Before
    fun setUp() {
        schoolRepository = FakeSchoolRepository()
        classRepository = FakeClassRepository(schoolRepository as FakeSchoolRepository)
        teacherRepository = FakeTeacherRepository(schoolRepository as FakeSchoolRepository)
        roomRepository = FakeRoomRepository(schoolRepository as FakeSchoolRepository)
        keyValueRepository = FakeKeyValueRepository()
        vppIdRepository = FakeVppIdRepository()

        runBlocking {
            (schoolRepository as FakeSchoolRepository).createExampleData()
            school = schoolRepository.getSchools().first()
            newSchool = PreviewSchool.generateRandomSchools(1).first().copy(schoolId = 1L)
        }

        profileOptionsUseCase = ProfileOptionsUseCase(
            schoolRepository = schoolRepository,
            classRepository = classRepository,
            teacherRepository = teacherRepository,
            roomRepository = roomRepository,
            kv = keyValueRepository,
            vppIdRepository = vppIdRepository
        )

        runBlocking {
            keyValueRepository.set("onboarding.school.${newSchool.schoolId}.classes", FakeClassRepository.classNames.joinToString(","))
            keyValueRepository.set("onboarding.school.${newSchool.schoolId}.teachers", FakeTeacherRepository.teacherNames.joinToString(","))
            keyValueRepository.set("onboarding.school.${newSchool.schoolId}.rooms", FakeRoomRepository.roomNames.joinToString(","))

            FakeClassRepository.classNames.drop(1).forEach {
                classRepository.createClass(school.schoolId, it)
            }

            FakeTeacherRepository.teacherNames.drop(1).forEach {
                teacherRepository.createTeacher(school.schoolId, it)
            }

            FakeRoomRepository.roomNames.drop(1).forEach {
                roomRepository.createRoom(Room(
                    roomId = UUID.randomUUID(),
                    school = school,
                    name = it
                ))
            }
        }
    }

    @Test
    fun `Return classes for new school`() {
        runBlocking {
            val result = profileOptionsUseCase(newSchool.schoolId, ProfileType.STUDENT)
            assert(result == FakeClassRepository.classNames)
        }
    }

    @Test
    fun `Return teachers for new school`() {
        runBlocking {
            val result = profileOptionsUseCase(newSchool.schoolId, ProfileType.TEACHER)
            assert(result == FakeTeacherRepository.teacherNames)
        }
    }

    @Test
    fun `Return rooms for new school`() {
        runBlocking {
            val result = profileOptionsUseCase(newSchool.schoolId, ProfileType.ROOM)
            assert(result == FakeRoomRepository.roomNames)
        }
    }

    @Test
    fun `Return classes for existing school`() {
        runBlocking {
            val result = profileOptionsUseCase(school.schoolId, ProfileType.STUDENT)
            assert(result == FakeClassRepository.classNames.drop(1))
        }
    }

    @Test
    fun `Return teachers for existing school`() {
        runBlocking {
            val result = profileOptionsUseCase(school.schoolId, ProfileType.TEACHER)
            assert(result == FakeTeacherRepository.teacherNames.drop(1))
        }
    }

    @Test
    fun `Return rooms for existing school`() {
        runBlocking {
            val result = profileOptionsUseCase(school.schoolId, ProfileType.ROOM)
            assert(result == FakeRoomRepository.roomNames.drop(1))
        }
    }
}