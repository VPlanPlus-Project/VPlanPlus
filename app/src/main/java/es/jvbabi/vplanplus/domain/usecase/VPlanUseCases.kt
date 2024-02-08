package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.source.database.dao.LessonSchoolEntityCrossoverDao
import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.Plan
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.DefaultValues
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@Deprecated("This class is not used anymore")
class VPlanUseCases(
    private val vPlanRepository: VPlanRepository,
    private val lessonRepository: LessonRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val schoolRepository: SchoolRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val lessonSchoolEntityCrossoverDao: LessonSchoolEntityCrossoverDao,
    private val keyValueUseCases: KeyValueUseCases,
    private val planRepository: PlanRepository,
    private val lessonTimesRepository: LessonTimeRepository
) {
    suspend fun getVPlanData(school: School, date: LocalDate): DataResponse<VPlanData?> {
        return vPlanRepository.getVPlanData(school, date)
    }

    suspend fun processVPlanData(vPlanData: VPlanData) {

        val planDateFormatter = DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.GERMAN)
        val planDate = LocalDate.parse(vPlanData.wPlanDataObject.head!!.date!!, planDateFormatter)

        val createDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
        val lastPlanUpdate = LocalDateTime.parse(
            vPlanData.wPlanDataObject.head!!.timestampString!!,
            createDateFormatter
        )

        val school = schoolRepository.getSchoolFromId(vPlanData.schoolId)!!

        val version = keyValueUseCases.getOrDefault(Keys.LESSON_VERSION_NUMBER, "-2").toLong() + 1

        // lists to collect data for bulk insert
        val insertLessons = mutableListOf<DbLesson>()
        val roomCrossovers = mutableListOf<Pair<UUID, UUID>>()
        val teacherCrossovers = mutableListOf<Pair<UUID, UUID>>()

        // get rooms and teachers
        var rooms = roomRepository.getRoomsBySchool(school)
        var teachers = teacherRepository.getTeachersBySchoolId(school.schoolId)

        vPlanData.wPlanDataObject.classes!!.forEach {

            val `class` = classRepository.getClassBySchoolIdAndClassName(
                vPlanData.schoolId,
                it.schoolClass,
                true
            )!!
            val defaultLessons = defaultLessonRepository.getDefaultLessonByClassId(`class`.classId)
            val bookings = roomRepository.getRoomBookingsByClass(`class`)
            val times = lessonTimesRepository.getLessonTimesByClass(`class`)

            // set lessons
            it.lessons!!.forEach lesson@{ lesson ->
                val defaultLesson =
                    it.defaultLessons!!.find { defaultLesson -> defaultLesson.defaultLesson!!.lessonId!! == lesson.defaultLessonVpId }?.defaultLesson

                val dbDefaultLesson = defaultLessons.firstOrNull { dl -> dl.vpId == defaultLesson?.lessonId?.toLong() }
                var defaultLessonDbId = dbDefaultLesson?.defaultLessonId

                val rawTeacherAcronyms = if (DefaultValues.isEmpty(lesson.teacher.teacher)) emptyList() else {
                    if (lesson.teacher.teacher.replace(" ", ",").contains(",")) {
                        lesson.teacher.teacher.replace(" ", ",").split(",")
                    } else listOfNotNull(lesson.teacher.teacher)
                }

                val rawRoomNames = if (DefaultValues.isEmpty(lesson.room.room)) null
                else lesson.room.room

                val lessonRooms = mutableListOf<String>()

                // this algorithm tries to find existing rooms within the raw room string. It splits the string by spaces and tries to find a room with the joined string.
                // An example would be "TH 1 TH 2" where it's not clear where to split.
                // Time for another angry checkpoint: While teachers are separated by commas, rooms are separated by spaces. But sometimes, there are spaces in room names.
                if (rawRoomNames != null) {
                    if (rooms.map { r -> r.name }.contains(lesson.room.room)) {
                        lessonRooms.add(lesson.room.room)
                    } else {
                        val split = lesson.room.room.split(" ")
                        var join = 0
                        var start = 0
                        for (a in 0..split.size) {
                            val joined = split.subList(start, join).joinToString(" ")
                            if (rooms.map { r -> r.name }.contains(joined)) {
                                lessonRooms.add(joined)
                                start = join
                            }
                            join += 1
                        }
                        if (start == 0) lessonRooms.add(lesson.room.room)
                    }
                }

                // add teachers and rooms to db if they don't exist
                val addTeachers = rawTeacherAcronyms.filter { t -> !teachers.map { dbT -> dbT.acronym }.contains(t) }
                val addRooms = lessonRooms.filter { r -> !rooms.map { dbR -> dbR.name }.contains(r) }

                addTeachers.forEach { teacher ->
                    teacherRepository.createTeacher(
                        schoolId = school.schoolId,
                        acronym = teacher
                    )
                }

                addRooms.forEach { room ->
                    roomRepository.createRoom(
                        room = Room(
                            school = school,
                            name = room
                        )
                    )
                }

                if (addTeachers.isNotEmpty()) teachers = teacherRepository.getTeachersBySchoolId(school.schoolId)
                if (addRooms.isNotEmpty()) rooms = roomRepository.getRoomsBySchool(school)

                //Log.d("VPlanUseCases", "Processing lesson ${lesson.lesson} for class ${`class`.className}")
                val dbRooms = rooms.filter { r -> lessonRooms.contains(r.name) }
                val roomChanged = lesson.room.roomChanged == "RaGeaendert"

                val dbTeachers = teachers.filter { t -> rawTeacherAcronyms.contains(t.acronym) }

                var changedSubject =
                    if (lesson.subject.subjectChanged == "FaGeaendert") lesson.subject.subject else null
                if (listOf("&nbsp;", "&amp;nbsp;", "---", "").contains(changedSubject)) changedSubject =
                    "-"

                if (dbDefaultLesson == null && defaultLesson != null) {
                    defaultLessonDbId = defaultLessonRepository.insert(
                        DbDefaultLesson(
                            defaultLessonId = UUID.randomUUID(),
                            vpId = defaultLesson.lessonId!!.toLong(),
                            subject = defaultLesson.subjectShort!!,
                            teacherId = dbTeachers.firstOrNull { t -> t.acronym == defaultLesson.teacherShort }?.teacherId,
                            classId = `class`.classId
                        )
                    )
                } else if (addTeachers.isNotEmpty() && dbDefaultLesson != null && defaultLesson?.teacherShort != null && dbDefaultLesson.teacher == null && addTeachers.contains(defaultLesson.teacherShort)) {
                    defaultLessonRepository.updateTeacherId(`class`.classId, dbDefaultLesson.vpId, teachers.first { t -> t.acronym == defaultLesson.teacherShort }.teacherId)
                }

                val lessonId = UUID.randomUUID()
                insertLessons.add(
                    DbLesson(
                        lessonId = lessonId,
                        roomIsChanged = roomChanged,
                        lessonNumber = lesson.lesson,
                        day = planDate,
                        info = if (DefaultValues.isEmpty(lesson.info)) null else lesson.info,
                        defaultLessonId = defaultLessonDbId,
                        changedSubject = changedSubject,
                        classLessonRefId = `class`.classId,
                        version = version,
                        roomBookingId = bookings.firstOrNull { booking ->
                            booking.from.toLocalDate().isEqual(planDate) && booking.from.toLocalTime().isBefore(
                                times[lesson.lesson]?.end?.toLocalTime()
                            ) && booking.to.toLocalTime().isAfter(
                                times[lesson.lesson]?.start?.toLocalTime()
                            )
                        }?.id
                    )
                )

                roomCrossovers.addAll(
                    dbRooms.map { room ->
                        Pair(lessonId, room.roomId)
                    }
                )

                teacherCrossovers.addAll(
                    dbTeachers.map { teacher ->
                        Pair(lessonId, teacher.teacherId)
                    }
                )
            }
        }

        lessonRepository.insertLessons(insertLessons)
        lessonSchoolEntityCrossoverDao.insertCrossovers(
            roomCrossovers.map { crossover ->
                Pair(crossover.first, crossover.second)
            }.plus(
                teacherCrossovers.map { crossover ->
                    Pair(crossover.first, crossover.second)
                }
            )
        )

        planRepository.createPlan(
            Plan(
                school = school,
                createAt = lastPlanUpdate,
                date = planDate,
                info = vPlanData.wPlanDataObject.info?.joinToString("\n") { it ?: "" },
                version = version
            )
        )
    }
}