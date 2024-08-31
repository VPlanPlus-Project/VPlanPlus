package es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase

import es.jvbabi.vplanplus.data.repository.FakeBaseDataRepository
import es.jvbabi.vplanplus.data.repository.FakeProfileRepository
import es.jvbabi.vplanplus.data.repository.FakeSchoolRepository
import es.jvbabi.vplanplus.data.repository.InternetRepository
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CheckCredentialsAndInitOnboardingForSchoolUseCaseTest {

    private lateinit var schoolRepository: SchoolRepository
    private lateinit var fullBaseDataRepository: BaseDataRepository
    private lateinit var reducedBaseDataRepository: BaseDataRepository
    private lateinit var offlineBaseDataRepository: BaseDataRepository
    private lateinit var profileRepository: FakeProfileRepository

    private lateinit var useCase: CheckCredentialsAndInitOnboardingForSchoolUseCase

    @Before
    fun setUp() {
        schoolRepository = FakeSchoolRepository(InternetRepository(true))
        profileRepository = FakeProfileRepository()
        offlineBaseDataRepository = FakeBaseDataRepository(
            hasInternet = false,
            hasRoomsAndTeachers = true
        )
    }

    @Test
    fun `Check with no internet`() {

        runBlocking {
            useCase = CheckCredentialsAndInitOnboardingForSchoolUseCase(
                schoolRepository = schoolRepository,
                baseDataRepository = offlineBaseDataRepository,
                profileRepository = profileRepository
            )
            useCase(10000000, "schueler", "pass")
        }
    }
}