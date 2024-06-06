package es.jvbabi.vplanplus.feature.main_grades.domain.usecase

import com.google.common.truth.Truth.assertThat
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.State
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.shared.data.FakeClassRepository
import es.jvbabi.vplanplus.shared.data.FakeKeyValueRepository
import es.jvbabi.vplanplus.shared.data.FakeProfileRepository
import es.jvbabi.vplanplus.shared.data.FakeRoomRepository
import es.jvbabi.vplanplus.shared.data.FakeSchoolRepository
import es.jvbabi.vplanplus.shared.data.FakeTeacherRepository
import es.jvbabi.vplanplus.shared.data.FakeVppIdRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class IsEnabledUseCaseTest {

    private lateinit var isEnabledUseCase: IsEnabledUseCase
    private lateinit var getCurrentIdentityUseCase: GetCurrentIdentityUseCase

    private lateinit var vppIdRepository: VppIdRepository
    private lateinit var schoolRepository: SchoolRepository
    private lateinit var classRepository: ClassRepository
    private lateinit var teacherRepository: TeacherRepository
    private lateinit var roomRepository: RoomRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var keyValueRepository: KeyValueRepository

    private lateinit var school: School
    private lateinit var `class`: Classes

    @Before
    fun setUp() {
        schoolRepository = FakeSchoolRepository()
        classRepository = FakeClassRepository(schoolRepository as FakeSchoolRepository)
        teacherRepository = FakeTeacherRepository(schoolRepository as FakeSchoolRepository)
        roomRepository = FakeRoomRepository(schoolRepository as FakeSchoolRepository)
        vppIdRepository = FakeVppIdRepository()
        profileRepository = FakeProfileRepository()
        keyValueRepository = FakeKeyValueRepository()
        runBlocking {
            (schoolRepository as FakeSchoolRepository).createExampleData()
            school = schoolRepository.getSchools().random()
            FakeClassRepository.classNames.forEach { `class` ->
                classRepository.createClass(school.schoolId, `class`)
            }
            FakeTeacherRepository.teacherNames.forEach { teacher ->
                teacherRepository.createTeacher(school.schoolId, teacher)
            }
            FakeRoomRepository.roomNames.forEach { room ->
                roomRepository.createRoom(Room(school = school, name = room))
            }
            `class` = classRepository.getClassesBySchool(school).random()

            (profileRepository as FakeProfileRepository).createProfile(
                name = `class`.name,
                customName = `class`.name,
                referenceId = `class`.classId,
                enableHomework = false,
                type = ProfileType.STUDENT
            )
            val teacher = teacherRepository.getTeachersBySchoolId(school.schoolId).random()
            (profileRepository as FakeProfileRepository).createProfile(
                name = teacher.acronym,
                customName = teacher.acronym,
                referenceId = teacher.teacherId,
                enableHomework = false,
                type = ProfileType.TEACHER
            )

            keyValueRepository.set(Keys.ACTIVE_PROFILE, profileRepository.getProfiles().first().last().id.toString())
        }

        getCurrentIdentityUseCase = GetCurrentIdentityUseCase(
            keyValueRepository = keyValueRepository,
            profileRepository = profileRepository
        )
        isEnabledUseCase = IsEnabledUseCase(
            getCurrentIdentityUseCase = getCurrentIdentityUseCase,
            vppIdRepository = vppIdRepository
        )
    }

    @Test
    fun `No vpp ID exists`() {
        runBlocking {
            val result = isEnabledUseCase().first()
            assertThat(result).isEqualTo(GradeUseState.NO_VPP_ID)
        }
    }

    @Test
    fun `Wrong profile active`() {
        runBlocking {
            addVppId()
            val result = isEnabledUseCase().first()
            assertThat(result).isEqualTo(GradeUseState.WRONG_PROFILE_SELECTED)
        }
    }

    @Test
    fun `Not enabled`() {
        runBlocking {
            addVppId()
            keyValueRepository.set(Keys.ACTIVE_PROFILE, profileRepository.getProfiles().first().first().id.toString())
            vppIdRepository.addVppIdToken(vppIdRepository.getVppIds().first().first(), "vppToken", null, true)
            val result = isEnabledUseCase().first()
            assertThat(result).isEqualTo(GradeUseState.NOT_ENABLED)
        }
    }

    @Test
    fun `All good`() {
        runBlocking {
            addVppId()
            keyValueRepository.set(Keys.ACTIVE_PROFILE, profileRepository.getProfiles().first().first().id.toString())
            vppIdRepository.addVppIdToken(vppIdRepository.getVppIds().first().first(), "vppToken", "bsToken", true)
            val result = isEnabledUseCase().first()
            assertThat(result).isEqualTo(GradeUseState.ENABLED)
        }
    }

    private suspend fun addVppId() {
        vppIdRepository.addVppId(VppId(
            id = 42,
            name = "Uwe Test",
            school = school,
            schoolId = school.schoolId,
            classes = `class`,
            className = `class`.name,
            state = State.ACTIVE,
            email = "uwe.test@web.de"
        ))
    }
}