package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.DefaultValues
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate
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
    suspend fun getVPlanData(school: School, date: LocalDate): DataResponse<VPlanData?> {
        return vPlanRepository.getVPlanData(school, date)
    }

    suspend fun processVplanData(vPlanData: VPlanData) {

        val planDateFormatter = DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.GERMAN)
        val planDate = LocalDate.parse(vPlanData.wPlanDataObject.head!!.date!!, planDateFormatter)

        /*val createDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
        val lastPlanUpdate = LocalDateTime.parse(
            vPlanData.wPlanDataObject.head!!.timestampString!!,
            createDateFormatter
        )*/

        val school = schoolRepository.getSchoolFromId(vPlanData.schoolId)

        vPlanData.wPlanDataObject.classes!!.forEach {

            val `class` = classRepository.getClassBySchoolIdAndClassName(
                vPlanData.schoolId,
                it.schoolClass,
                true
            )!!

            // set lessons
            lessonRepository.deleteLessonForClass(`class`, planDate)
            it.lessons!!.forEach lesson@{ lesson ->

                val defaultLesson =
                    it.defaultLessons!!.find { defaultLesson -> defaultLesson.defaultLesson!!.lessonId!! == lesson.defaultLessonVpId }?.defaultLesson

                if (`class`.className == "VERW") {
                    return@lesson // TODO handle this
                }
                //Log.d("VPlanUseCases", "Processing lesson ${lesson.lesson} for class ${`class`.className}")
                val rooms = if (DefaultValues.isEmpty(lesson.room.room)) emptyList() else {
                    var rooms = lesson.room.room
                    if (roomRepository.getRoomByName(school, rooms, false) == null) {
                        rooms = " $rooms "
                        roomRepository.getRoomsBySchool(school).filter { room ->
                            val regex = Regex(" ${room.name} ")
                            regex.containsMatchIn(rooms)
                        }
                    } else {
                        listOf(roomRepository.getRoomByName(school, rooms, false)!!)
                    }
                }
                val roomChanged = lesson.room.roomChanged == "RaGeaendert"

                val teachers = if (DefaultValues.isEmpty(lesson.teacher.teacher)) emptyList() else {
                    if (lesson.teacher.teacher.contains(",")) {
                        teacherReository.getTeachersBySchoolId(school.id!!).filter { teacher ->
                            lesson.teacher.teacher.split(",").contains(teacher.acronym)
                        }
                    } else {
                        listOf(teacherReository.find(school, lesson.teacher.teacher, true)!!)
                    }
                }
                val teacherChanged = lesson.teacher.teacherChanged == "LeGeaendert"

                var originalSubject = defaultLesson?.subjectShort ?: "-"
                var changedSubject =
                    if (lesson.subject.subjectChanged == "FaGeaendert") lesson.subject.subject else null
                if (listOf(
                        "&nbsp;",
                        "&amp;nbsp;",
                        "---"
                    ).contains(originalSubject)
                ) originalSubject = "-"
                if (listOf("&nbsp;", "&amp;nbsp;", "---").contains(changedSubject)) changedSubject =
                    "-"
                lessonRepository.insertLesson(
                    Lesson(
                        classId = `class`.id!!,
                        info = lesson.info,
                        roomIsChanged = roomChanged,
                        teacherIsChanged = teacherChanged,
                        originalSubject = originalSubject,
                        changedSubject = changedSubject,
                        lesson = lesson.lesson,
                        dayTimestamp = DateUtils.getDayTimestamp(planDate)
                    )
                        .withRooms(rooms)
                        .withTeachers(teachers)
                )
            }
        }
    }
}