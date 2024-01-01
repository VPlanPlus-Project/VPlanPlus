package es.jvbabi.vplanplus.domain.usecase.onboarding

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.google.gson.Gson
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.Keys
import java.time.LocalDate
import java.util.UUID

class SaveProfileUseCase(
    private val schoolRepository: SchoolRepository,
    private val kv: KeyValueRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val profileRepository: ProfileRepository,
    private val holidayRepository: HolidayRepository,
    private val lessonTimeRepository: LessonTimeRepository,
    private val context: Context
) {

    /**
     * Saves the profile to the database
     * @param schoolId the school id
     * @param username the username
     * @param password the password
     * @param defaultLessonsEnabled Map where key is vpId and value is whether the default lesson is enabled
     * */
    suspend operator fun invoke(
        schoolId: Long,
        username: String,
        password: String,
        referenceName: String,
        type: ProfileType,
        defaultLessonsEnabled: Map<Long, Boolean> = emptyMap()
    ) {
        var school = schoolRepository.getSchoolFromId(schoolId)

        val defaultLessons = Gson().fromJson(kv.get("onboarding.school.$schoolId.defaultLessons")?:"[]", Array<DefaultLesson>::class.java).toList()

        if (school == null) { // school not in database
            val classes = kv.get("onboarding.school.$schoolId.classes")?.split(",") ?: emptyList()
            val teachers = kv.get("onboarding.school.$schoolId.teachers")?.split(",") ?: emptyList()
            val rooms = kv.get("onboarding.school.$schoolId.rooms")?.split(",") ?: emptyList()
            val holidays = kv.get("onboarding.school.$schoolId.holidays")?.split(",")?.map { LocalDate.parse(it) } ?: emptyList()
            val lessonTimes = Gson().fromJson(kv.get("onboarding.school.$schoolId.lessonTimes")?:"[]", Array<LessonTime>::class.java).toList()

            schoolRepository.createSchool(
                schoolId = schoolId,
                username = username,
                password = password,
                name = kv.get("onboarding.school.$schoolId.name") ?: "No name",
                daysPerWeek = kv.get("onboarding.school.$schoolId.daysPerWeek")?.toIntOrNull() ?: 5,
                fullyCompatible = rooms.isNotEmpty() && teachers.isNotEmpty()
            )

            // insert classes, teachers and rooms
            classes.forEach {
                classRepository.createClass(
                    schoolId = schoolId,
                    className = it
                )
            }
            teachers.forEach {
                teacherRepository.createTeacher(
                    schoolId = schoolId,
                    acronym = it
                )
            }

            school = schoolRepository.getSchoolFromId(schoolId)!!
            rooms.forEach {
                roomRepository.createRoom(
                    Room(
                        school = school,
                        name = it
                    )
                )
            }

            holidays.forEach {
                holidayRepository.insertHoliday(
                    holiday = Holiday(
                        schoolHolidayRefId = schoolId,
                        date = it
                    )
                )
            }

            lessonTimes.forEach {
                lessonTimeRepository.insertLessonTime(
                    es.jvbabi.vplanplus.domain.model.LessonTime(
                        classLessonTimeRefId = classRepository.getClassBySchoolIdAndClassName(schoolId, it.className, false)!!.classId,
                        lessonNumber = it.lessonNumber,
                        start = it.startTime,
                        end = it.endTime,
                    )
                )
            }
        }

        defaultLessons.forEach {
            defaultLessonRepository.insert(
                DbDefaultLesson(
                    defaultLessonId = UUID.randomUUID(),
                    vpId = it.vpId,
                    subject = it.subject,
                    teacherId = teacherRepository.find(school, it.teacher, false)?.teacherId,
                    classId = classRepository.getClassBySchoolIdAndClassName(schoolId, it.className, false)!!.classId
                )
            )
        }

        val referenceId: UUID
        val name: String
        when (type) {
            ProfileType.TEACHER -> {
                val teacher = teacherRepository.find(
                    school = school,
                    acronym = referenceName,
                    false
                )!!
                referenceId = teacher.teacherId
                name = teacher.acronym
            }
            ProfileType.STUDENT -> {
                val `class` = classRepository.getClassBySchoolIdAndClassName(
                    schoolId = schoolId,
                    className = referenceName,
                    false
                )!!
                referenceId = `class`.classId
                name = `class`.name
            }
            ProfileType.ROOM -> {
                val room = roomRepository.getRoomByName(
                    school = school,
                    name = referenceName,
                    false
                )!!
                referenceId = room.roomId
                name = room.name
            }
        }

        val profileId = profileRepository.createProfile(
            name = referenceName,
            type = type,
            referenceId = referenceId,
            customName = name
        )

        defaultLessonsEnabled.forEach {
            if (it.value) {
                profileRepository.enableDefaultLesson(
                    profileId, it.key
                )
            } else {
                profileRepository.disableDefaultLesson(
                    profileId, it.key
                )
            }
        }

        kv.set(Keys.ACTIVE_PROFILE, profileId.toString())
        kv.delete("onboarding.school.$schoolId.name")
        kv.delete("onboarding.school.$schoolId.daysPerWeek")
        kv.delete("onboarding.school.$schoolId.classes")
        kv.delete("onboarding.school.$schoolId.teachers")
        kv.delete("onboarding.school.$schoolId.rooms")
        kv.delete("onboarding.school.$schoolId.defaultLessons")
        kv.delete("onboarding.school.$schoolId.holidays")
        kv.delete("onboarding.school.$schoolId.lessonTimes")

        // notification channel

        val channelName = "Profil $referenceName"
        val descriptionText = "Benachrichtigungen für neue Pläne"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            "PROFILE_$referenceName",
            channelName,
            importance
        ).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}