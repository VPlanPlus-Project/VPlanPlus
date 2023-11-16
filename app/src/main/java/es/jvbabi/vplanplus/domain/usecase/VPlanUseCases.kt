package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.source.database.dao.LessonRoomCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonTeacherCrossoverDao
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.DefaultValues
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class VPlanUseCases(
    private val vPlanRepository: VPlanRepository,
    private val lessonRepository: LessonRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val schoolRepository: SchoolRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val lessonTeacherCrossover: LessonTeacherCrossoverDao,
    private val lessonRoomCrossover: LessonRoomCrossoverDao
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

                val dbDefaultLesson = if (defaultLesson != null) defaultLessonRepository.getDefaultLessonByVpId(defaultLesson.lessonId!!.toLong()) else null
                var defaultLessonDbId = dbDefaultLesson?.id

                if (`class`.className == "VERW") {
                    return@lesson // TODO handle this
                }
                //Log.d("VPlanUseCases", "Processing lesson ${lesson.lesson} for class ${`class`.className}")
                val dbRooms = if (DefaultValues.isEmpty(lesson.room.room)) emptyList() else {
                    val rooms = lesson.room.room

                    if (roomRepository.getRoomsBySchool(school).map { r -> r.name }.contains(rooms)) listOf(roomRepository.getRoomByName(school, rooms, false)!!)
                    else {
                        val existingRooms = roomRepository.getRoomsBySchool(school).filter { room ->
                            val regex = Regex(" ${room.name} ")
                            regex.containsMatchIn(" $rooms ")
                        }
                        existingRooms.ifEmpty { listOf(roomRepository.getRoomByName(school, rooms, true)!!) }
                    }
                }
                val roomChanged = lesson.room.roomChanged == "RaGeaendert"

                val dbTeachers = if (DefaultValues.isEmpty(lesson.teacher.teacher)) emptyList() else {
                    if (lesson.teacher.teacher.contains(",")) {
                        teacherRepository.getTeachersBySchoolId(school.schoolId).filter { teacher ->
                            lesson.teacher.teacher.split(",").contains(teacher.acronym)
                        }
                    } else {
                        listOf(teacherRepository.find(school, lesson.teacher.teacher, true)!!)
                    }
                }

                var changedSubject =
                    if (lesson.subject.subjectChanged == "FaGeaendert") lesson.subject.subject else null
                if (listOf("&nbsp;", "&amp;nbsp;", "---").contains(changedSubject)) changedSubject =
                    "-"

                if (dbDefaultLesson == null && defaultLesson != null) {
                    defaultLessonDbId = defaultLessonRepository.insert(
                        vpId = defaultLesson.lessonId!!.toLong(),
                        subject = defaultLesson.subjectShort!!,
                        teacherId = teacherRepository.find(school, defaultLesson.teacherShort!!, false)?.teacherId,
                        classId = `class`.classId
                    )
                }

                val lessonId = lessonRepository.insertLesson(
                    DbLesson(
                        roomIsChanged = roomChanged,
                        lessonNumber = lesson.lesson,
                        day = planDate,
                        info = if (DefaultValues.isEmpty(lesson.info)) null else lesson.info,
                        defaultLessonId = defaultLessonDbId,
                        changedSubject = changedSubject,
                        classLessonRefId = `class`.classId
                    )
                )

                dbRooms.forEach { room ->
                    lessonRoomCrossover.insertCrossover(
                        lessonId, room.roomId
                    )
                }

                dbTeachers.forEach { teacher ->
                    lessonTeacherCrossover.insertCrossover(
                        lessonId, teacher.teacherId
                    )
                }
            }
        }
    }

    suspend fun deletePlans() {
        lessonRepository.deleteAllLessons()
    }
}