package es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.domain.usecase

import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.ui.QrResultState
import io.ktor.http.HttpStatusCode

class TestSchoolCredentialsUseCase(
    private val schoolRepository: SchoolRepository
) {
    suspend operator fun invoke(sp24SchoolId: Long, username: String, password: String): QrResultState? {
        return when (schoolRepository.login(sp24SchoolId, username, password)) {
            HttpStatusCode.NotFound -> QrResultState.SCHOOL_NOT_FOUND
            HttpStatusCode.Unauthorized -> QrResultState.UNAUTHORIZED
            HttpStatusCode.OK -> null
            else -> QrResultState.NETWORK_ERROR
        }
    }
}