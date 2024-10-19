package es.jvbabi.vplanplus.feature.ndp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetNextDayUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.ToggleHomeworkHiddenStateUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.ChangeTaskDoneStateUseCase
import es.jvbabi.vplanplus.feature.ndp.data.repository.NdpUsageRepositoryImpl
import es.jvbabi.vplanplus.feature.ndp.domain.repository.NdpUsageRepository
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.TriggerNdpReminderNotificationUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.guided.GetExamsToGetRemindedUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.guided.MarkExamRemindersAsViewedUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.guided.NdpGuidedUseCases
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.reminderscheduler.OnNdpFinishedUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.reminderscheduler.OnNdpStartedUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NdpModule {

    @Provides
    @Singleton
    fun provideNdpUsageRepository(db: VppDatabase): NdpUsageRepository = NdpUsageRepositoryImpl(db.ndpUsageDao)

    @Provides
    @Singleton
    fun provideTriggerNdpReminderNotificationUseCase(
        profileRepository: ProfileRepository,
        notificationRepository: NotificationRepository,
        stringRepository: StringRepository,
        examRepository: ExamRepository,
        getNextDayUseCase: GetNextDayUseCase
    ) = TriggerNdpReminderNotificationUseCase(
        profileRepository = profileRepository,
        notificationRepository = notificationRepository,
        stringRepository = stringRepository,
        examRepository = examRepository,
        getNextDayUseCase = getNextDayUseCase
    )

    @Provides
    @Singleton
    fun provideNdpGuidedUseCases(
        homeworkRepository: HomeworkRepository,
        examRepository: ExamRepository,
        ndpUsageRepository: NdpUsageRepository,
        getCurrentProfileUseCase: GetCurrentProfileUseCase
    ) = NdpGuidedUseCases(
        toggleHomeworkHiddenUseCase = ToggleHomeworkHiddenStateUseCase(homeworkRepository),
        toggleTaskDoneStateUseCase = ChangeTaskDoneStateUseCase(homeworkRepository),

        getExamsToGetRemindedUseCase = GetExamsToGetRemindedUseCase(examRepository, getCurrentProfileUseCase),

        markExamRemindersAsViewedUseCase = MarkExamRemindersAsViewedUseCase(examRepository, getCurrentProfileUseCase),

        onNdpStartedUseCase = OnNdpStartedUseCase(ndpUsageRepository, getCurrentProfileUseCase),
        onNdpFinishedUseCase = OnNdpFinishedUseCase(ndpUsageRepository, getCurrentProfileUseCase)
    )
}