package es.jvbabi.vplanplus.feature.main_homework.view.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.AddTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.ChangeVisibilityUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.DeleteHomeworkTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.DeleteHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.EditTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.GetHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.HideHomeworkNotificationBannerUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.HideHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.HomeworkUseCases
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.IsUpdateRunningUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.MarkAllDoneUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.MarkSingleDoneUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.ShowHomeworkNotificationBannerUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.UpdateDueDateUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.UpdateUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.UpdateHomeworkEnabledUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkViewModule {

    @Provides
    @Singleton
    fun provideHomeworkUseCases(
        homeworkRepository: HomeworkRepository,
        profileRepository: ProfileRepository,
        keyValueRepository: KeyValueRepository,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    ): HomeworkUseCases {
        return HomeworkUseCases(
            GetHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase
            ), MarkAllDoneUseCase(
                homeworkRepository = homeworkRepository
            ), MarkSingleDoneUseCase(
                homeworkRepository = homeworkRepository
            ), AddTaskUseCase(
                homeworkRepository = homeworkRepository
            ), DeleteHomeworkUseCase(
                homeworkRepository = homeworkRepository
            ), ChangeVisibilityUseCase(
                homeworkRepository = homeworkRepository
            ), DeleteHomeworkTaskUseCase(
                homeworkRepository = homeworkRepository
            ), EditTaskUseCase(
                homeworkRepository = homeworkRepository
            ), IsUpdateRunningUseCase(
                keyValueRepository = keyValueRepository
            ), UpdateUseCase(
                homeworkRepository = homeworkRepository
            ), HideHomeworkUseCase(
                homeworkRepository = homeworkRepository
            ), ShowHomeworkNotificationBannerUseCase(
                keyValueRepository = keyValueRepository
            ), HideHomeworkNotificationBannerUseCase(
                keyValueRepository = keyValueRepository
            ),
            UpdateDueDateUseCase(homeworkRepository),
            UpdateHomeworkEnabledUseCase(profileRepository, homeworkRepository)
        )
    }
}