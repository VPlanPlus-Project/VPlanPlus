package es.jvbabi.vplanplus.feature.settings.vpp_id.ui.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.BsLoginUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VppIdSettingsModule {
    @Provides
    @Singleton
    fun provideBsLoginUseCases(
        keyValueRepository: KeyValueRepository
    ): BsLoginUseCases {
        return BsLoginUseCases(
            getVppIdServerUseCase = GetVppIdServerUseCase(keyValueRepository)
        )
    }
}