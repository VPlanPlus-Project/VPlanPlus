package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import com.google.common.truth.Truth.assertThat
import es.jvbabi.vplanplus.domain.repository.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.shared.data.FakeSchoolRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class TestSchoolExistenceTest {

    private lateinit var schoolRepository: SchoolRepository
    private lateinit var testSchoolExistence: TestSchoolExistence

    @Before
    fun setUp() {
        schoolRepository = FakeSchoolRepository()
        testSchoolExistence = TestSchoolExistence(schoolRepository)
    }

    @Test
    fun `Test for non-existing school`() {
        runBlocking {
            assertThat(schoolRepository.checkSchoolId(1L)).isEqualTo(SchoolIdCheckResult.NOT_FOUND)
            assertThat(schoolRepository.checkSchoolId(15000001L)).isEqualTo(SchoolIdCheckResult.NOT_FOUND)
        }
    }

    @Test
    fun `Test for existing school`() {
        runBlocking {
            assertThat(schoolRepository.checkSchoolId(10000000L)).isEqualTo(SchoolIdCheckResult.VALID)
            assertThat(schoolRepository.checkSchoolId(15000000L)).isEqualTo(SchoolIdCheckResult.VALID)
        }
    }
}