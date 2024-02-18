package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import com.google.common.truth.Truth.assertThat
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.shared.data.FakeBaseDataRepository
import es.jvbabi.vplanplus.shared.data.FakeClassRepository
import es.jvbabi.vplanplus.shared.data.FakeKeyValueRepository
import es.jvbabi.vplanplus.shared.data.FakeRoomRepository
import es.jvbabi.vplanplus.shared.data.FakeSchoolRepository
import es.jvbabi.vplanplus.shared.data.FakeTeacherRepository
import es.jvbabi.vplanplus.ui.preview.School
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var keyValueRepository: KeyValueRepository
    private lateinit var schoolRepository: SchoolRepository
    private lateinit var baseDataRepository: BaseDataRepository

    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        keyValueRepository = FakeKeyValueRepository()
        schoolRepository = FakeSchoolRepository()
        runBlocking { (schoolRepository as FakeSchoolRepository).createExampleData() }
        baseDataRepository = FakeBaseDataRepository(mapOf(1L to true, 2L to false))

        loginUseCase = LoginUseCase(
            kv = keyValueRepository,
            schoolRepository = schoolRepository,
            baseDataRepository = baseDataRepository
        )
    }

    @Test
    fun `Return if school already exists`() {
        runBlocking {
            val result = loginUseCase(FakeSchoolRepository.exampleSchools.first().schoolId.toString(), "username", "password")
            assert(result == LoginResult.SUCCESS)
        }
    }

    @Test
    fun `Fully supported school, save base data to key-value store`() {
        runBlocking {
            val school = School.generateRandomSchools(1).first().copy(schoolId = 1)
            val result = loginUseCase(school.schoolId.toString(), "username", "password")
            assert(result == LoginResult.SUCCESS)

            val classes = keyValueRepository.get("onboarding.school.${school.schoolId}.classes")!!.split(",")
            assertThat(classes).isEqualTo(FakeClassRepository.classNames)

            val teachers = keyValueRepository.get("onboarding.school.${school.schoolId}.teachers")!!.split(",")
            assertThat(teachers).isEqualTo(FakeTeacherRepository.teacherNames)

            val rooms = keyValueRepository.get("onboarding.school.${school.schoolId}.rooms")!!.split(",")
            assertThat(rooms).isEqualTo(FakeRoomRepository.roomNames)
        }
    }

    @Test
    fun `Not fully supported school, save base data to key-value store without teachers and rooms`() {
        runBlocking {
            val school = School.generateRandomSchools(1).first().copy(schoolId = 2)
            val result = loginUseCase(school.schoolId.toString(), "username", "password")
            assert(result == LoginResult.SUCCESS)

            val classes = keyValueRepository.get("onboarding.school.${school.schoolId}.classes")!!.split(",").filter { it.isNotBlank() }
            assertThat(classes).isEqualTo(FakeClassRepository.classNames)

            val teachers = keyValueRepository.get("onboarding.school.${school.schoolId}.teachers")!!.split(",").filter { it.isNotBlank() }
            assertThat(teachers).isEqualTo(emptyList<String>())

            val rooms = keyValueRepository.get("onboarding.school.${school.schoolId}.rooms")!!.split(",").filter { it.isNotBlank() }
            assertThat(rooms).isEqualTo(emptyList<String>())
        }
    }
}