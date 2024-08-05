package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase

import es.jvbabi.vplanplus.data.repository.FakeSchoolRepository
import es.jvbabi.vplanplus.data.repository.InternetRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.ui.SchoolIdError
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DoesSchoolIdExistsUseCaseTest {
    private lateinit var useCaseWithInternet: DoesSchoolIdExistsUseCase
    private lateinit var useCaseWithoutInternet: DoesSchoolIdExistsUseCase
    private var workingInternetConnection = InternetRepository(false)
    private var failingInternetConnection = InternetRepository(true)

    @Before
    fun setUp() {
        useCaseWithInternet = DoesSchoolIdExistsUseCase(FakeSchoolRepository(workingInternetConnection))
        useCaseWithoutInternet = DoesSchoolIdExistsUseCase(FakeSchoolRepository(failingInternetConnection))
    }

    @Test
    fun `No network connection`() = runBlocking {
        assert(useCaseWithoutInternet(10000000) == SchoolIdError.NETWORK_ERROR)
    }

    @Test
    fun `School ID exists`() = runBlocking {
        assert(useCaseWithInternet(10000000) == null)
    }

    @Test
    fun `School ID does not exist`() = runBlocking {
        assert(useCaseWithInternet(12345678) == SchoolIdError.DOES_NOT_EXIST)
    }
}