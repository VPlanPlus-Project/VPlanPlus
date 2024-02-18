package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import android.app.NotificationManager
import com.google.gson.Gson
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.toLocalDateTime
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
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository
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
        defaultLessonsEnabled: Map<Long, Boolean> = emptyMap(),
        onStatusUpdate: (ProfileCreationStatus) -> Unit
    ) {
        var school = schoolRepository.getSchoolFromId(schoolId)

        val defaultLessons = Gson().fromJson(
            kv.get("onboarding.school.$schoolId.defaultLessons") ?: "[]",
            Array<DefaultLesson>::class.java
        ).toList()

        if (school == null) { // school not in database
            val classes =
                kv.get("onboarding.school.$schoolId.classes")?.split(",")?.filter { it != "" }
                    ?: emptyList()
            val teachers =
                kv.get("onboarding.school.$schoolId.teachers")?.split(",")?.filter { it != "" }
                    ?: emptyList()
            val rooms = kv.get("onboarding.school.$schoolId.rooms")?.split(",")?.filter { it != "" }
                ?: emptyList()
            val holidays =
                kv.get("onboarding.school.$schoolId.holidays")?.split(",")?.filter { it != "" }
                    ?.map { LocalDate.parse(it) } ?: emptyList()
            val lessonTimes = Gson().fromJson(
                kv.get("onboarding.school.$schoolId.lessonTimes") ?: "[]",
                Array<LessonTime>::class.java
            ).toList()

            val total =
                classes.size + teachers.size + rooms.size + defaultLessons.size + holidays.size
            var progress = 0.0

            schoolRepository.createSchool(
                schoolId = schoolId,
                username = username,
                password = password,
                name = kv.get("onboarding.school.$schoolId.name") ?: "No name",
                daysPerWeek = kv.get("onboarding.school.$schoolId.daysPerWeek")?.toIntOrNull() ?: 5,
                fullyCompatible = rooms.isNotEmpty() && teachers.isNotEmpty()
            )

            // insert classes, teachers and rooms
            classRepository.insertClasses(
                schoolId = schoolId,
                classes = classes
            )
            progress += classes.size
            onStatusUpdate(ProfileCreationStatus(ProfileCreationStage.INSERT_CLASSES, progress / total))

            teacherRepository.insertTeachersByAcronym(
                schoolId = schoolId,
                teachers = teachers
            )
            progress += teachers.size
            onStatusUpdate(ProfileCreationStatus(ProfileCreationStage.INSERT_TEACHERS, progress / total))

            roomRepository.insertRoomsByName(
                schoolId = schoolId,
                rooms = rooms
            )
            progress += rooms.size
            onStatusUpdate(ProfileCreationStatus(ProfileCreationStage.INSERT_ROOMS, progress / total))

            holidayRepository.replaceHolidays(
                holidays = holidays.map {
                    Holiday(
                        schoolHolidayRefId = schoolId,
                        date = it
                    )
                }
            )
            progress += holidays.size
            onStatusUpdate(ProfileCreationStatus(ProfileCreationStage.INSERT_HOLIDAYS, progress / total))

            school = schoolRepository.getSchoolFromId(schoolId)!!
            lessonTimeRepository.insertLessonTimes(
                lessonTimes = lessonTimes.map {
                    es.jvbabi.vplanplus.domain.model.LessonTime(
                        classLessonTimeRefId = classRepository.getClassBySchoolIdAndClassName(
                            schoolId,
                            it.className,
                            false
                        )!!.classId,
                        lessonNumber = it.lessonNumber,
                        start = "${it.startTime}:00".toLocalDateTime().atBeginningOfTheWorld(),
                        end = "${it.endTime}:00".toLocalDateTime().atBeginningOfTheWorld(),
                    )
                }
            )
            progress += lessonTimes.size
            onStatusUpdate(ProfileCreationStatus(ProfileCreationStage.INITIAL_SYNC, progress / total))
        }

        defaultLessons.forEach {
            defaultLessonRepository.insert(
                DbDefaultLesson(
                    defaultLessonId = UUID.randomUUID(),
                    vpId = it.vpId,
                    subject = it.subject,
                    teacherId = teacherRepository.find(school, it.teacher, false)?.teacherId,
                    classId = classRepository.getClassBySchoolIdAndClassName(
                        schoolId,
                        it.className,
                        false
                    )!!.classId
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

        notificationRepository.createChannel(
            "PROFILE_$profileId",
            stringRepository.getString(R.string.notification_profileName, referenceName),
            stringRepository.getString(R.string.notification_profileDescription),
            NotificationManager.IMPORTANCE_DEFAULT
        )
    }
}

data class ProfileCreationStatus(
    val profileCreationStage: ProfileCreationStage,
    val progress: Double?,
)

enum class ProfileCreationStage {
    NONE,
    INSERT_TEACHERS,
    INSERT_CLASSES,
    INSERT_ROOMS,
    INSERT_HOLIDAYS,
    INITIAL_SYNC,
}