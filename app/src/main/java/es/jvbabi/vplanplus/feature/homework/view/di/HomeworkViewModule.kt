package es.jvbabi.vplanplus.feature.homework.view.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.GetHomeworkUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.HomeworkUseCases
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.MarkAllDoneUseCase
import es.jvbabi.vplanplus.feature.homework.view.domain.usecase.MarkSingleDoneUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkViewModule {

    @Provides
    @Singleton
    fun provideHomeworkUseCases(
        homeworkRepository: HomeworkRepository,
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
            )
        )
    }
}