package es.jvbabi.vplanplus.feature.homework.view.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.AddTaskUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.ChangeVisibilityUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.DeleteHomeworkTaskUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.DeleteHomeworkUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.EditTaskUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.GetHomeworkUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.HideHomeworkNotificationBannerUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.HideHomeworkUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.HomeworkUseCases
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.IsUpdateRunningUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.MarkAllDoneUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.MarkSingleDoneUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.ShowHomeworkNotificationBannerUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.UpdateDueDateUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.UpdateUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkViewModule {

    @Provides
    @Singleton
    fun provideHomeworkUseCases(
        homeworkRepository: HomeworkRepository,
        keyValueRepository: KeyValueRepository,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    ): HomeworkUseCases {
        return HomeworkUseCases(
            getHomeworkUseCase = GetHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase
            ),
            markAllDoneUseCase = MarkAllDoneUseCase(
                homeworkRepository = homeworkRepository
            ),
            markSingleDoneUseCase = MarkSingleDoneUseCase(
                homeworkRepository = homeworkRepository
            ),
            addTaskUseCase = AddTaskUseCase(
                homeworkRepository = homeworkRepository
            ),
            deleteHomeworkUseCase = DeleteHomeworkUseCase(
                homeworkRepository = homeworkRepository
            ),
            changeVisibilityUseCase = ChangeVisibilityUseCase(
                homeworkRepository = homeworkRepository
            ),
            deleteHomeworkTaskUseCase = DeleteHomeworkTaskUseCase(
                homeworkRepository = homeworkRepository
            ),
            editTaskUseCase = EditTaskUseCase(
                homeworkRepository = homeworkRepository
            ),
            isUpdateRunningUseCase = IsUpdateRunningUseCase(
                keyValueRepository = keyValueRepository
            ),
            updateUseCase = UpdateUseCase(
                homeworkRepository = homeworkRepository
            ),
            hideHomeworkUseCase = HideHomeworkUseCase(
                homeworkRepository = homeworkRepository
            ),
            showHomeworkNotificationBannerUseCase = ShowHomeworkNotificationBannerUseCase(
                keyValueRepository = keyValueRepository
            ),
            hideHomeworkNotificationBannerUseCase = HideHomeworkNotificationBannerUseCase(
                keyValueRepository = keyValueRepository
            ),
            updateDueDateUseCase = UpdateDueDateUseCase(
                homeworkRepository = homeworkRepository
            )
        )
    }
}