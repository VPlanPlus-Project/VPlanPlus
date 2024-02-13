package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import com.google.common.truth.Truth.assertThat
import es.jvbabi.vplanplus.shared.data.FakeSchoolRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class GetSchoolByIdUseCaseTest {

    private lateinit var schoolRepository: FakeSchoolRepository
    private lateinit var getSchoolByIdUseCase: GetSchoolByIdUseCase

    @Before
    fun setUp() {
        schoolRepository = FakeSchoolRepository()
        runBlocking { schoolRepository.createExampleData() }
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