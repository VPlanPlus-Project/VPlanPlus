package es.jvbabi.vplanplus.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class VPlanUseCases(
    private val vPlanRepository: VPlanRepository,
    private val lessonRepository: LessonRepository,
    private val classRepository: ClassRepository,
    private val teacherReository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val schoolRepository: SchoolRepository
) {
    suspend fun getVPlanData(school: School, date: LocalDate): OnlineResponse<VPlanData?> {
        return vPlanRepository.getVPlanData(school, date)
    }

    suspend fun processVplanData(vPlanData: VPlanData) {

        val planDateFormatter = DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.GERMAN)
        val planDate = LocalDate.parse(vPlanData.wPlanDataObject.head!!.date!!, planDateFormatter)

        val createDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
        val lastPlanUpdate = LocalDateTime.parse(vPlanData.wPlanDataObject.head!!.timestampString!!, createDateFormatter)

        val school = schoolRepository.getSchoolFromId(vPlanData.schoolId)

        vPlanData.wPlanDataObject.classes!!.forEach {

            val `class` = classRepository.getClassBySchoolIdAndClassName(vPlanData.schoolId, it.schoolClass, true)!!

            // set lessons
            lessonRepository.deleteLessonForClass(`class`, planDate)
            it.lessons!!.forEach lesson@{ lesson ->
                if (`class`.className == "VERW") {
                    return@lesson // TODO handle this
                }
                Log.d("VPlanUseCases", "Processing lesson ${lesson.lesson} for class ${`class`.className}")
                val room = roomRepository.getRoomByName(school, lesson.room.room, true)!!
                val roomChanged = lesson.room.roomChanged == "RaGeaendert"
                val originalTeacher = teacherReository.find(school, it.defaultLessons!!.find { defaultLesson -> defaultLesson.defaultLesson!!.lessonId!! == lesson.defaultLessonVpId }?.defaultLesson?.teacherShort?:"", true)!!
                val changedTeacher = if (lesson.teacher.teacherChanged == "LeGeaendert") {
                    teacherReository.find(school, lesson.teacher.teacher, true)
                } else {
                    null
                }

                val originalSubject = it.defaultLessons!!.find { defaultLesson -> defaultLesson.defaultLesson!!.lessonId!! == lesson.defaultLessonVpId }?.defaultLesson?.subjectShort?:"-"
                val changedSubject = if (lesson.subject.subjectChanged == "FaGeaendert") lesson.subject.subject else null
                lessonRepository.insertLesson(
                    Lesson(
                        classId = `class`.id!!,
                        info = lesson.info,
                        roomIsChanged = roomChanged,
                        originalRoomId = room.id!!,
                        originalSubject = originalSubject,
                        changedSubject = changedSubject,
                        originalTeacherId = originalTeacher.id,
                        changedTeacherId = changedTeacher?.id,
                        lesson = lesson.lesson,
                        dayTimestamp = DateUtils.getDayTimestamp(planDate)
                    )
                )
            }
        }
    }
}