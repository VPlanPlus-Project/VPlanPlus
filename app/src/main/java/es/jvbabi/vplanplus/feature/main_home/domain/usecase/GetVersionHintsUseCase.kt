package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.model.VersionHints
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class GetVersionHintsUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val vppIdRepository: VppIdRepository
) {

    suspend operator fun invoke(): VersionHints? {
        val currentVersion = BuildConfig.VERSION_CODE
        val lastVersion = keyValueRepository
            .getOrDefault(Keys.LAST_VERSION_HINTS_VERSION, (currentVersion-1).toString())
            .toInt()

        if (lastVersion >= currentVersion) return null

        val hints = vppIdRepository.getVersionHint(currentVersion)

        return hints.data
    }
}