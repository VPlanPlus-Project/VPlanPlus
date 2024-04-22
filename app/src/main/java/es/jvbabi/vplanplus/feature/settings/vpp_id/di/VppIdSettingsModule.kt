package es.jvbabi.vplanplus.feature.settings.vpp_id.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.TestForMissingVppIdToProfileConnectionsUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.AccountSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.CloseSessionUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.DeleteAccountUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.GetAccountsUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.GetProfilesUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.GetProfilesWhichCanBeUsedForVppIdUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.GetSessionsUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.SetProfileVppIdUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.TestAccountUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VppIdSettingsModule {
    @Provides
    @Singleton
    fun provideAccountSettingsUseCases(
        vppIdRepository: VppIdRepository,
        keyValueRepository: KeyValueRepository,
        profileRepository: ProfileRepository,
        classRepository: ClassRepository
    ): AccountSettingsUseCases {
        return AccountSettingsUseCases(
            getAccountsUseCase = GetAccountsUseCase(vppIdRepository = vppIdRepository),
            testAccountUseCase = TestAccountUseCase(vppIdRepository = vppIdRepository),
            deleteAccountUseCase = DeleteAccountUseCase(vppIdRepository = vppIdRepository),
            getSessionsUseCase = GetSessionsUseCase(vppIdRepository = vppIdRepository),
            closeSessionUseCase = CloseSessionUseCase(vppIdRepository = vppIdRepository),
            getVppIdServerUseCase = GetVppIdServerUseCase(keyValueRepository = keyValueRepository),
            getProfilesUseCase = GetProfilesUseCase(profileRepository = profileRepository),
            getProfilesWhichCanBeUsedForVppIdUseCase = GetProfilesWhichCanBeUsedForVppIdUseCase(profileRepository, classRepository),
            setProfileVppIdUseCase = SetProfileVppIdUseCase(profileRepository, keyValueRepository, TestForMissingVppIdToProfileConnectionsUseCase(vppIdRepository, profileRepository))
        )
    }
}