package es.jvbabi.vplanplus.feature.homework.shared.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.di.VppModule
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.homework.shared.data.repository.HomeworkRepositoryImpl
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkModule {

    @Provides
    @Singleton
    fun provideHomeworkRepository(
        db: VppDatabase,
        vppIdRepository: VppIdRepository,
        profileRepository: ProfileRepository,
        classRepository: ClassRepository,
        defaultLessonRepository: DefaultLessonRepository,
        logRecordRepository: LogRecordRepository
    ): HomeworkRepository {
        return HomeworkRepositoryImpl(
            homeworkDao = db.homeworkDao,
            vppIdRepository = vppIdRepository,
            profileRepository = profileRepository,
            classRepository = classRepository,
            defaultLessonRepository = defaultLessonRepository,
            vppIdNetworkRepository = VppModule.provideVppIdNetworkRepository(logRecordRepository)
        )
    }
}