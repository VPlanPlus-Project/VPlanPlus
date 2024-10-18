package es.jvbabi.vplanplus.feature.main_homework.shared.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.di.VppModule
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.data.repository.HomeworkRepositoryImpl
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.ChangeTaskDoneStateUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.UpdateHomeworkUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkModule {

    @Provides
    @Singleton
    fun provideChangeTaskDoneStateUseCase(
        homeworkRepository: HomeworkRepository
    ) = ChangeTaskDoneStateUseCase(
        homeworkRepository = homeworkRepository
    )

    @Provides
    @Singleton
    fun provideUpdateHomeworkUseCase(
        profileRepository: ProfileRepository,
        homeworkRepository: HomeworkRepository,
        fileRepository: FileRepository,
        vppIdRepository: VppIdRepository,
        notificationRepository: NotificationRepository,
        stringRepository: StringRepository
    ) = UpdateHomeworkUseCase(
        profileRepository = profileRepository,
        homeworkRepository = homeworkRepository,
        fileRepository = fileRepository,
        vppIdRepository = vppIdRepository,
        notificationRepository = notificationRepository,
        stringRepository = stringRepository
    )

    @Provides
    @Singleton
    fun provideHomeworkRepository(
        db: VppDatabase,
        vppIdRepository: VppIdRepository,
        logRecordRepository: LogRecordRepository,
        defaultLessonRepository: DefaultLessonRepository,
        keyValueRepository: KeyValueRepository,
        @ApplicationContext context: Context
    ): HomeworkRepository {
        return HomeworkRepositoryImpl(
            homeworkDao = db.homeworkDao,
            homeworkDocumentDao = db.homeworkDocumentDao,
            keyValueDao = db.keyValueDao,
            vppIdRepository = vppIdRepository,
            vppIdNetworkRepository = VppModule.provideVppIdNetworkRepository(keyValueRepository, logRecordRepository),
            defaultLessonRepository = defaultLessonRepository,
            context = context
        )
    }
}