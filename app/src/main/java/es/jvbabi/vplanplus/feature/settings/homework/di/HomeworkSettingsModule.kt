package es.jvbabi.vplanplus.feature.settings.homework.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
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
        homeworkRepository: HomeworkRepository
    ) = HomeworkSettingsUseCases(
        isShowNotificationOnNewHomeworkUseCase = IsShowNotificationOnNewHomeworkUseCase(keyValueRepository),
        setShowNotificationOnNewHomeworkUseCase = SetShowNotificationOnNewHomeworkUseCase(keyValueRepository),
        isRemindOnUnfinishedHomeworkUseCase = IsRemindOnUnfinishedHomeworkUseCase(keyValueRepository),
        setRemindOnUnfinishedHomeworkUseCase = SetRemindOnUnfinishedHomeworkUseCase(keyValueRepository),
        getDefaultNotificationTimeUseCase = GetDefaultNotificationTimeUseCase(keyValueRepository),
        setDefaultNotificationTimeUseCase = SetDefaultNotificationTimeUseCase(keyValueRepository),
        getPreferredHomeworkNotificationTimeUseCase = GetPreferredHomeworkNotificationTimeUseCase(homeworkRepository),
        setPreferredHomeworkNotificationTimeUseCase = SetPreferredHomeworkNotificationTimeUseCase(homeworkRepository),
        removePreferredHomeworkNotificationTimeUseCase = RemovePreferredHomeworkNotificationTimeUseCase(homeworkRepository)
    )
}