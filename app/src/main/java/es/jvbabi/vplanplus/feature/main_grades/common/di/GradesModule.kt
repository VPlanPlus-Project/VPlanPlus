package es.jvbabi.vplanplus.feature.main_grades.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.main_grades.common.domain.usecases.UpdateGradesUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.data.repository.GradeRepositoryImpl
import es.jvbabi.vplanplus.feature.main_grades.view.di.GradeModule.provideBsNetworkRepository
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GradesModule {

    @Provides
    @Singleton
    fun provideGradeRepository(
        db: VppDatabase,
        logRepository: LogRecordRepository
    ): GradeRepository {
        return GradeRepositoryImpl(
            teacherDao = db.teacherDao,
            subjectDao = db.subjectDao,
            gradeDao = db.gradeDao,
            yearDao = db.yearDao,
            bsNetworkRepository = provideBsNetworkRepository(logRepository),
        )
    }

    @Provides
    @Singleton
    fun provideUpdateGradesUseCase(
        gradesRepository: GradeRepository,
        profileRepository: ProfileRepository,
        vppIdRepository: VppIdRepository,
        notificationRepository: NotificationRepository,
        stringRepository: StringRepository,
        keyValueRepository: KeyValueRepository
    ) = UpdateGradesUseCase(
        profileRepository = profileRepository,
        vppIdRepository = vppIdRepository,
        gradeRepository = gradesRepository,
        notificationRepository = notificationRepository,
        stringRepository = stringRepository,
        getVppIdServerUseCase = GetVppIdServerUseCase(keyValueRepository)
    )
}