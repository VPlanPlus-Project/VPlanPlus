package es.jvbabi.vplanplus.feature.ndp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetNextDayUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.ToggleHomeworkHiddenStateUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.ChangeTaskDoneStateUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.TriggerNdpReminderNotificationUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.guided.GetExamsToGetRemindedUseCase
import es.jvbabi.vplanplus.feature.ndp.domain.usecase.guided.NdpGuidedUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NdpModule {

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
        getCurrentProfileUseCase: GetCurrentProfileUseCase
    ) = NdpGuidedUseCases(
        toggleHomeworkHiddenUseCase = ToggleHomeworkHiddenStateUseCase(homeworkRepository),
        toggleTaskDoneStateUseCase = ChangeTaskDoneStateUseCase(homeworkRepository),

        getExamsToGetRemindedUseCase = GetExamsToGetRemindedUseCase(examRepository, getCurrentProfileUseCase)
    )
}