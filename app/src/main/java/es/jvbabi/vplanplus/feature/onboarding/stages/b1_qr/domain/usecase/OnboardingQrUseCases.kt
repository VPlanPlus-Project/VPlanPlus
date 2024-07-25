package es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.domain.usecase

import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.CheckCredentialsAndInitOnboardingForSchoolUseCase

data class OnboardingQrUseCases(
    val testSchoolCredentialsUseCase: TestSchoolCredentialsUseCase,
    val checkCredentialsAndInitOnboardingForSchoolUseCase: CheckCredentialsAndInitOnboardingForSchoolUseCase
)
