package es.jvbabi.vplanplus.feature.main_grades.di

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
import es.jvbabi.vplanplus.feature.main_grades.data.repository.GradeRepositoryImpl
import es.jvbabi.vplanplus.feature.main_grades.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.CanShowEnableBiometricBannerUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.GetGradesUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.GradeUseCases
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.HideBannerUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.HideEnableBiometricBannerUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.IsBiometricEnabledUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.IsBiometricSetUpUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.IsEnabledUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.RequestBiometricUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.SetBiometricUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.ShowBannerUseCase
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
    fun provideGradeRepository(
        db: VppDatabase,
        logRepository: LogRecordRepository,
        vppIdRepository: VppIdRepository,
        notificationRepository: NotificationRepository,
        stringRepository: StringRepository,
        logRecordRepository: LogRecordRepository
    ): GradeRepository {
        return GradeRepositoryImpl(
            teacherDao = db.teacherDao,
            subjectDao = db.subjectDao,
            gradeDao = db.gradeDao,
            yearDao = db.yearDao,
            vppIdRepository = vppIdRepository,
            bsNetworkRepository = provideBsNetworkRepository(logRepository),
            notificationRepository = notificationRepository,
            stringRepository = stringRepository,
            logRecordRepository = logRecordRepository
        )
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