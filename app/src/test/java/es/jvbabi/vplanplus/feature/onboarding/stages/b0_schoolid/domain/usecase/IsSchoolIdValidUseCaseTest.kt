package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase

import org.junit.Before
import org.junit.Test

class IsSchoolIdValidUseCaseTest {

        private lateinit var useCase: IsSchoolIdValidUseCase

        @Before
        fun setUp() {
            useCase = IsSchoolIdValidUseCase()
        }

        @Test
        fun `Valid school ID`() {
            assert(useCase(10000000))
            assert(useCase(80954890))
            assert(useCase(99999999))
        }

        @Test
        fun `Invalid school ID`() {
            assert(!useCase(9999999))
            assert(!useCase(100000000))
            assert(!useCase(-50))
            assert(!useCase(0))
        }
}