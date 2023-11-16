package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.util.DateUtils
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime

class LessonTimeUseCases(
    private val lessonTimeRepository: LessonTimeRepository,
    private val roomRepository: RoomRepository,
    private val teacherRepository: TeacherRepository,
    private val lessonUseCases: LessonUseCases,
    private val classUseCases: ClassUseCases,
) {

    suspend fun getCurrentLessonNumber(profile: Profile): Int { // TODO test
        return when (profile.type) {
            ProfileType.STUDENT -> {
                val times =
                    lessonTimeRepository.getLessonTimesByClass(classUseCases.getClassById(profile.referenceId))
                times.entries.firstOrNull {
                    DateUtils.calculateProgress(
                        it.value.start,
                        it.value.end,
                        LocalTime.now().toString()
                    )!! in 0.0..1.0
                }?.value!!.lessonNumber
            }

            ProfileType.ROOM -> {
                val lessons = lessonUseCases.getLessonsForRoom(
                    roomRepository.getRoomById(profile.referenceId),
                    LocalDate.now()
                ).first().lessons
                lessons.firstOrNull {
                    DateUtils.calculateProgress(
                        DateUtils.localDateTimeToTimeString(
                            it.start
                        ), DateUtils.localDateTimeToTimeString(it.end), LocalTime.now().toString()
                    )!! in 0.0..1.0
                }?.lessonNumber ?: 0
            }

            ProfileType.TEACHER -> {
                val lessons = lessonUseCases.getLessonsForTeacher(
                    teacherRepository.getTeacherById(profile.referenceId)!!,
                    LocalDate.now()
                ).first().lessons
                lessons.firstOrNull {
                    DateUtils.calculateProgress(
                        DateUtils.localDateTimeToTimeString(
                            it.start
                        ), DateUtils.localDateTimeToTimeString(it.end), LocalTime.now().toString()
                    )!! in 0.0..1.0
                }?.lessonNumber ?: 0
            }
        }
    }
}