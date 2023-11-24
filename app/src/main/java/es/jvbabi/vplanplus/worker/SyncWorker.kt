package es.jvbabi.vplanplus.worker

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.CalendarEvent
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.LogRecordRepository
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
import es.jvbabi.vplanplus.ui.screens.home.Day
import es.jvbabi.vplanplus.ui.screens.home.DayType
import es.jvbabi.vplanplus.util.DateUtils.toLocalUnixTimestamp
import kotlinx.coroutines.flow.first
import java.time.LocalDate


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
    @Assisted private val calendarRepository: CalendarRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        Log.d("SyncWorker", "SYNCING")
        logRecordRepository.log("SyncWorker", "Syncing")
        val planIsChanged = hashMapOf<Profile, Boolean>()
        val syncDays = (keyValueUseCases.get(Keys.SETTINGS_SYNC_DAY_DIFFERENCE) ?: "3").toInt()
        schoolUseCases.getSchools().forEach school@{ school ->
            repeat(syncDays + 2) { i ->
                val profiles = profileUseCases.getProfilesBySchoolId(school.schoolId)
                val date = LocalDate.now().plusDays(i - 2L)
                val hashesBefore = hashMapOf<Profile, String>()
                val hashesAfter = hashMapOf<Profile, String>()
                val currentVersion = keyValueUseCases.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0")
                    .toLong()

                // set hash before sync to evaluate later if any changes were made
                profiles.forEach { profile ->
                    hashesBefore[profile] = profileUseCases.getPlanSum(profile, date, false, planVersion = currentVersion)
                }

                // get today's data
                val data = vPlanUseCases.getVPlanData(school, date)
                Log.d("SyncWorker","Syncing ${school.schoolId} (${school.name}) at $date: ${data.response}")

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
                vPlanUseCases.processVplanData(data.data!!)

                profiles.forEach profile@{ profile ->
                    // set hash after sync to evaluate later if any changes were made
                    hashesAfter[profile] = profileUseCases.getPlanSum(profile, date, false, currentVersion+1)

                    // check if plan has changed
                    if (hashesBefore[profile] != hashesAfter[profile]) {
                        planIsChanged[profile] = true
                    }

                    // build calendar
                    val calendar = profileUseCases.getCalendarFromProfile(profile)
                    if (calendar != null) {

                        calendarRepository.deleteCalendarEvents(school = school, date = date)
                        val lessons = getLessonsByProfile(profile, date, currentVersion+1)
                        Log.d("SyncWorker.Calendar", "Calendar: $calendar $date ${lessons.dayType} ${lessons.lessons.isEmpty()}")
                        if (lessons.dayType != DayType.DATA || lessons.lessons.isEmpty()) return@profile
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
                                                .last { it.displaySubject != "-" }.start.toLocalUnixTimestamp(),
                                            date = date
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
                                                    location = school.name + " Raum " + lesson.rooms.joinToString(
                                                        ", "
                                                    ),
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
        Log.d("SyncWorker", "Changed profiles: ${planIsChanged.keys.map { it.displayName }}")
        planIsChanged.keys.forEach { profile ->
            // send notification
            if (planIsChanged[profile] == true && (!isAppInForeground() || keyValueUseCases.get(
                    Keys.SETTINGS_NOTIFICATION_SHOW_NOTIFICATION_IF_APP_IS_VISIBLE
                ) == "true")
            ) sendNewPlanNotification(profile)
        }

        keyValueUseCases.set(
            Keys.LESSON_VERSION_NUMBER,
            (keyValueUseCases.getOrDefault(Keys.LESSON_VERSION_NUMBER, "-2")
                .toLong() + 1L).toString()
        )
        Log.d("SyncWorker", "SYNCED")
        logRecordRepository.log("SyncWorker", "Synced sucessfully")
        return Result.success()
    }

    private suspend fun sendNewPlanNotification(profile: Profile) {
        logRecordRepository.log(
            "SyncWorker",
            "Sending notification for profile ${profile.displayName}"
        )

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("profileId", profile.id)

        val pendingIntent = PendingIntent.getActivity(
            context,
            profile.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val school = profileUseCases.getSchoolFromProfileId(profile.id)
        val builder = NotificationCompat.Builder(context, "PROFILE_${profile.originalName}")
            .setContentTitle(context.getString(R.string.notification_newPlanTitle))
            .setContentText(
                context.getString(
                    R.string.notification_newPlanText,
                    profile.displayName,
                    school.name
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(profile.id.toInt(), builder.build())
    }

    private fun isAppInForeground(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE)
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
                ), date, version
            )
        }.first()
    }
}