package es.jvbabi.vplanplus.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.CalendarEvent
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.LogRecordRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.LessonUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import es.jvbabi.vplanplus.util.App.isAppInForeground
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.toLocalUnixTimestamp
import es.jvbabi.vplanplus.util.MathTools
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class SyncWorker @AssistedInject constructor(
    private val context: Context,
    params: WorkerParameters,
    @Assisted private val profileUseCases: ProfileUseCases,
    @Assisted private val schoolUseCases: SchoolUseCases,
    @Assisted private val vPlanUseCases: VPlanUseCases,
    @Assisted private val keyValueUseCases: KeyValueUseCases,
    @Assisted private val lessonUseCases: LessonUseCases,
    @Assisted private val classUseCases: ClassUseCases,
    @Assisted private val teacherRepository: TeacherRepository,
    @Assisted private val roomRepository: RoomRepository,
    @Assisted private val logRecordRepository: LogRecordRepository,
    @Assisted private val calendarRepository: CalendarRepository,
    @Assisted private val planRepository: PlanRepository
) : CoroutineWorker(context, params) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            1,
            NotificationCompat.Builder(context, "SYNC")
                .setContentTitle("VPlanPlus")
                .setContentText("Synchronisiere...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        )
    }

    override suspend fun doWork(): Result {
        if (profileUseCases.getProfiles().first().isEmpty()) return Result.success()

        Log.d("SyncWorker", "SYNCING")
        logRecordRepository.log("SyncWorker", "Syncing")
        val syncDays = (keyValueUseCases.get(Keys.SETTINGS_SYNC_DAY_DIFFERENCE) ?: "3").toInt()
        val profileDataBefore = hashMapOf<Profile, List<Lesson>>()
        schoolUseCases.getSchools().forEach school@{ school ->
            repeat(syncDays + 2) { i ->
                val profiles = profileUseCases.getProfilesBySchoolId(school.schoolId)
                val date = LocalDate.now().plusDays(i - 2L)
                val currentVersion = keyValueUseCases.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0")
                    .toLong()

                // set hash before sync to evaluate later if any changes were made
                val start = System.currentTimeMillis()
                profiles.forEach { profile ->
                    profileDataBefore[profile] = getLessonsByProfile(profile, date, currentVersion).lessons.filter { profile.isDefaultLessonEnabled(it.vpId) }
                }
                val network = System.currentTimeMillis()
                Log.d("SyncWorker.Timer", "1. Before Data: ${network - start}ms")

                // get today's data
                val data = vPlanUseCases.getVPlanData(school, date)
                Log.d("SyncWorker", "Syncing ${school.schoolId} (${school.name}) at $date: ${data.response}")
                val end = System.currentTimeMillis()
                Log.d("SyncWorker.Timer", "2. Network Req: ${end - network}ms")

                // if any errors occur, return failure
                if (!listOf(Response.SUCCESS, Response.NO_DATA_AVAILABLE).contains(data.response)) {
                    logRecordRepository.log("SyncWorker", "Error while syncing ${school.schoolId} (${school.name}): ${data.response}")
                    return Result.failure()
                }

                // if no data is available, skip to next day
                if (data.response == Response.NO_DATA_AVAILABLE) {
                    logRecordRepository.log(
                        "SyncWorker",
                        "No data available for ${school.schoolId} (${school.name} at $date)"
                    )
                    return@repeat
                }

                // update database
                vPlanUseCases.processVPlanData(data.data!!)
                val afterProcessing = System.currentTimeMillis()
                Log.d("SyncWorker.Timer", "3. Processing: ${afterProcessing - end}ms")

                profiles.forEach profile@{ profile ->

                    // check if plan has changed
                    val day = getLessonsByProfile(profile, date, currentVersion + 1)
                    val changedLessons = day.lessons
                        .filter { profile.isDefaultLessonEnabled(it.vpId) }
                        .filter { l -> !profileDataBefore[profile]!!.map { it.toHash() }.contains(l.toHash()) }
                    val type = if (profileDataBefore[profile]!!.isEmpty()) NotificationType.NEW_PLAN else NotificationType.CHANGED_LESSONS

                    if (changedLessons.isEmpty()) return@profile
                    if (canSendNotification() && !date.isBefore(LocalDate.now())) sendNewPlanNotification(profile, changedLessons, day.info, date, type)

                    // build calendar
                    val calendar = profileUseCases.getCalendarFromProfile(profile)
                    if (calendar != null) {

                        calendarRepository.deleteCalendarEvents(school = school, date = date)
                        val lessons = getLessonsByProfile(profile, date, currentVersion + 1)
                        Log.d("SyncWorker.Calendar", "Calendar: $calendar $date ${lessons.type} ${lessons.lessons.isEmpty()}")
                        if (lessons.state == DayDataState.NO_DATA || lessons.lessons.isEmpty()) return@profile
                        Log.d("SyncWorker.Calendar", "${profile.displayName}: Calendar Type: ${profile.calendarType}")
                        when (profile.calendarType) {
                            ProfileCalendarType.DAY -> {
                                Log.d(
                                    "SyncWorker.Calendar",
                                    "Creating day event for ${profile.displayName} at $date"
                                )
                                Log.d("SyncWorker.Calendar",
                                    " --> ID: " + calendarRepository.insertEvent(
                                        CalendarEvent(
                                            title = "Schultag " + profile.displayName,
                                            calendarId = calendar.id,
                                            location = school.name,
                                            startTimeStamp = lessons.lessons.filter {
                                                profile.isDefaultLessonEnabled(
                                                    it.vpId
                                                )
                                            }.sortedBy { it.lessonNumber }
                                                .first { it.displaySubject != "-" }.start.toLocalUnixTimestamp(),
                                            endTimeStamp = lessons.lessons.filter {
                                                profile.isDefaultLessonEnabled(
                                                    it.vpId
                                                )
                                            }.sortedBy { it.lessonNumber }
                                                .last { it.displaySubject != "-" }.end.toLocalUnixTimestamp(),
                                            date = date,
                                            info = day.info
                                        ),
                                        school = school
                                    )
                                )
                            }

                            ProfileCalendarType.LESSON -> {
                                Log.d(
                                    "SyncWorker.Calendar",
                                    "Creating lesson events for ${profile.displayName} at $date"
                                )
                                lessons.lessons.filter { profile.isDefaultLessonEnabled(it.vpId) }
                                    .forEach { lesson ->
                                        if (lesson.displaySubject != "-") {
                                            calendarRepository.insertEvent(
                                                CalendarEvent(
                                                    title = lesson.displaySubject,
                                                    calendarId = calendar.id,
                                                    location = school.name + " Raum " + lesson.rooms.joinToString(", "),
                                                    startTimeStamp = lesson.start.toLocalUnixTimestamp(),
                                                    endTimeStamp = lesson.end.toLocalUnixTimestamp(),
                                                    date = date
                                                ),
                                                school = school
                                            )
                                        }
                                    }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        keyValueUseCases.set(
            Keys.LESSON_VERSION_NUMBER,
            (keyValueUseCases.getOrDefault(Keys.LESSON_VERSION_NUMBER, "-2")
                .toLong() + 1L).toString()
        )
        lessonUseCases.deleteLessonsByVersion(keyValueUseCases.get(Keys.LESSON_VERSION_NUMBER)!!.toLong()-1L)
        planRepository.deletePlansByVersion(keyValueUseCases.get(Keys.LESSON_VERSION_NUMBER)!!.toLong()-1L)
        keyValueUseCases.set(Keys.LAST_SYNC_TS, (System.currentTimeMillis()/1000).toString())
        Log.d("SyncWorker", "SYNCED")
        logRecordRepository.log("SyncWorker", "Synced sucessfully")
        return Result.success()
    }

    private suspend fun sendNewPlanNotification(profile: Profile, changedLessons: List<Lesson>, info: String?, date: LocalDate, notificationType: NotificationType) {
        logRecordRepository.log(
            "SyncWorker",
            "Sending notification for profile ${profile.displayName}"
        )

        val intent = Intent(context, MainActivity::class.java)
            .putExtra("profileId", profile.id.toString())
            .putExtra("dateStr", date.toString())

        Log.d("SyncWorker.Notification", "Sending $notificationType for ${profile.displayName} at ${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
        Log.d("SyncWorker.Notification", "Cantor: " + MathTools.cantor(profile.id.hashCode(), "${date.dayOfMonth}${date.monthValue}".toInt()))

        val pendingIntent = PendingIntent.getActivity(
            context,
            MathTools.cantor(profile.id.hashCode(), date.toString().replace("-", "").toInt()),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val school = profileUseCases.getSchoolFromProfileId(profile.id)

        val message = when (notificationType) {
            NotificationType.CHANGED_LESSONS -> context.getString(
                R.string.notification_planChangedText,
                profile.displayName,
                school.name,
                DateUtils.localizedRelativeDate(context, date)
            ) +
                    buildInfoNotificationString(info) +
                    buildChangedNotificationString(changedLessons)
            NotificationType.NEW_PLAN -> context.getString(
                R.string.notification_newPlanText,
                profile.displayName,
                school.name,
                DateUtils.localizedRelativeDate(context, date)
            ) +
                    buildInfoNotificationString(info) +
                    buildChangedNotificationString(changedLessons)
        }

        val builder = NotificationCompat.Builder(context, "PROFILE_${profile.originalName}")
            .setContentTitle(context.getString(when (notificationType) {
                NotificationType.NEW_PLAN -> R.string.notification_newPlanTitle
                NotificationType.CHANGED_LESSONS -> R.string.notification_planChangedTitle
            }))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(MathTools.cantor(profile.id.hashCode(), date.toString().replace("-", "").toInt()), builder.build())
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


    private suspend fun getLessonsByProfile(profile: Profile, date: LocalDate, version: Long): Day {
        return when (profile.type) {
            ProfileType.STUDENT -> lessonUseCases.getLessonsForClass(
                classUseCases.getClassById(
                    profile.referenceId
                ), date, version
            )

            ProfileType.TEACHER -> lessonUseCases.getLessonsForTeacher(
                teacherRepository.getTeacherById(profile.referenceId)!!,
                date, version
            )

            ProfileType.ROOM -> lessonUseCases.getLessonsForRoom(
                roomRepository.getRoomById(
                    profile.referenceId
                )!!, date, version
            )
        }.first()
    }

    /**
     * Checks if a notification should be sent.
     * @return true if app is in background or if the setting "Show notification if app is visible" is enabled
     */
    private suspend fun canSendNotification() = (!isAppInForeground() || keyValueUseCases.get(
        Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE
    ) == "true")
}

private enum class NotificationType {
    NEW_PLAN, CHANGED_LESSONS
}