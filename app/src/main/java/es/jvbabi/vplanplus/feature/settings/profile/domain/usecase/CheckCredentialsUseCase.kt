package es.jvbabi.vplanplus.feature.settings.profile.domain.usecase

import es.jvbabi.vplanplus.domain.repository.BaseDataRepository

class CheckCredentialsUseCase(
    private val baseDataRepository: BaseDataRepository
) {
    suspend operator fun invoke(
        schoolId: Long,
        username: String,
        password: String
    ): Boolean? {
        val response = baseDataRepository.checkCredentials(schoolId, username, password)
        if (response.response == null) return null
        return response.data
    }
}