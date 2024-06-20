package es.jvbabi.vplanplus.feature.main_homework.view.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.AddTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.DeleteHomeworkTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.EditTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.MarkSingleDoneUseCase
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.UpdateDueDateUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.GetHomeworkByIdUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.HomeworkDetailUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkDetailModule {

    @Provides
    @Singleton
    fun provideHomeworkDetailUseCases(
        homeworkRepository: HomeworkRepository,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    ) = HomeworkDetailUseCases(
        getCurrentIdentityUseCase = getCurrentIdentityUseCase,
        getHomeworkByIdUseCase = GetHomeworkByIdUseCase(homeworkRepository),
        taskDoneUseCase = MarkSingleDoneUseCase(homeworkRepository),
        updateDueDateUseCase = UpdateDueDateUseCase(homeworkRepository),
        deleteHomeworkTaskUseCase = DeleteHomeworkTaskUseCase(homeworkRepository),
        editTaskUseCase = EditTaskUseCase(homeworkRepository),
        addTaskUseCase = AddTaskUseCase(homeworkRepository)
    )
}