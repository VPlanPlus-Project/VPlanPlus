package es.jvbabi.vplanplus.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
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
    private val defaultLessonRepository: DefaultLessonRepository,
    private val classRepository: ClassRepository,
    private val teacherReository: TeacherRepository,
    private val roomRepository: RoomRepository
) {
    suspend fun getVPlanData(school: School, date: LocalDate): OnlineResponse<VPlanData?> {
        return vPlanRepository.getVPlanData(school, date)
    }

    suspend fun processVplanData(vPlanData: VPlanData) {

        val planDateFormatter = DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.GERMAN)
        val planDate = LocalDate.parse(vPlanData.wPlanDataObject.head!!.date!!, planDateFormatter)

        val createDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
        val lastPlanUpdate = LocalDateTime.parse(vPlanData.wPlanDataObject.head!!.timestampString!!, createDateFormatter)

        vPlanData.wPlanDataObject.classes!!.forEach {

            // get class or create if not exists
            val dbClass = try {
                classRepository.getClassById(classRepository.getClassIdBySchoolIdAndClassName(vPlanData.schoolId, it.schoolClass))
            } catch (e: NullPointerException) {
                classRepository.createClass(
                    schoolId = vPlanData.schoolId,
                    className = it.schoolClass
                )
                classRepository.getClassById(classRepository.getClassIdBySchoolIdAndClassName(vPlanData.schoolId, it.schoolClass))
            }

            // set default lessons
            it.defaultLessons!!.forEach { defaultLessonWrapper ->
                // get or create teacher
                var teacher = teacherReository.find(schoolId = vPlanData.schoolId, acronym = defaultLessonWrapper.defaultLesson!!.teacherShort!!)
                if (teacher == null) {
                    teacherReository.createTeacher(schoolId = vPlanData.schoolId, acronym = defaultLessonWrapper.defaultLesson!!.teacherShort!!)
                    teacher = teacherReository.find(schoolId = vPlanData.schoolId, acronym = defaultLessonWrapper.defaultLesson!!.teacherShort!!)!!
                }

                // create default lesson
                val defaultLesson = DefaultLesson(
                    schoolId = vPlanData.schoolId,
                    vpId = defaultLessonWrapper.defaultLesson!!.lessonId!!,
                    subject = defaultLessonWrapper.defaultLesson!!.subjectShort!!,
                    teacherId = teacher.id!!,
                )
                defaultLessonRepository.updateDefaultLesson(defaultLesson)
            }

            // set actual lessons
            lessonRepository.deleteLessonForClass(dbClass.id!!, planDate)
            it.lessons!!.forEach { lesson ->
                val defaultLesson = if (lesson.defaultLessonVpId == null) null else defaultLessonRepository.getDefaultLessonByVpId(vPlanData.schoolId, lesson.defaultLessonVpId!!)
                var room = roomRepository.getRoomByName(vPlanData.schoolId, lesson.room.room)
                if (room == null) {
                    if (lesson.room.room != "") {
                        roomRepository.createRoom(
                            Room(
                                schoolId = vPlanData.schoolId,
                                name = lesson.room.room
                            )
                        )
                        room = roomRepository.getRoomByName(vPlanData.schoolId, lesson.room.room)
                    }
                }
                val teacherId = if (lesson.teacher.teacherChanged == "LeGeaendert") {
                    try {
                        teacherReository.find(schoolId = vPlanData.schoolId, acronym = lesson.teacher.teacher)?.id?:-1
                    } catch (e: NullPointerException) {
                        Log.e("ERROR", e.stackTraceToString())
                    }
                } else {
                    null
                }
                lessonRepository.insertLesson(
                    Lesson(
                        defaultLessonId = defaultLesson?.id,
                        classId = dbClass.id,
                        roomId = room?.id!!,
                        changedInfo = lesson.info,
                        changedSubject = if (lesson.subject.subjectChanged == "FaGeaendert") lesson.subject.subject else null,
                        changedTeacherId = teacherId,
                        lesson = lesson.lesson,
                        roomIsChanged = lesson.room.roomChanged == "RaGeaendert",
                        timestamp = DateUtils.getDayTimestamp(planDate)
                    )
                )
            }
        }
    }
}