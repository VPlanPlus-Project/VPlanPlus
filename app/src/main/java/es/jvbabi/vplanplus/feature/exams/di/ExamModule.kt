package es.jvbabi.vplanplus.feature.exams.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.exams.data.repository.ExamRepositoryImpl
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.feature.exams.domain.usecase.details.DeleteExamUseCase
import es.jvbabi.vplanplus.feature.exams.domain.usecase.details.ExamDetailsUseCases
import es.jvbabi.vplanplus.feature.exams.domain.usecase.details.GetExamUseCase
import es.jvbabi.vplanplus.feature.exams.domain.usecase.details.UpdateExamCategoryUseCase
import es.jvbabi.vplanplus.feature.exams.domain.usecase.details.UpdateExamDateUseCase
import es.jvbabi.vplanplus.feature.exams.domain.usecase.details.UpdateExamDetailsUseCase
import es.jvbabi.vplanplus.feature.exams.domain.usecase.details.UpdateExamReminderDaysUseCase
import es.jvbabi.vplanplus.feature.exams.domain.usecase.details.UpdateExamTitleUseCase
import es.jvbabi.vplanplus.feature.exams.domain.usecase.new_exam.NewExamUseCases
import es.jvbabi.vplanplus.feature.exams.domain.usecase.new_exam.SaveExamUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.GetDefaultLessonsUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExamModule {

    @Provides
    @Singleton
    fun provideExamRepository(
        db: VppDatabase
    ): ExamRepository {
        return ExamRepositoryImpl(db.examDao)
    }

    @Provides
    @Singleton
    fun provideNewExamUseCases(
        defaultLessonRepository: DefaultLessonRepository,
        getCurrentProfileUseCase: GetCurrentProfileUseCase,

        examRepository: ExamRepository
    ): NewExamUseCases {
        return NewExamUseCases(
            getDefaultLessonsUseCase = GetDefaultLessonsUseCase(defaultLessonRepository, getCurrentProfileUseCase),
            getCurrentProfileUseCase = getCurrentProfileUseCase,

            saveExamUseCase = SaveExamUseCase(
                getCurrentProfileUseCase = getCurrentProfileUseCase,
                examRepository = examRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideExamDetailsUseCases(
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        examRepository: ExamRepository,
    ) = ExamDetailsUseCases(
        getExamUseCase = GetExamUseCase(examRepository),
        getCurrentProfileUseCase = getCurrentProfileUseCase,

        updateTitleUseCase = UpdateExamTitleUseCase(examRepository, getCurrentProfileUseCase),
        updateDateUseCase = UpdateExamDateUseCase(examRepository, getCurrentProfileUseCase),
        updateCategoryUseCase = UpdateExamCategoryUseCase(examRepository, getCurrentProfileUseCase),
        updateExamDetailsUseCase = UpdateExamDetailsUseCase(examRepository, getCurrentProfileUseCase),
        updateReminderDaysUseCase = UpdateExamReminderDaysUseCase(examRepository, getCurrentProfileUseCase),
        deleteExamUseCase = DeleteExamUseCase(examRepository, getCurrentProfileUseCase)
    )
}