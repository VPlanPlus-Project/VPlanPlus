package es.jvbabi.vplanplus.feature.main_homework.list.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.AddTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.ChangeVisibilityUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.DeleteHomeworkTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.DeleteHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.EditTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.GetHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.HideHomeworkNotificationBannerUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.HideHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.HomeworkUseCases
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.IsUpdateRunningUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.MarkAllDoneUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.MarkSingleDoneUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.ShowHomeworkNotificationBannerUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.UpdateDueDateUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.UpdateUseCase
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
        getCurrentProfileUseCase: GetCurrentProfileUseCase
    ): HomeworkUseCases {
        return HomeworkUseCases(
            GetHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase
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