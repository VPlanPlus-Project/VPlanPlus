package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase

import es.jvbabi.vplanplus.data.repository.FakeKeyValueRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class SetSchoolIdUseCaseTest {

    lateinit var useCase: SetSchoolIdUseCase
    lateinit var fakeKeyValueRepository: FakeKeyValueRepository

    @Before
    fun setUp() {
        fakeKeyValueRepository = FakeKeyValueRepository()
        useCase = SetSchoolIdUseCase(fakeKeyValueRepository)
    }

    @Test
    fun `Set school ID`() = runBlocking {
        useCase(10000000)
        assertEquals("10000000", fakeKeyValueRepository.get("onboarding.sp24_school_id"))
    }
}