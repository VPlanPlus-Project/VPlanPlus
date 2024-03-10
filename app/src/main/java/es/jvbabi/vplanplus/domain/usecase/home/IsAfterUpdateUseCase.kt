package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class IsAfterUpdateUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(): Boolean {
        return keyValueRepository.getOrDefault(Keys.LAST_APP_VERSION, (BuildConfig.VERSION_CODE-1).toString()) < BuildConfig.VERSION_CODE.toString()
    }
}