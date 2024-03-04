package es.jvbabi.vplanplus.feature.grades.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.grades.data.repository.GradeRepositoryImpl
import es.jvbabi.vplanplus.feature.grades.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.grades.domain.usecase.CalculateAverageUseCase
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GetGradesUseCase
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseCases
import es.jvbabi.vplanplus.feature.grades.domain.usecase.HideBannerUseCase
import es.jvbabi.vplanplus.feature.grades.domain.usecase.IsEnabledUseCase
import es.jvbabi.vplanplus.feature.grades.domain.usecase.ShowBannerUseCase
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
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
        stringRepository: StringRepository
    ): GradeRepository {
        return GradeRepositoryImpl(
            teacherDao = db.teacherDao,
            subjectDao = db.subjectDao,
            gradeDao = db.gradeDao,
            vppIdRepository = vppIdRepository,
            bsNetworkRepository = provideBsNetworkRepository(logRepository),
            notificationRepository = notificationRepository,
            stringRepository = stringRepository
        )
    }

    @Provides
    @Singleton
    fun provideGradeUseCases(
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
        vppIdRepository: VppIdRepository,
        gradeRepository: GradeRepository,
        keyValueRepository: KeyValueRepository
    ): GradeUseCases {
        return GradeUseCases(
            isEnabledUseCase = IsEnabledUseCase(
                getCurrentIdentityUseCase = getCurrentIdentityUseCase,
                vppIdRepository = vppIdRepository
            ),
            getGradesUseCase = GetGradesUseCase(
                getCurrentIdentityUseCase = getCurrentIdentityUseCase,
                gradeRepository = gradeRepository,
                calculateAverageUseCase = CalculateAverageUseCase()
            ),
            showBannerUseCase = ShowBannerUseCase(keyValueRepository),
            hideBannerUseCase = HideBannerUseCase(keyValueRepository),
            calculateAverageUseCase = CalculateAverageUseCase()
        )
    }
}