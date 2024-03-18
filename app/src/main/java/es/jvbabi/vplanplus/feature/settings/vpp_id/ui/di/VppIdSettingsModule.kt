package es.jvbabi.vplanplus.feature.settings.vpp_id.ui.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.AccountSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.CloseSessionUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.DeleteAccountUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.GetAccountsUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.GetSessionsUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.TestAccountUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VppIdSettingsModule {
    @Provides
    @Singleton
    fun provideAccountSettingsUseCases(
        vppIdRepository: VppIdRepository,
        keyValueRepository: KeyValueRepository
    ): AccountSettingsUseCases {
        return AccountSettingsUseCases(
            getAccountsUseCase = GetAccountsUseCase(vppIdRepository = vppIdRepository),
            testAccountUseCase = TestAccountUseCase(vppIdRepository = vppIdRepository),
            deleteAccountUseCase = DeleteAccountUseCase(vppIdRepository = vppIdRepository),
            getSessionsUseCase = GetSessionsUseCase(vppIdRepository = vppIdRepository),
            closeSessionUseCase = CloseSessionUseCase(vppIdRepository = vppIdRepository),
            getVppIdServerUseCase = GetVppIdServerUseCase(keyValueRepository = keyValueRepository)
        )
    }
}