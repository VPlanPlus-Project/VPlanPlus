package es.jvbabi.vplanplus.feature.main_homework.view.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_homework.list_old.domain.usecase.AddTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list_old.domain.usecase.DeleteHomeworkTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list_old.domain.usecase.EditTaskUseCase
import es.jvbabi.vplanplus.feature.main_homework.list_old.domain.usecase.UpdateDueDateUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.ChangeTaskDoneStateUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.GetHomeworkByIdUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.HomeworkDetailUseCases
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.UpdateDocumentsUseCase
import es.jvbabi.vplanplus.feature.main_homework.view.domain.usecase.UpdateHomeworkVisibilityUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkDetailModule {

    @Provides
    @Singleton
    fun provideHomeworkDetailUseCases(
        homeworkRepository: HomeworkRepository,
        fileRepository: FileRepository,
        changeTaskDoneStateUseCase: ChangeTaskDoneStateUseCase,
        getCurrentProfileUseCase: GetCurrentProfileUseCase
    ) = HomeworkDetailUseCases(
        getCurrentProfileUseCase = getCurrentProfileUseCase,
        getHomeworkByIdUseCase = GetHomeworkByIdUseCase(homeworkRepository),
        taskDoneUseCase = changeTaskDoneStateUseCase,
        updateDueDateUseCase = UpdateDueDateUseCase(homeworkRepository, getCurrentProfileUseCase),
        deleteHomeworkTaskUseCase = DeleteHomeworkTaskUseCase(homeworkRepository, getCurrentProfileUseCase),
        editTaskUseCase = EditTaskUseCase(homeworkRepository, getCurrentProfileUseCase),
        addTaskUseCase = AddTaskUseCase(homeworkRepository, getCurrentProfileUseCase),
        updateHomeworkVisibilityUseCase = UpdateHomeworkVisibilityUseCase(homeworkRepository, getCurrentProfileUseCase),
        updateDocumentsUseCase = UpdateDocumentsUseCase(homeworkRepository, fileRepository, getCurrentProfileUseCase)
    )
}