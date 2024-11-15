package es.jvbabi.vplanplus.domain.usecase.sync

import android.content.Context
import android.util.Log
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.LessonSchoolEntityCrossoverDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Plan
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.xml.DefaultValues
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.BaseDataResponse
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.NewTimetableLesson
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_SYSTEM
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_SYSTEM_NOTIFICATION_ID
import es.jvbabi.vplanplus.domain.repository.OpenScreenTask
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.repository.WeekRepository
import es.jvbabi.vplanplus.domain.usecase.calendar.UpdateCalendarUseCase
import es.jvbabi.vplanplus.feature.exams.domain.usecase.UpdateAssessmentsUseCase
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.main_grades.common.domain.usecases.UpdateGradesUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.UpdateHomeworkUseCase
import es.jvbabi.vplanplus.ui.NotificationDestination
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.atStartOfDay
import es.jvbabi.vplanplus.util.DateUtils.withDayOfWeek
import es.jvbabi.vplanplus.util.MathTools
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

private const val SYNC_DAYS_PAST = 2

class DoSyncUseCase(
    private val context: Context,
    private val keyValueRepository: KeyValueRepository,
    private val logRecordRepository: LogRecordRepository,
    private val messageRepository: MessageRepository,
    private val schoolRepository: SchoolRepository,
    private val roomRepository: RoomRepository,
    private val groupRepository: GroupRepository,
    private val teacherRepository: TeacherRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val lessonTimesRepository: LessonTimeRepository,
    private val profileRepository: ProfileRepository,
    private val lessonRepository: LessonRepository,
    private val vPlanRepository: VPlanRepository,
    private val lessonSchoolEntityCrossoverDao: LessonSchoolEntityCrossoverDao,
    private val planRepository: PlanRepository,
    private val systemRepository: SystemRepository,
    private val holidayRepository: HolidayRepository,
    private val baseDataRepository: BaseDataRepository,
    private val notificationRepository: NotificationRepository,
    private val timetableRepository: TimetableRepository,
    private val weekRepository: WeekRepository,
    private val updateCalendarUseCase: UpdateCalendarUseCase,
    private val updateHomeworkUseCase: UpdateHomeworkUseCase,
    private val updateGradesUseCase: UpdateGradesUseCase,
    private val updateAssessmentsUseCase: UpdateAssessmentsUseCase,
) {
    suspend operator fun invoke(): Boolean {

        if (profileRepository.getProfiles().first().isEmpty()) return true
        val daysAhead = keyValueRepository.get(Keys.SETTINGS_SYNC_DAY_DIFFERENCE)?.toIntOrNull()
            ?: Keys.SETTINGS_SYNC_DAY_DIFFERENCE_DEFAULT

        var currentVersion =
            keyValueRepository.get(Keys.LESSON_VERSION_NUMBER)?.toLongOrNull() ?: -1L

        logRecordRepository.log("Sync.Homework", "Syncing homework")
        updateHomeworkUseCase(currentVersion != -1L)
        updateAssessmentsUseCase()

        logRecordRepository.log("Sync", "Syncing $daysAhead days ahead")

        planRepository.deletePlansByVersion(currentVersion + 1)
        lessonRepository.deleteLessonsByVersion(currentVersion + 1)

        logRecordRepository.log("Sync.Messages", "Syncing messages for all app users")
        messageRepository.updateMessages(null)

        logRecordRepository.log("Sync.Grades", "Syncing grades")

        updateGradesUseCase()

        val profileDataBefore = hashMapOf<Profile, List<Lesson>>()
        val notifications = mutableListOf<NotificationData>()

        schoolRepository.getSchools().filter { it.credentialsValid != false }.forEach school@{ school ->
            val times = mutableListOf<Pair<String, Long>>()
            times.add("Start" to System.currentTimeMillis())
            logRecordRepository.log("Sync.School", "Syncing school ${school.name}")
            logRecordRepository.log("Sync.Messages", "Syncing messages for school ${school.name}")
            messageRepository.updateMessages(school.id)
            times.add("Messages" to System.currentTimeMillis())

            logRecordRepository.log(
                "Sync.RoomBookings",
                "Syncing room bookings for school ${school.name}"
            )
            roomRepository.fetchRoomBookings(school)
            times.add("RoomBookings" to System.currentTimeMillis())

            val baseDataResponse = baseDataRepository.getBaseData(school.sp24SchoolId, school.username, school.password)
            val baseData = (baseDataResponse as? BaseDataResponse.Success)?.baseData
            times.add("BaseData Download" to System.currentTimeMillis())
            if (baseData != null) {
                val holidays = holidayRepository.getHolidaysBySchoolId(school.id).map { it.date }
                val holidaysToRemove = holidays.filter { it in baseData.holidays }
                if (holidaysToRemove.isNotEmpty()) holidayRepository.deleteHolidaysBySchoolId(school.id)

                val newHolidays = baseData.holidays.filter { it !in holidays || holidaysToRemove.isNotEmpty() }
                newHolidays.forEach { holidayRepository.insertHoliday(school.id, it) }
            }

            if (school.canUseTimetable != false) {

                logRecordRepository.log(
                    "Sync.Timetable",
                    "Syncing timetable for school ${school.name}"
                )
                val timetable = vPlanRepository.getSPlanData(
                    sp24SchoolId = school.sp24SchoolId,
                    username = school.username,
                    password = school.password
                )
                times.add("Timetable Download" to System.currentTimeMillis())

                val teachers = teacherRepository.getTeachersBySchoolId(school.id)
                val rooms = roomRepository.getRoomsBySchool(school)
                val groups = groupRepository.getGroupsBySchool(school)

                //timetableRepository.clearTimetableForSchool(school)
                timetable.data?.sPlan?.schoolWeekTypes?.forEach forEachWeekType@{ type ->
                    if (type.type.isEmpty()) return@forEachWeekType
                    weekRepository.insertWeekType(school, type.type)
                }
                val weekTypes = weekRepository.getWeekTypesBySchool(school)
                timetable.data?.sPlan?.weeks?.forEach { week ->
                    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    weekRepository.insertWeek(
                        school = school,
                        startDate = LocalDate.parse(week.dateFrom, formatter),
                        endDate = LocalDate.parse(week.dateTo, formatter),
                        weekType = weekTypes.first { it.name == week.type },
                        weekNumber = week.weekNumber
                    )
                }
                val newTimetableLessons = timetable.data?.sPlan?.classes.orEmpty().map { group ->
                    groupRepository.getGroupBySchoolAndName(school.id, group.schoolClass)?.let { groupObj ->
                        val lessonTimes = lessonTimesRepository.getLessonTimesByGroup(groupObj)
                        if (lessonTimes.isNotEmpty()) return@let
                        lessonTimesRepository.deleteLessonTimes(groupObj)
                        group.lessonTimes.orEmpty().forEach forEachLessonTime@{ lessonTime ->
                            if (lessonTime.lessonNumber == null || lessonTime.start == null || lessonTime.end == null || !(lessonTime.start?:"a").matches(Regex("\\d+:\\d+")) || !(lessonTime.end?:"a").matches(Regex("\\d+:\\d+"))) return@forEachLessonTime
                            lessonTimesRepository.insertLessonTime(
                                groupId = groupObj.groupId,
                                lessonNumber = lessonTime.lessonNumber!!,
                                from = lessonTime.start!!.substringBefore(":").toInt() * 60 * 60 + lessonTime.start!!.substringAfter(":").toInt() * 60L,
                                to = lessonTime.end!!.substringBefore(":").toInt() * 60 * 60 + lessonTime.end!!.substringAfter(":").toInt() * 60L,
                            )
                        }
                    }
                    group.lessons.orEmpty().mapNotNull forEachLesson@{ lesson ->
                        NewTimetableLesson(
                            group = groups.firstOrNull { it.name == group.schoolClass } ?: return@forEachLesson null,
                            subject = lesson.subjectShort,
                            lessonNumber = lesson.lessonNumber,
                            teachers = teachers.filter { it.acronym in lesson.teacherShort.split(",") },
                            rooms = rooms.filter { it.name == lesson.roomShort },
                            dayOfWeek = DayOfWeek.of(lesson.dayOfWeek),
                            weekType = lesson.weekType?.let { weekTypes.firstOrNull { wt -> wt.name == it } },
                            week = null
                        )
                    }
                }.flatten()
                timetableRepository.insertTimetableLessons(newTimetableLessons)
                times.add("Timetable Insert" to System.currentTimeMillis())
            }

            var weeks = weekRepository.getWeeksBySchool(school)
            if (weeks.isEmpty()) run handleWeeks@{
                val week1Response = vPlanRepository.getSPlanDataViaWPlan6(
                    school.buildAccess(),
                    weekNumber = 1
                )
                times.add("Weeks Download" to System.currentTimeMillis())
                if (week1Response.data == null) return@handleWeeks
                week1Response.data.schoolWeeks.orEmpty().map { it.weekType }.distinct().forEach {
                    weekRepository.insertWeekType(school, it)
                }

                val weekTypes = weekRepository.getWeekTypesBySchool(school)
                week1Response.data.schoolWeeks.orEmpty().forEach { week ->
                    weekRepository.insertWeek(
                        school = school,
                        startDate = LocalDate.parse(week.dateFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        endDate = LocalDate.parse(week.dateTo, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        weekType = weekTypes.first { it.name == week.weekType },
                        weekNumber = week.weekNumber
                    )
                }
                weeks = weekRepository.getWeeksBySchool(school)
            }
            weeks = weekRepository.getWeeksBySchool(school)
            val weekTypes = weekRepository.getWeekTypesBySchool(school)

            // refresh splan
            val sPlanResponse = vPlanRepository.getSPlanDataViaWPlan6(
                school.buildAccess(),
                weekNumber = weeks.firstOrNull { LocalDate.now() in it.start..it.end.withDayOfWeek(6) }?.weekNumber ?: 1,
                allowFallback = true
            )
            times.add("SPlan Download" to System.currentTimeMillis())
            if (sPlanResponse.data != null) {
                val teachers = teacherRepository.getTeachersBySchoolId(school.id)
                val rooms = roomRepository.getRoomsBySchool(school)
                val groups = groupRepository.getGroupsBySchool(school)
                val timetable = timetableRepository.getWeekTimetableForSchool(school, null)
                val newLessons = mutableListOf<NewTimetableLesson>()
                val removeLessons = timetable.toMutableSet()
                sPlanResponse.data.classes.orEmpty().forEach { group ->
                    groups.firstOrNull { it.name == group.schoolClass }?.let { groupObj ->
                        val lessonTimes = lessonTimesRepository.getLessonTimesByGroup(groupObj)
                        if (lessonTimes.isNotEmpty()) return@let
                        lessonTimesRepository.deleteLessonTimes(groupObj)
                        group.lessonTimes.orEmpty().forEach forEachLessonTime@{ lessonTime ->
                            if (lessonTime.lessonNumber == null || lessonTime.start == null || lessonTime.end == null || !(lessonTime.start?:"a").matches(Regex("\\d+:\\d+")) || !(lessonTime.end?:"a").matches(Regex("\\d+:\\d+"))) return@forEachLessonTime
                            lessonTimesRepository.insertLessonTime(
                                groupId = groupObj.groupId,
                                lessonNumber = lessonTime.lessonNumber!!,
                                from = lessonTime.start!!.substringBefore(":").toInt() * 60 * 60 + lessonTime.start!!.substringAfter(":").toInt() * 60L,
                                to = lessonTime.end!!.substringBefore(":").toInt() * 60 * 60 + lessonTime.end!!.substringAfter(":").toInt() * 60L,
                            )
                        }
                    }
                    group.lessons?.forEach forEachLesson@{ lesson ->
                        val lessonRooms = getRoomsFromRawData(lesson.roomShort, rooms)
                        val existingLessons = timetable.filter { l ->
                            l.lessonNumber == lesson.lessonNumber &&
                                    l.group.name == group.schoolClass &&
                                    l.dayOfWeek.value == lesson.dayOfWeek &&
                                    l.weekType?.name == lesson.weekType &&
                                    l.subject == lesson.subjectShort &&
                                    l.teachers.map { it.acronym }.containsAll(lesson.teacherShort.split(",")) &&
                                    l.rooms.map { it.name }.containsAll(lessonRooms)
                        }.toSet()
                        if (existingLessons.count() != 1) {
                            newLessons.add(
                                NewTimetableLesson(
                                    group = groups.firstOrNull { it.name == group.schoolClass } ?: groupRepository.getGroupBySchoolAndName(school.id, group.schoolClass) ?: run {
                                        Log.e("Sync.Timetable", "Group ${group.schoolClass} not found")
                                        return@forEachLesson
                                    },
                                    weekType = weekTypes.firstOrNull { it.name == lesson.weekType },
                                    lessonNumber = lesson.lessonNumber,
                                    teachers = teachers.filter { it.acronym in lesson.teacherShort.split(",") },
                                    subject = lesson.subjectShort,
                                    rooms = rooms.filter { it.name in lessonRooms },
                                    dayOfWeek = DayOfWeek.of(lesson.dayOfWeek),
                                    week = null
                                )
                            )
                        } else {
                            removeLessons.removeAll(existingLessons)
                        }
                    }
                }
                Log.d("Sync.Timetable", "Inserting ${newLessons.size} new lessons")
                Log.d("Sync.Timetable", "Removing ${removeLessons.size} old lessons")
                timetableRepository.deleteFromTimetableById(removeLessons.map { it.id })
                timetableRepository.insertTimetableLessons(newLessons)
                times.add("SPlan Insert" to System.currentTimeMillis())
            }

            repeat(daysAhead + SYNC_DAYS_PAST) {
                val date = LocalDate.now().plusDays(it - SYNC_DAYS_PAST.toLong())
                logRecordRepository.log("Sync.Day", "Syncing day $date")

                val profiles = profileRepository.getProfilesBySchool(school.id).first()
                profiles.forEach { profile ->
                    profileDataBefore[profile] =
                        lessonRepository.getLessonsForProfile(profile, date, currentVersion)
                            .first()
                            ?.filter { l -> l is Lesson.TimetableLesson || l is Lesson.SubstitutionPlanLesson && (profile as? ClassProfile)?.isDefaultLessonEnabled(l.defaultLesson?.vpId) ?: true }
                            ?.toList() ?: emptyList()
                }

                val vPlanData = vPlanRepository.getVPlanData(
                    sp24SchoolId = school.sp24SchoolId,
                    username = school.username,
                    password = school.password,
                    date = date,
                    preferredDownloadMode = school.schoolDownloadMode
                )
                times.add("VPlan.$date Download" to System.currentTimeMillis())
                if (vPlanData.response == HttpStatusCode.Unauthorized) {
                    Log.d("Sync.VPlan", "Unauthorized")
                    schoolRepository.updateCredentialsValid(school, false)
                    val notificationId = CHANNEL_SYSTEM_NOTIFICATION_ID + 100 + school.id
                    notificationRepository.sendNotification(
                        channelId = CHANNEL_ID_SYSTEM,
                        id = notificationId,
                        title = context.getString(R.string.notification_syncErrorCredentialsIncorrectTitle),
                        message = context.getString(R.string.notification_syncErrorCredentialsIncorrectText, school.username, school.sp24SchoolId, school.name),
                        icon = R.drawable.vpp,
                        onClickTask = OpenScreenTask("${Screen.SettingsProfileScreen.route}?task=update_credentials&schoolId=${school.id}")
                    )
                    return@school
                }

                if (!listOf(HttpStatusCode.OK, HttpStatusCode.NotFound).contains(vPlanData.response)) {
                    logRecordRepository.log("Sync.VPlan", "Failed to sync VPlan for $date")
                    return false
                }

                if (vPlanData.response == HttpStatusCode.NotFound) {
                    logRecordRepository.log(
                        "SyncWorker",
                        "No data available for ${school.id} (${school.name} at $date)"
                    )
                    return@repeat
                }

                processVPlanData(vPlanData.data ?: return@school)
                times.add("VPlan.$date Insert" to System.currentTimeMillis())
                profiles.forEach profile@{ profile ->
                    // check if plan has changed
                    val day =
                        planRepository.getDayForProfile(profile, date, currentVersion + 1).first()
                    val importantLessons = day.lessons
                        .filter { l -> l is Lesson.TimetableLesson || l is Lesson.SubstitutionPlanLesson && (profile as? ClassProfile)?.isDefaultLessonEnabled(l.defaultLesson?.vpId) ?: true }
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

                }
            }

            times.forEachIndexed { i, (name, time) ->
                if (i == 0) return@forEachIndexed
                logRecordRepository.log("Sync.Times", "$name took ${time - times[i - 1].second}ms")
                Log.d("Sync.Times", "$name took ${time - times[i - 1].second}ms")
            }
        }

        currentVersion += 1
        keyValueRepository.set(
            Keys.LESSON_VERSION_NUMBER,
            currentVersion.toString()
        )
        keyValueRepository.set(
            Keys.LAST_SYNC_TS, ZonedDateTimeConverter().zonedDateTimeToTimestamp(
                ZonedDateTime.now()
            ).toString()
        )
        lessonRepository.deleteLessonsByVersion(currentVersion - 1)
        planRepository.deletePlansByVersion(currentVersion - 1)

        updateCalendarUseCase()

        notifications.forEach { notificationData ->
            sendNewPlanNotification(notificationData)
        }

        logRecordRepository.log("SyncWorker", "Synced successfully")
        Log.d("SyncWorker", "Synced successfully")

        return true
    }


    private suspend fun processVPlanData(vPlanData: VPlanData) {

        val planDateFormatter = DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.GERMAN)
        val planDate = ZonedDateTime.of(
            LocalDate
                .parse(vPlanData.wPlanDataObject.head!!.date!!, planDateFormatter)
                .atTime(0, 0, 0),
            ZoneId.of("UTC")
        )

        val createDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
        val lastPlanUpdate = vPlanData.wPlanDataObject.head!!.timestampString?.let { ZonedDateTime.of(
            LocalDateTime.parse(
                it,
                createDateFormatter
            ), ZoneId.of("Europe/Berlin")
        ) } ?: planDate.atStartOfDay()

        val school = schoolRepository.getSchoolBySp24Id(vPlanData.sp24SchoolId)!!

        val currentVersion =
            keyValueRepository.get(Keys.LESSON_VERSION_NUMBER)?.toLongOrNull() ?: -1L

        // lists to collect data for bulk insert
        val insertLessons = mutableListOf<DbLesson>()
        val roomCrossovers = mutableListOf<Pair<UUID, Int>>()
        val teacherCrossovers = mutableListOf<Pair<UUID, UUID>>()

        // get rooms and teachers
        var rooms = roomRepository.getRoomsBySchool(school)
        var teachers = teacherRepository.getTeachersBySchoolId(school.id)

        // update stuff
        groupRepository.getGroupsBySchool(school).forEach { dlClass ->
            defaultLessonRepository
                .getDefaultLessonByGroupId(dlClass.groupId)
                .groupBy { it.vpId }
                .map { it.value.dropLast(1).map { item -> item.defaultLessonId } }
                .flatten()
                .forEach { id ->
                    defaultLessonRepository.deleteDefaultLesson(id)
                }
        }

        vPlanData.wPlanDataObject.classes!!.forEach forEachClass@{

            val `class` = groupRepository.getGroupBySchoolAndName(
                school.id,
                it.schoolClass
            ).run {
                if (this != null) return@run this
                if (!groupRepository.insertGroup(school.buildAccess(), null, it.schoolClass, true)) return@run null
                groupRepository.getGroupBySchoolAndName(
                    school.id,
                    it.schoolClass
                )!!
            } ?: return@forEachClass
            val defaultLessons = defaultLessonRepository.getDefaultLessonByGroupId(`class`.groupId)
            val bookings = roomRepository.getRoomBookingsByClass(`class`)
            val times = lessonTimesRepository.getLessonTimesByGroup(`class`)

            // set lessons
            it.lessons!!.forEach lesson@{ lesson ->
                val defaultLesson =
                    it.defaultLessons!!.find { defaultLesson -> defaultLesson.defaultLesson!!.lessonId!! == lesson.defaultLessonVpId }?.defaultLesson

                val dbDefaultLesson =
                    defaultLessons.firstOrNull { dl -> dl.vpId == defaultLesson?.lessonId }
                var defaultLessonDbId = dbDefaultLesson?.defaultLessonId

                val rawTeacherAcronyms =
                    if (DefaultValues.isEmpty(lesson.teacher.teacher)) emptyList() else {
                        if (lesson.teacher.teacher.replace(" ", ",").contains(",")) {
                            lesson.teacher.teacher.replace(" ", ",").split(",")
                        } else listOfNotNull(lesson.teacher.teacher)
                    }

                val rawRoomNames = if (DefaultValues.isEmpty(lesson.room.room)) null
                else lesson.room.room

                val lessonRooms = getRoomsFromRawData(rawRoomNames, rooms)

                // add teachers and rooms to db if they don't exist
                val addTeachers = rawTeacherAcronyms.filter { t ->
                    !teachers.map { dbT -> dbT.acronym }.contains(t)
                }
                val addRooms =
                    lessonRooms.filter { r -> !rooms.map { dbR -> dbR.name }.contains(r) }

                addTeachers.forEach { teacher ->
                    teacherRepository.createTeacher(
                        schoolId = school.id,
                        acronym = teacher
                    )
                }

                if (addRooms.isNotEmpty()) roomRepository.insertRoomsByName(school, addRooms)

                if (addTeachers.isNotEmpty()) teachers =
                    teacherRepository.getTeachersBySchoolId(school.id)
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
                            id = UUID.randomUUID(),
                            vpId = defaultLesson.lessonId!!,
                            subject = defaultLesson.subjectShort!!,
                            teacherId = dbTeachers.firstOrNull { t -> t.acronym == defaultLesson.teacherShort }?.teacherId,
                            classId = `class`.groupId,
                            defaultLesson.courseGroup
                        )
                    )
                } else if (addTeachers.isNotEmpty() && dbDefaultLesson != null && defaultLesson?.teacherShort != null && dbDefaultLesson.teacher == null && addTeachers.contains(
                        defaultLesson.teacherShort
                    )
                ) {
                    defaultLessonRepository.updateTeacherId(
                        `class`.groupId,
                        dbDefaultLesson.vpId,
                        teachers.first { t -> t.acronym == defaultLesson.teacherShort }.teacherId
                    )
                }

                val lessonId = UUID.randomUUID()
                insertLessons.add(
                    DbLesson(
                        id = lessonId,
                        isRoomChanged = roomChanged,
                        lessonNumber = lesson.lesson,
                        day = planDate,
                        info = if (DefaultValues.isEmpty(lesson.info)) null else lesson.info,
                        defaultLessonId = defaultLessonDbId,
                        changedSubject = changedSubject,
                        groupId = `class`.groupId,
                        version = currentVersion + 1,
                        roomBookingId = bookings.firstOrNull { booking ->
                            booking.from
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

            // clean up default lessons 2
            val planDefaultLessons = it
                .defaultLessons
                ?.mapNotNull { dl -> dl.defaultLesson?.lessonId } ?: emptyList()
            defaultLessons
                .filter { dl -> !planDefaultLessons.contains(dl.vpId) }
                .forEach { dl ->
                    defaultLessonRepository.deleteDefaultLesson(dl.defaultLessonId)
                }
        }

        lessonRepository.insertLessons(insertLessons)
        lessonSchoolEntityCrossoverDao.insertRoomCrossovers(
            roomCrossovers.map { crossover ->
                Pair(crossover.first, crossover.second)
            }
        )
        lessonSchoolEntityCrossoverDao.insertTeacherCrossovers(
            teacherCrossovers.map { crossover ->
                Pair(crossover.first, crossover.second)
            }
        )

        planRepository.createPlan(
            Plan(
                school = school,
                createAt = lastPlanUpdate,
                date = planDate,
                info = vPlanData.wPlanDataObject.info?.joinToString("\n") { it ?: "" },
                version = currentVersion + 1
            )
        )
    }

    /**
     * This algorithm tries to find existing rooms within the raw room string. It splits the string by spaces and tries to find a room with the joined string.
     * An example would be "TH 1 TH 2" where it's not clear where to split.
     * Time for another angry checkpoint: While teachers are separated by commas, rooms are separated by spaces. But sometimes, there are spaces in room names.
     */
    private fun getRoomsFromRawData(
        rawRoomNames: String?,
        rooms: List<Room>,
    ): List<String> {
        val result = mutableListOf<String>()
        if (rawRoomNames != null) {
            if (rooms.map { r -> r.name }.contains(rawRoomNames)) {
                result.add(rawRoomNames)
            } else {
                val split = rawRoomNames.split(" ")
                var join = 0
                var start = 0
                for (a in 0..split.size) {
                    val joined = split.subList(start, join).joinToString(" ")
                    if (rooms.map { r -> r.name }.contains(joined)) {
                        result.add(joined)
                        start = join
                    }
                    join += 1
                }
                if (start == 0) result.add(rawRoomNames)
            }
        }
        return result
    }

    private suspend fun canSendNotification() =
        (!systemRepository.isAppInForeground() || keyValueRepository.get(
            Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE
        ) == "true")

    private suspend fun sendNewPlanNotification(notificationData: NotificationData) {

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

        val message = when (notificationData.notificationType) {
            SyncNotificationType.CHANGED_LESSONS -> context.getString(
                R.string.notification_planChangedText,
                notificationData.profile.displayName,
                notificationData.profile.getSchool().name,
                DateUtils.localizedRelativeDate(context, notificationData.date)
            ) +
                    buildInfoNotificationString(notificationData.info) +
                    buildChangedNotificationString(notificationData.changedLessons)

            SyncNotificationType.NEW_PLAN -> context.getString(
                R.string.notification_newPlanText,
                notificationData.profile.displayName,
                notificationData.profile.getSchool().name,
                DateUtils.localizedRelativeDate(context, notificationData.date)
            ) +
                    buildInfoNotificationString(notificationData.info) +
                    buildChangedNotificationString(notificationData.changedLessons)
        }

        notificationRepository.sendNotification(
            channelId = "PROFILE_${notificationData.profile.id.toString().lowercase()}",
            id = MathTools.cantor(
                notificationData.profile.id.hashCode(),
                notificationData.date.toString().replace("-", "").toInt()
            ),
            title = context.getString(
                when (notificationData.notificationType) {
                    SyncNotificationType.NEW_PLAN -> R.string.notification_newPlanTitle
                    SyncNotificationType.CHANGED_LESSONS -> R.string.notification_planChangedTitle
                }
            ),
            message = message,
            icon = R.drawable.vpp,
            onClickTask = OpenScreenTask(destination = Json.encodeToString(
                NotificationDestination(
                    screen = "calendar",
                    profileId = notificationData.profile.id.toString(),
                    payload = Json.encodeToString(Screen.CalendarScreen(notificationData.date))
                )
            ))
        )
    }

    private fun buildChangedNotificationString(changedLessons: List<Lesson>): String {
        if (changedLessons.isEmpty()) return ""
        var changedString = "\n"
        changedLessons.forEach { lesson ->
            changedString += "${lesson.lessonNumber}: ${
                if (lesson.displaySubject == "-") "-" else
                    context.getString(
                        R.string.notification_lesson,
                        lesson.displaySubject,
                        lesson.teachers.joinToString(", ") { it.acronym },
                        lesson.rooms.joinToString(", ") { it.name }
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
