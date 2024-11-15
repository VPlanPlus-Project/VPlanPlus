package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.DataType
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GetDayUseCase(
    private val planRepository: PlanRepository,
    private val timetableRepository: TimetableRepository,
    private val lessonRepository: LessonRepository,
    private val holidayRepository: HolidayRepository,
    private val examRepository: ExamRepository,
    private val homeworkRepository: HomeworkRepository,
    private val keyValueRepository: KeyValueRepository,
    private val gradeRepository: GradeRepository,
) {

    /**
     * @param fast If true, it will load the lessons, emit them and then load the exams and homeworks. If false, it will wait until all the data is loaded before emitting the school day.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        date: LocalDate = LocalDate.now(),
        profile: Profile,
        fast: Boolean = true
    ): Flow<SchoolDay> {
        var schoolDay = SchoolDay(date)
        return keyValueRepository.getFlowOrDefault(Keys.LESSON_VERSION_NUMBER, "0")
            .flatMapLatest { version ->
                flow {
                    val day = planRepository.getDayInfoForSchool(
                        profile.getSchool().id,
                        date,
                        version.toLong()
                    ).first()
                    var dataType: DataType
                    val lessons = if (day == null) {
                        dataType = DataType.TIMETABLE
                        val lessons = when (profile) {
                            is ClassProfile -> timetableRepository.getTimetableForGroup(
                                profile.group,
                                date
                            )

                            else -> emptyList()
                        }
                        if (lessons.isEmpty()) dataType = DataType.NO_DATA
                        lessons
                    } else {
                        dataType = DataType.SUBSTITUTION_PLAN
                        lessonRepository.getLessonsForProfile(profile, date, version.toLong())
                            .first()
                    }
                        .orEmpty()
                        .filter { lesson ->
                            if (profile is ClassProfile && lesson is Lesson.SubstitutionPlanLesson) profile.isDefaultLessonEnabled(
                                lesson.defaultLesson?.vpId
                            )
                            else true
                        }

                    val isHoliday = holidayRepository.isHoliday(profile.getSchool().id, date)
                    schoolDay = schoolDay.copy(
                        lessons = if (isHoliday) emptyList() else lessons,
                        info = day?.info,
                        type = if (isHoliday) DayType.HOLIDAY else if (date.dayOfWeek.value > profile.getSchool().daysPerWeek) DayType.WEEKEND else DayType.NORMAL,
                        dataType = dataType,
                        version = version.toLongOrNull() ?: -1L
                    )
                    if (fast) emit(schoolDay)

                    val homeworkFlow =
                        (profile as? ClassProfile)?.let { homeworkRepository.getAllByProfile(it) }
                            ?: flow { emit(emptyList()) }
                    val gradesFlow = (profile as? ClassProfile)?.vppId?.let {
                        gradeRepository.getGradesByUser(
                            it,
                            date
                        ).map { grades -> grades.filter { grade -> grade.givenAt == date } }
                    } ?: flow { emit(emptyList()) }
                    val examsFlow = (profile as? ClassProfile)?.let {
                        examRepository.getExams(
                            date,
                            (profile as? ClassProfile)
                        )
                    } ?: flow { emit(emptyList()) }
                    combine(
                        homeworkFlow,
                        gradesFlow,
                        examsFlow
                    ) { homework, grades, exams ->
                        schoolDay.copy(
                            homework = homework
                                .filter {
                                    (it is PersonalizedHomework.LocalHomework || (it is PersonalizedHomework.CloudHomework && !it.isHidden)) && (it.homework.until.toLocalDate() == date || (!it.allDone() && it.homework.until.toLocalDate()
                                        .isBefore(LocalDate.now())))
                                }
                                .sortedBy { "${it.homework.until.toEpochSecond()}__${it.homework.defaultLesson?.subject}" },
                            grades = grades,
                            exams = exams
                        )
                    }.collect {
                        schoolDay = it
                        emit(schoolDay)
                    }
                }
            }
    }
}