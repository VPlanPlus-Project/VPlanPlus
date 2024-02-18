package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import com.google.common.truth.Truth.assertThat
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.shared.data.FakeSchoolRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CheckSchoolIdSyntaxTest {

    private lateinit var schoolRepository: SchoolRepository

    private lateinit var checkSchoolIdSyntaxUseCase: CheckSchoolIdSyntax

    @Before
    fun setUp() {
        schoolRepository = FakeSchoolRepository()
        checkSchoolIdSyntaxUseCase = CheckSchoolIdSyntax(schoolRepository)
    }

    @Test
    fun `don't allow alphanumeric school ids`() {
        runBlocking {
            assertThat(checkSchoolIdSyntaxUseCase("345g2")).isFalse()
        }
    }

    @Test
    fun `don't allow too short school ids`() {
        runBlocking {
            assertThat(checkSchoolIdSyntaxUseCase("1")).isFalse()
            assertThat(checkSchoolIdSyntaxUseCase("12")).isFalse()
            assertThat(checkSchoolIdSyntaxUseCase("123")).isFalse()
            assertThat(checkSchoolIdSyntaxUseCase("1234")).isFalse()
            assertThat(checkSchoolIdSyntaxUseCase("12345")).isFalse()
            assertThat(checkSchoolIdSyntaxUseCase("123456")).isFalse()
            assertThat(checkSchoolIdSyntaxUseCase("1234567")).isFalse()
        }
    }

    @Test
    fun `don't allow too long school ids`() {
        runBlocking {
            assertThat(checkSchoolIdSyntaxUseCase("123456789")).isFalse()
        }
    }

    @Test
    fun `allow valid school ids`() {
        runBlocking {
            assertThat(checkSchoolIdSyntaxUseCase("12345678")).isTrue()
        }
    }
}