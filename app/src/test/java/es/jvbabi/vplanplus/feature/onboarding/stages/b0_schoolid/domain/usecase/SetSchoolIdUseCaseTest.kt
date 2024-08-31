package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase

import es.jvbabi.vplanplus.data.repository.FakeKeyValueRepository
import es.jvbabi.vplanplus.data.repository.FakeSchoolRepository
import es.jvbabi.vplanplus.data.repository.InternetRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SetSchoolIdUseCaseTest {

    private lateinit var useCase: SetSchoolIdUseCase
    private lateinit var fakeKeyValueRepository: FakeKeyValueRepository
    private lateinit var fakeSchoolRepository: FakeSchoolRepository

    @Before
    fun setUp() {
        fakeKeyValueRepository = FakeKeyValueRepository()
        fakeSchoolRepository = FakeSchoolRepository(InternetRepository(false))
        useCase = SetSchoolIdUseCase(fakeKeyValueRepository, fakeSchoolRepository)
    }

    @Test
    fun `Set school ID`() = runBlocking {
        useCase(10000000)
        assertEquals("10000000", fakeKeyValueRepository.get("onboarding.sp24_school_id"))
    }
}