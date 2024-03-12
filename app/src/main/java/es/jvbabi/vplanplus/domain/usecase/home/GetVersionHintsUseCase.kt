package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class GetVersionHintsUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val vppIdRepository: VppIdRepository
) {

    suspend operator fun invoke(): List<VersionHints> {
        val currentVersion = BuildConfig.VERSION_CODE
        val lastVersion = keyValueRepository
            .getOrDefault(Keys.LAST_VERSION_HINTS_VERSION, currentVersion.toString())
            .toInt()

        if (currentVersion <= lastVersion) return emptyList()

        val hints = vppIdRepository.getVersionHints(currentVersion, lastVersion)

        return hints.data
    }
}