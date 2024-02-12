package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import com.google.common.truth.Truth.assertThat
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.shared.data.FakeSchoolRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class GetSchoolByIdUseCaseTest {

    private lateinit var schoolRepository: SchoolRepository
    private lateinit var getSchoolByIdUseCase: GetSchoolByIdUseCase

    @Before
    fun setUp() {
        schoolRepository = FakeSchoolRepository()
        listOf(
            School(10000000, "Testschool", "example", "example", 5, true),
            School(10000001, "Albert-Einstein-Gymnasium", "schueler", "ein.stein", 5, true),
            School(10000002, "Gymnasium am Steinwald", "schueler", "steinwald", 5, false),
        ).forEach {
            runBlocking {
                schoolRepository.createSchool(
                    schoolId = it.schoolId,
                    username = it.username,
                    password = it.password,
                    name = it.name,
                    daysPerWeek = it.daysPerWeek,
                    fullyCompatible = it.fullyCompatible
                )
            }
        }
    }

    @Test
    fun `get school by id`() {
        getSchoolByIdUseCase = GetSchoolByIdUseCase(schoolRepository)
        runBlocking {
            val school = getSchoolByIdUseCase(10000000)
            assertThat(school).isNotNull()
            assertThat(school!!.schoolId).isEqualTo(10000000L)
            assertThat(school.name).isEqualTo("Testschool")
            assertThat(school.username).isEqualTo("example")
            assertThat(school.password).isEqualTo("example")
            assertThat(school.daysPerWeek).isEqualTo(5)
            assertThat(school.fullyCompatible).isTrue()
        }
    }

    @Test
    fun `get school by id not found`() {
        getSchoolByIdUseCase = GetSchoolByIdUseCase(schoolRepository)
        runBlocking {
            val school = getSchoolByIdUseCase(10000003)
            assertThat(school).isNull()
        }
    }
}