package es.jvbabi.vplanplus.feature.main_homework.list.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
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
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.ShowHomeworkNotificationBannerUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.UpdateDueDateUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.UpdateUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.ChangeTaskDoneStateUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.UpdateHomeworkUseCase
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
        fileRepository: FileRepository,
        keyValueRepository: KeyValueRepository,
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        changeTaskDoneStateUseCase: ChangeTaskDoneStateUseCase,
        updateHomeworkUseCase: UpdateHomeworkUseCase
    ): HomeworkUseCases {
        return HomeworkUseCases(
            GetHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase
            ),
            MarkAllDoneUseCase(changeTaskDoneStateUseCase),
            changeTaskDoneStateUseCase,
            addTaskUseCase = AddTaskUseCase(
                homeworkRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase
            ),
            deleteHomeworkUseCase = DeleteHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                fileRepository = fileRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase
            ),
            changeVisibilityUseCase = ChangeVisibilityUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase
            ),
            deleteHomeworkTaskUseCase = DeleteHomeworkTaskUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase
            ),
            editTaskUseCase = EditTaskUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase
            ),
            isUpdateRunningUseCase = IsUpdateRunningUseCase(keyValueRepository),
            updateUseCase = UpdateUseCase(updateHomeworkUseCase),
            hideHomeworkUseCase = HideHomeworkUseCase(homeworkRepository),
            showHomeworkNotificationBannerUseCase = ShowHomeworkNotificationBannerUseCase(keyValueRepository),
            hideHomeworkNotificationBannerUseCase = HideHomeworkNotificationBannerUseCase(keyValueRepository),
            updateDueDateUseCase = UpdateDueDateUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase
            ),
            updateHomeworkEnabledUseCase = UpdateHomeworkEnabledUseCase(profileRepository, homeworkRepository)
        )
    }
}