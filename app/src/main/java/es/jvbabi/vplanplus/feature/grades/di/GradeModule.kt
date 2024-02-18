package es.jvbabi.vplanplus.feature.grades.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.grades.data.repository.GradeRepositoryImpl
import es.jvbabi.vplanplus.feature.grades.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseCases
import es.jvbabi.vplanplus.feature.grades.domain.usecase.IsEnabledUseCase
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
        vppIdRepository: VppIdRepository
    ): GradeRepository {
        return GradeRepositoryImpl(
            teacherDao = db.teacherDao,
            subjectDao = db.subjectDao,
            gradeDao = db.gradeDao,
            vppIdRepository = vppIdRepository,
            bsNetworkRepository = provideBsNetworkRepository(logRepository)
        )
    }

    @Provides
    @Singleton
    fun provideGradeUseCases(
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
        vppIdRepository: VppIdRepository
    ): GradeUseCases {
        return GradeUseCases(
            isEnabledUseCase = IsEnabledUseCase(
                getCurrentIdentityUseCase = getCurrentIdentityUseCase,
                vppIdRepository = vppIdRepository
            )
        )
    }
}