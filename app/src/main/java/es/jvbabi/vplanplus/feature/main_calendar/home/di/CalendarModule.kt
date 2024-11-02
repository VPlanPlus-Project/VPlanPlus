package es.jvbabi.vplanplus.feature.main_calendar.home.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.CalendarViewUseCases
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.CanShowTimetableInfoBannerUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.DismissTimetableInfoBannerUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.GetDayUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetCurrentDataVersionUseCase
import es.jvbabi.vplanplus.feature.main_home.domain.usecase.GetLastSyncUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {

    @Provides
    @Singleton
    fun provideCalendarViewUseCases(
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        planRepository: PlanRepository,
        keyValueRepository: KeyValueRepository,
        homeworkRepository: HomeworkRepository,
        gradeRepository: GradeRepository,
        lessonRepository: LessonRepository,
        timetableRepository: TimetableRepository,
        holidayRepository: HolidayRepository,
        examRepository: ExamRepository
    ): CalendarViewUseCases = CalendarViewUseCases(
        getCurrentProfileUseCase = getCurrentProfileUseCase,
        getDayUseCase = GetDayUseCase(
            planRepository = planRepository,
            keyValueRepository = keyValueRepository,
            homeworkRepository = homeworkRepository,
            gradeRepository = gradeRepository,
            lessonRepository = lessonRepository,
            timetableRepository = timetableRepository,
            getCurrentProfileUseCase = getCurrentProfileUseCase,
            holidayRepository = holidayRepository,
            examRepository = examRepository
        ),
        getLastSyncUseCase = GetLastSyncUseCase(keyValueRepository),

        canShowTimetableInfoBannerUseCase = CanShowTimetableInfoBannerUseCase(keyValueRepository),
        dismissTimetableInfoBannerUseCase = DismissTimetableInfoBannerUseCase(keyValueRepository),
        getCurrentDataVersionUseCase = GetCurrentDataVersionUseCase(keyValueRepository)
    )
}