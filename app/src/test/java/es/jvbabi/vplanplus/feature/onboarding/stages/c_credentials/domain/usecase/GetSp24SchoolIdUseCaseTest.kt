package es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase

import es.jvbabi.vplanplus.data.repository.FakeKeyValueRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class GetSp24SchoolIdUseCaseTest {

    private lateinit var keyValueRepository: KeyValueRepository
    private lateinit var useCase: GetSp24SchoolIdUseCase

    @Before
    fun setUp() = runBlocking {
        keyValueRepository = FakeKeyValueRepository()
        keyValueRepository.set("onboarding.sp24_school_id", "10000000")
        useCase = GetSp24SchoolIdUseCase(keyValueRepository)
    }

    @Test
    fun `Get school ID`() = runBlocking {
        assertEquals(10000000, useCase())
    }
}