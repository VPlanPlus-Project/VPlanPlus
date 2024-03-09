package es.jvbabi.vplanplus.feature.settings.homework.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.HomeworkSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.IsShowNotificationOnNewHomeworkUseCase
import es.jvbabi.vplanplus.feature.settings.homework.domain.usecase.SetShowNotificationOnNewHomeworkUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkSettingsModule {

    @Provides
    @Singleton
    fun provideHomeworkSettingsUseCases(
        keyValueRepository: KeyValueRepository
    ) = HomeworkSettingsUseCases(
        isShowNotificationOnNewHomeworkUseCase = IsShowNotificationOnNewHomeworkUseCase(keyValueRepository),
        setShowNotificationOnNewHomeworkUseCase = SetShowNotificationOnNewHomeworkUseCase(keyValueRepository)
    )
}