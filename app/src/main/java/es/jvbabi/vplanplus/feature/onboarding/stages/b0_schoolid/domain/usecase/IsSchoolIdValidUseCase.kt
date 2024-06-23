package es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.domain.usecase

class IsSchoolIdValidUseCase {
    operator fun invoke(sp24Id: Int): Boolean {
        return sp24Id in 10000000..99999999
    }
}