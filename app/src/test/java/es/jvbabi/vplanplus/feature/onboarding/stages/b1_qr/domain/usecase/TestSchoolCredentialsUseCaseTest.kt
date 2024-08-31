package es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.domain.usecase

import es.jvbabi.vplanplus.data.repository.FakeSchoolRepository
import es.jvbabi.vplanplus.data.repository.InternetRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.ui.QrResultState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class TestSchoolCredentialsUseCaseTest {

    private lateinit var schoolRepositoryWithInternet: FakeSchoolRepository
    private lateinit var schoolRepositoryWithoutInternet: FakeSchoolRepository
    private lateinit var useCaseWithInternet: TestSchoolCredentialsUseCase
    private lateinit var useCaseWithoutInternet: TestSchoolCredentialsUseCase

    @Before
    fun setUp() {
        schoolRepositoryWithInternet = FakeSchoolRepository(InternetRepository(false))
        schoolRepositoryWithoutInternet = FakeSchoolRepository(InternetRepository(true))
        useCaseWithInternet = TestSchoolCredentialsUseCase(schoolRepositoryWithInternet)
        useCaseWithoutInternet = TestSchoolCredentialsUseCase(schoolRepositoryWithoutInternet)
    }

    @Test
    fun `No network connection`() = runBlocking {
        assertEquals(useCaseWithoutInternet(10000000, "user", "pass"), QrResultState.NETWORK_ERROR)
    }

    @Test
    fun `School not found`() = runBlocking {
        assert(useCaseWithInternet(12341234, "user", "pass") == QrResultState.SCHOOL_NOT_FOUND)
    }

    @Test
    fun `School found`() = runBlocking {
        assert(useCaseWithInternet(10000000, "schueler", "pass") == null)
    }

    @Test
    fun `School found with different credentials`() = runBlocking {
        assertEquals(useCaseWithInternet(10000000, "schueler", "wrong"), QrResultState.UNAUTHORIZED)
    }
}