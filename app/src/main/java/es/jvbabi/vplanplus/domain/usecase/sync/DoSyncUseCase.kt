package es.jvbabi.vplanplus.domain.usecase.sync

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.source.database.dao.LessonSchoolEntityCrossoverDao
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Plan
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.xml.DefaultValues
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_GRADES
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.usecase.profile.GetSchoolFromProfileUseCase
import es.jvbabi.vplanplus.feature.grades.domain.model.GradeModifier
import es.jvbabi.vplanplus.feature.grades.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.MathTools
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

private const val SYNC_DAYS_PAST = 2

class DoSyncUseCase(
    private val context: Context,
    private val keyValueRepository: KeyValueRepository,
    private val logRecordRepository: LogRecordRepository,
    private val messageRepository: MessageRepository,
    private val schoolRepository: SchoolRepository,
    private val roomRepository: RoomRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val lessonTimesRepository: LessonTimeRepository,
    private val profileRepository: ProfileRepository,
    private val lessonRepository: LessonRepository,
    private val vPlanRepository: VPlanRepository,
    private val lessonSchoolEntityCrossoverDao: LessonSchoolEntityCrossoverDao,
    private val planRepository: PlanRepository,
    private val systemRepository: SystemRepository,
    private val calendarRepository: CalendarRepository,
    private val getSchoolFromProfileUseCase: GetSchoolFromProfileUseCase,
    private val notificationRepository: NotificationRepository,
    private val gradeRepository: GradeRepository
) {
    suspend operator fun invoke(): Boolean {
        if (profileRepository.getProfiles().first().isEmpty()) return true
        val daysAhead = keyValueRepository.get(Keys.SETTINGS_SYNC_DAY_DIFFERENCE)?.toIntOrNull()
            ?: Keys.SETTINGS_SYNC_DAY_DIFFERENCE_DEFAULT
        logRecordRepository.log("Sync", "Syncing $daysAhead days ahead")

        val currentVersion =
            keyValueRepository.get(Keys.LESSON_VERSION_NUMBER)?.toLongOrNull() ?: 0L

        logRecordRepository.log("Sync.Messages", "Syncing messages for all app users")
        messageRepository.updateMessages(null)

        logRecordRepository.log("Sync.Grades", "Syncing grades")
        val newGrades = gradeRepository.updateGrades()

        val pendingIntent = Intent(context, MainActivity::class.java)
            .putExtra("screen", "grades")
            .let { intent ->
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        if (newGrades.isNotEmpty()) {
            val msg = if (newGrades.size == 1) {
                context.getString(
                    R.string.notification_newGradeText,
                    newGrades.first().value.roundToInt()
                        .toString() + when (newGrades.first().modifier) {
                        GradeModifier.MINUS -> "-"
                        GradeModifier.PLUS -> "+"
                        else -> ""
                    },
                    newGrades.first().subject.name
                )
            } else {
                context.getString(R.string.notification_newGradesText, newGrades.size)
            }
            notificationRepository.sendNotification(
                CHANNEL_ID_GRADES,
                564,
                context.getString(R.string.notification_newGradesTitle),
                msg,
                R.drawable.vpp,
                pendingIntent,
            )
        }

        val profileDataBefore = hashMapOf<Profile, List<Lesson>>()
        val notifications = mutableListOf<NotificationData>()

        schoolRepository.getSchools().forEach { school ->
            logRecordRepository.log("Sync.School", "Syncing school ${school.name}")
            logRecordRepository.log("Sync.Messages", "Syncing messages for school ${school.name}")
            messageRepository.updateMessages(school.schoolId)

            logRecordRepository.log(
                "Sync.RoomBookings",
                "Syncing room bookings for school ${school.name}"
            )
            roomRepository.fetchRoomBookings(school)

            repeat(daysAhead + SYNC_DAYS_PAST) {
                val date = LocalDate.now().plusDays(it - SYNC_DAYS_PAST.toLong())
                logRecordRepository.log("Sync.Day", "Syncing day $date")

                val profiles = profileRepository.getProfilesBySchoolId(school.schoolId)
                profiles.forEach { profile ->
                    profileDataBefore[profile] =
                        lessonRepository.getLessonsForProfile(profile.id, date, currentVersion)
                            .first()
                            ?.filter { l -> profile.isDefaultLessonEnabled(l.vpId) }
                            ?.toList() ?: emptyList()
                }

                val data = vPlanRepository.getVPlanData(school, date)
                if (!listOf(HttpStatusCode.OK, HttpStatusCode.NotFound).contains(data.response)) {
                    logRecordRepository.log("Sync.VPlan", "Failed to sync VPlan for $date")
                    return false
                }

                if (data.response == HttpStatusCode.NotFound) {
                    logRecordRepository.log(
                        "SyncWorker",
                        "No data available for ${school.schoolId} (${school.name} at $date)"
                    )
                    return@repeat
                }

                processVPlanData(data.data ?: return@forEach)
                profiles.forEach profile@{ profile ->
                    // check if plan has changed
                    val day =
                        planRepository.getDayForProfile(profile, date, currentVersion + 1).first()
                    val importantLessons = day.lessons
                        .filter { l -> profile.isDefaultLessonEnabled(l.vpId) }
                    val changedLessons = importantLessons.filter { l ->
                        !profileDataBefore[profile]!!.map { prevData -> prevData.toHash() }
                            .contains(l.toHash())
                    }
                    if (changedLessons.isEmpty()) return@profile // no changes, continue with next profile
                    val type =
                        if (profileDataBefore[profile]!!.isEmpty()) SyncNotificationType.NEW_PLAN else SyncNotificationType.CHANGED_LESSONS
                    if (canSendNotification() && !date.isBefore(LocalDate.now())) notifications.add(
                        NotificationData(profile, changedLessons, day.info, date, type)
                    )

                    calendarRepository.processLessons(profile, day)
                }
            }
        }

        keyValueRepository.set(
            Keys.LESSON_VERSION_NUMBER,
            (currentVersion + 1).toString()
        )
        keyValueRepository.set(Keys.LAST_SYNC_TS, (System.currentTimeMillis() / 1000).toString())
        lessonRepository.deleteLessonsByVersion(currentVersion)
        planRepository.deletePlansByVersion(currentVersion)

        notifications.forEach { notificationData ->
            sendNewPlanNotification(notificationData)
        }

        logRecordRepository.log("SyncWorker", "Synced successfully")
        Log.d("SyncWorker", "Synced successfully")

        return true
    }


    private suspend fun processVPlanData(vPlanData: VPlanData) {

        val planDateFormatter = DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.GERMAN)
        val planDate = LocalDate.parse(vPlanData.wPlanDataObject.head!!.date!!, planDateFormatter)

        val createDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
        val lastPlanUpdate = LocalDateTime.parse(
            vPlanData.wPlanDataObject.head!!.timestampString!!,
            createDateFormatter
        )

        val school = schoolRepository.getSchoolFromId(vPlanData.schoolId)!!

        val version = keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "-2").toLong() + 1

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

                val dbDefaultLesson =
                    defaultLessons.firstOrNull { dl -> dl.vpId == defaultLesson?.lessonId?.toLong() }
                var defaultLessonDbId = dbDefaultLesson?.defaultLessonId

                val rawTeacherAcronyms =
                    if (DefaultValues.isEmpty(lesson.teacher.teacher)) emptyList() else {
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
                val addTeachers = rawTeacherAcronyms.filter { t ->
                    !teachers.map { dbT -> dbT.acronym }.contains(t)
                }
                val addRooms =
                    lessonRooms.filter { r -> !rooms.map { dbR -> dbR.name }.contains(r) }

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

                if (addTeachers.isNotEmpty()) teachers =
                    teacherRepository.getTeachersBySchoolId(school.schoolId)
                if (addRooms.isNotEmpty()) rooms = roomRepository.getRoomsBySchool(school)

                //Log.d("VPlanUseCases", "Processing lesson ${lesson.lesson} for class ${`class`.className}")
                val dbRooms = rooms.filter { r -> lessonRooms.contains(r.name) }
                val roomChanged = lesson.room.roomChanged == "RaGeaendert"

                val dbTeachers = teachers.filter { t -> rawTeacherAcronyms.contains(t.acronym) }

                var changedSubject =
                    if (lesson.subject.subjectChanged == "FaGeaendert") lesson.subject.subject else null
                if (listOf(
                        "&nbsp;",
                        "&amp;nbsp;",
                        "---",
                        ""
                    ).contains(changedSubject)
                ) changedSubject =
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
                } else if (addTeachers.isNotEmpty() && dbDefaultLesson != null && defaultLesson?.teacherShort != null && dbDefaultLesson.teacher == null && addTeachers.contains(
                        defaultLesson.teacherShort
                    )
                ) {
                    defaultLessonRepository.updateTeacherId(
                        `class`.classId,
                        dbDefaultLesson.vpId,
                        teachers.first { t -> t.acronym == defaultLesson.teacherShort }.teacherId
                    )
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
                            booking.from.toLocalDate()
                                .isEqual(planDate) && booking.from.toLocalTime().isBefore(
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

    private suspend fun canSendNotification() =
        (!systemRepository.isAppInForeground() || keyValueRepository.get(
            Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE
        ) == "true")

    private suspend fun sendNewPlanNotification(notificationData: NotificationData) {

        val intent = Intent(context, MainActivity::class.java)
            .putExtra("profileId", notificationData.profile.id.toString())
            .putExtra("dateStr", notificationData.date.toString())

        Log.d(
            "SyncWorker.Notification",
            "Sending ${notificationData.notificationType} for ${notificationData.profile.displayName} at ${
                notificationData.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }"
        )
        Log.d(
            "SyncWorker.Notification",
            "Cantor: " + MathTools.cantor(
                notificationData.profile.id.hashCode(),
                "${notificationData.date.dayOfMonth}${notificationData.date.monthValue}".toInt()
            )
        )

        val pendingIntent = PendingIntent.getActivity(
            context,
            MathTools.cantor(
                notificationData.profile.id.hashCode(),
                notificationData.date.toString().replace("-", "").toInt()
            ),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val school = getSchoolFromProfileUseCase(notificationData.profile)

        val message = when (notificationData.notificationType) {
            SyncNotificationType.CHANGED_LESSONS -> context.getString(
                R.string.notification_planChangedText,
                notificationData.profile.displayName,
                school.name,
                DateUtils.localizedRelativeDate(context, notificationData.date)
            ) +
                    buildInfoNotificationString(notificationData.info) +
                    buildChangedNotificationString(notificationData.changedLessons)

            SyncNotificationType.NEW_PLAN -> context.getString(
                R.string.notification_newPlanText,
                notificationData.profile.displayName,
                school.name,
                DateUtils.localizedRelativeDate(context, notificationData.date)
            ) +
                    buildInfoNotificationString(notificationData.info) +
                    buildChangedNotificationString(notificationData.changedLessons)
        }

        notificationRepository.sendNotification(
            "PROFILE_${notificationData.profile.id.toString().lowercase()}",
            MathTools.cantor(
                notificationData.profile.id.hashCode(),
                notificationData.date.toString().replace("-", "").toInt()
            ),
            context.getString(
                when (notificationData.notificationType) {
                    SyncNotificationType.NEW_PLAN -> R.string.notification_newPlanTitle
                    SyncNotificationType.CHANGED_LESSONS -> R.string.notification_planChangedTitle
                }
            ),
            message,
            R.drawable.vpp,
            pendingIntent
        )
    }

    private fun buildChangedNotificationString(changedLessons: List<Lesson>): String {
        if (changedLessons.isEmpty()) return ""
        var changedString = "\n" + context.getString(R.string.notification_changedLessons) + "\n"
        changedLessons.forEach { lesson ->
            changedString += "${lesson.lessonNumber}: ${
                if (lesson.displaySubject == "-") "-" else
                    context.getString(
                        R.string.notification_lesson,
                        lesson.displaySubject,
                        lesson.teachers.joinToString(", "),
                        lesson.rooms.joinToString(", ")
                    )
            } \n"
        }
        return changedString
    }

    private fun buildInfoNotificationString(info: String?): String {
        return if (info != null) "\n$info" else ""
    }
}

data class NotificationData(
    val profile: Profile,
    val changedLessons: List<Lesson>,
    val info: String?,
    val date: LocalDate,
    val notificationType: SyncNotificationType
)

enum class SyncNotificationType {
    NEW_PLAN, CHANGED_LESSONS
}
