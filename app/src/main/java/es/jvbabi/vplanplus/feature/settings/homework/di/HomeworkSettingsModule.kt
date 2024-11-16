package es.jvbabi.vplanplus.feature.settings.homework.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.home.SetUpUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.CanSendNotificationUseCase
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.GetDefaultNotificationTimeUseCase
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.GetPreferredHomeworkNotificationTimeUseCase
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.HomeworkSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.IsRemindOnUnfinishedHomeworkUseCase
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.IsShowNotificationOnNewHomeworkUseCase
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.RemovePreferredHomeworkNotificationTimeUseCase
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.SetDefaultNotificationTimeUseCase
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.SetPreferredHomeworkNotificationTimeUseCase
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.SetRemindOnUnfinishedHomeworkUseCase
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.SetShowNotificationOnNewHomeworkUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkSettingsModule {

    @Provides
    @Singleton
    fun provideHomeworkSettingsUseCases(
        keyValueRepository: KeyValueRepository,
        homeworkRepository: HomeworkRepository,
        alarmManagerRepository: AlarmManagerRepository,
        setUpUseCase: SetUpUseCase
    ) = HomeworkSettingsUseCases(
        isShowNotificationOnNewHomeworkUseCase = IsShowNotificationOnNewHomeworkUseCase(keyValueRepository),
        setShowNotificationOnNewHomeworkUseCase = SetShowNotificationOnNewHomeworkUseCase(keyValueRepository),
        isRemindOnUnfinishedHomeworkUseCase = IsRemindOnUnfinishedHomeworkUseCase(keyValueRepository),
        setRemindOnUnfinishedHomeworkUseCase = SetRemindOnUnfinishedHomeworkUseCase(
            keyValueRepository = keyValueRepository,
            setUpUseCase = setUpUseCase
        ),
        getDefaultNotificationTimeUseCase = GetDefaultNotificationTimeUseCase(keyValueRepository),
        setDefaultNotificationTimeUseCase = SetDefaultNotificationTimeUseCase(
            keyValueRepository = keyValueRepository,
            setUpUseCase = setUpUseCase
        ),
        getPreferredHomeworkNotificationTimeUseCase = GetPreferredHomeworkNotificationTimeUseCase(homeworkRepository),
        setPreferredHomeworkNotificationTimeUseCase = SetPreferredHomeworkNotificationTimeUseCase(
            homeworkRepository = homeworkRepository,
            setUpUseCase = setUpUseCase
        ),
        removePreferredHomeworkNotificationTimeUseCase = RemovePreferredHomeworkNotificationTimeUseCase(homeworkRepository),
        canSendNotificationUseCase = CanSendNotificationUseCase(alarmManagerRepository)
    )
}