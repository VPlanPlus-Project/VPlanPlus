package es.jvbabi.vplanplus.feature.homework.shared.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.di.VppModule
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.homework.shared.data.repository.HomeworkRepositoryImpl
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.homework.shared.domain.usecase.HomeworkReminderUseCase
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
        logRecordRepository: LogRecordRepository,
        notificationRepository: NotificationRepository,
        stringRepository: StringRepository,
        defaultLessonRepository: DefaultLessonRepository,
        keyValueRepository: KeyValueRepository
    ): HomeworkRepository {
        return HomeworkRepositoryImpl(
            homeworkDao = db.homeworkDao,
            homeworkNotificationTimeDao = db.homeworkNotificationTimeDao,
            vppIdRepository = vppIdRepository,
            profileRepository = profileRepository,
            classRepository = classRepository,
            vppIdNetworkRepository = VppModule.provideVppIdNetworkRepository(keyValueRepository, logRecordRepository),
            notificationRepository = notificationRepository,
            stringRepository = stringRepository,
            defaultLessonRepository = defaultLessonRepository,
            keyValueRepository = keyValueRepository
        )
    }

    @Provides
    @Singleton
    fun provideHomeworkReminderUseCase(
        homeworkRepository: HomeworkRepository,
        notificationRepository: NotificationRepository,
        stringRepository: StringRepository,
        keyValueRepository: KeyValueRepository,
        @ApplicationContext context: Context
    ) = HomeworkReminderUseCase(
        homeworkRepository = homeworkRepository,
        notificationRepository = notificationRepository,
        stringRepository = stringRepository,
        keyValueRepository = keyValueRepository,
        context = context
    )
}