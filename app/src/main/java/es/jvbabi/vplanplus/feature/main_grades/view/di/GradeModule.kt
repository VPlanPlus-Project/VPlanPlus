package es.jvbabi.vplanplus.feature.main_grades.view.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.main_grades.view.data.repository.GradeRepositoryImpl
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.CanShowEnableBiometricBannerUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.GetGradesUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.GradeUseCases
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.HideBannerUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.HideEnableBiometricBannerUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.IsBiometricEnabledUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.IsBiometricSetUpUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.IsEnabledUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.RequestBiometricUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.SetBiometricUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.ShowBannerUseCase
import es.jvbabi.vplanplus.shared.data.BsNetworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GradeModule {

    @Provides
    fun provideBsNetworkRepository(logRepository: LogRecordRepository): BsNetworkRepository {
        return BsNetworkRepository(logRepository = logRepository)
    }

    @Provides
    @Singleton
    fun provideGradeUseCases(
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        vppIdRepository: VppIdRepository,
        gradeRepository: GradeRepository,
        keyValueRepository: KeyValueRepository,
        biometricRepository: BiometricRepository,
        stringRepository: StringRepository
    ): GradeUseCases {
        return GradeUseCases(
            isEnabledUseCase = IsEnabledUseCase(
                getCurrentProfileUseCase = getCurrentProfileUseCase,
                vppIdRepository = vppIdRepository
            ),
            getGradesUseCase = GetGradesUseCase(
                getCurrentProfileUseCase = getCurrentProfileUseCase,
                gradeRepository = gradeRepository
            ),
            showBannerUseCase = ShowBannerUseCase(keyValueRepository),
            hideBannerUseCase = HideBannerUseCase(keyValueRepository),
            isBiometricEnabled = IsBiometricEnabledUseCase(keyValueRepository),
            canShowEnableBiometricBannerUseCase = CanShowEnableBiometricBannerUseCase(
                keyValueRepository = keyValueRepository,
                biometricRepository = biometricRepository
            ),
            hideEnableBiometricBannerUseCase = HideEnableBiometricBannerUseCase(keyValueRepository),
            setBiometricUseCase = SetBiometricUseCase(keyValueRepository),
            isBiometricSetUpUseCase = IsBiometricSetUpUseCase(biometricRepository),
            requestBiometricUseCase = RequestBiometricUseCase(
                biometricRepository = biometricRepository,
                stringRepository = stringRepository
            )
        )
    }
}