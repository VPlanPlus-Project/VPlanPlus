package es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.model.ProfileType.ROOM
import es.jvbabi.vplanplus.domain.model.ProfileType.STUDENT
import es.jvbabi.vplanplus.domain.model.ProfileType.TEACHER
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.Holiday
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingDefaultLesson
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingInitClass
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.UpdateFcmTokenUseCase
import kotlinx.serialization.json.Json
import java.time.ZoneId
import java.time.ZonedDateTime

val onboardingSetupKeys = listOf(
    "onboarding.school_id",
    "onboarding.sp24_school_id",
    "onboarding.school_name",
    "onboarding.username",
    "onboarding.password",
    "onboarding.days_per_week",
    "onboarding.is_fully_supported",
    "onboarding.is_first_profile",
    "onboarding.teachers",
    "onboarding.rooms",
    "onboarding.classes",
    "onboarding.holidays",
    "onboarding.default_lessons",
    "onboarding.profile_type",
    "onboarding.profile",
    "onboarding.profile_default_lessons",
    "onboarding.download_mode",
    "onboarding.can_use_timetable"
)

class SetupUseCase(
    private val schoolRepository: SchoolRepository,
    private val groupRepository: GroupRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val holidayRepository: HolidayRepository,
    private val lessonTimeRepository: LessonTimeRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val profileRepository: ProfileRepository,
    private val keyValueRepository: KeyValueRepository,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
) {
    suspend operator fun invoke(): Boolean {
        val json = Json { allowStructuredMapKeys = true }

        val schoolId = keyValueRepository.get("onboarding.school_id")!!.toInt()
        val isFirstProfile = keyValueRepository.get("onboarding.is_first_profile")!!.toBoolean()
        val selectedProfileType = ProfileType.entries[keyValueRepository.get("onboarding.profile_type")!!.toInt()]
        val selectedProfileEntityName = keyValueRepository.get("onboarding.profile")!!
        val selectedDefaultLessons = json.decodeFromString<Map<OnboardingDefaultLesson, Boolean>>(keyValueRepository.get("onboarding.profile_default_lessons")?:"[]")

        var school = schoolRepository.getSchoolFromId(schoolId)
        Log.d("SetupUseCase", "School ID: $schoolId, object: $school")
        if (school == null) {
            Log.d("SetupUseCase", "School is null, creating new school")
            val sp24SchoolId = keyValueRepository.get("onboarding.sp24_school_id")!!.toInt()
            val name = keyValueRepository.get("onboarding.school_name")!!
            val username = keyValueRepository.get("onboarding.username")!!
            val password = keyValueRepository.get("onboarding.password")!!
            val daysPerWeek = keyValueRepository.get("onboarding.days_per_week")!!.toInt()
            val fullyCompatible = keyValueRepository.get("onboarding.is_fully_supported")!!.toBoolean()
            val teacherAcronyms = Json.decodeFromString<List<String>>(keyValueRepository.get("onboarding.teachers")!!)
            val roomNames = Json.decodeFromString<List<String>>(keyValueRepository.get("onboarding.rooms")!!)
            val classesData = Json.decodeFromString<List<OnboardingInitClass>>(keyValueRepository.get("onboarding.classes")!!)
            val holidays = Json.decodeFromString<List<Holiday>>(keyValueRepository.get("onboarding.holidays")!!)
            val defaultLessons = Json.decodeFromString<List<OnboardingDefaultLesson>>(keyValueRepository.get("onboarding.default_lessons")!!)
            val downloadMode = SchoolDownloadMode.valueOf(keyValueRepository.get("onboarding.download_mode")!!)
            val canUseTimetable = keyValueRepository.get("onboarding.can_use_timetable")!!.toBoolean()
            schoolRepository.createSchool(
                schoolId = schoolId,
                sp24SchoolId = sp24SchoolId,
                name = name,
                username = username,
                password = password,
                daysPerWeek = daysPerWeek,
                fullyCompatible = fullyCompatible,
                schoolDownloadMode = downloadMode,
                canUseTimetable = canUseTimetable
            )
            school = schoolRepository.getSchoolFromId(schoolId)!!
            Log.d("SetupUseCase", "School created: $school")

            Log.d("SetupUseCase", "Inserting ${teacherAcronyms.size} teachers")
            teacherRepository.insertTeachersByAcronym(schoolId, teacherAcronyms)
            var teachers = teacherRepository.getTeachersBySchoolId(school.id)

            Log.d("SetupUseCase", "Inserting ${classesData.size} classes")
            classesData.forEach { clazz ->
                groupRepository.insertGroup(
                    schoolSp24Access = school.buildAccess(),
                    groupName = clazz.name,
                    groupId = clazz.id,
                    isClass = true
                )
                Log.d("SetupUseCase", " Inserting ${clazz.lessonTimes.size} lesson times for class ${clazz.name}")
                clazz.lessonTimes.forEach {
                    lessonTimeRepository.insertLessonTime(
                        groupId = clazz.id,
                        lessonNumber = it.lessonNumber,
                        from = ZonedDateTime.of(1970, 1, 1, it.startHour, it.startMinute, 0, 0, ZoneId.of("UTC")).toEpochSecond(),
                        to = ZonedDateTime.of(1970, 1, 1, it.endHour, it.endMinute, 0, 0, ZoneId.of("UTC")).toEpochSecond()
                    )
                }
            }
            val classes = groupRepository.getGroupsBySchool(school).filter { it.isClass }

            Log.d("SetupUseCase", "Inserting ${roomNames.size} rooms")
            roomRepository.insertRoomsByName(school, roomNames)

            Log.d("SetupUseCase", "Inserting ${holidays.size} holidays")
            holidayRepository.deleteHolidaysBySchoolId(schoolId)
            holidays.forEach { holidayRepository.insertHoliday(schoolId = schoolId, date = it.date) }

            Log.d("SetupUseCase", "Inserting ${defaultLessons.size} default lessons")
            defaultLessons.forEach forEachDefaultLesson@{ defaultLesson ->
                val group = classes.firstOrNull { defaultLesson.clazz == it.name } ?: run {
                    Log.w("SetupUseCase", "Class '${defaultLesson.clazz}' not found, aborting")
                    return@forEachDefaultLesson
                }
                val teacher = teachers.firstOrNull { defaultLesson.teacher == it.acronym } ?: run {
                    if (defaultLesson.teacher.isNullOrBlank()) {
                        Log.w("SetupUseCase", "Teacher '${defaultLesson.teacher}' not found, aborting")
                        return@forEachDefaultLesson
                    }
                    Log.w("SetupUseCase", "Teacher ${defaultLesson.teacher} not found, creating")
                    teacherRepository.createTeacher(schoolId, defaultLesson.teacher)
                    teachers = teacherRepository.getTeachersBySchoolId(school.id)
                    teachers.first { defaultLesson.teacher == it.acronym }
                }
                defaultLessonRepository.insertDefaultLesson(
                    groupId = group.groupId,
                    subject = defaultLesson.subject,
                    teacherId = teacher.teacherId,
                    vpId = defaultLesson.vpId,
                    courseGroup = defaultLesson.courseGroup
                )
            }
        }

        val classes = groupRepository.getGroupsBySchool(school).filter { it.isClass }
        val teachers = teacherRepository.getTeachersBySchoolId(school.id)
        val rooms = roomRepository.getRoomsBySchool(school)

        Log.d("SetupUseCase", "Creating profile ${selectedProfileType.name}")
        val profileId = when (selectedProfileType) {
            STUDENT -> profileRepository.createClassProfile(group = classes.first { it.name == selectedProfileEntityName }, isHomeworkEnabled = isFirstProfile)
            TEACHER -> profileRepository.createTeacherProfile(teacher = teachers.first { it.acronym == selectedProfileEntityName })
            ROOM -> profileRepository.createRoomProfile(room = rooms.first { it.name == selectedProfileEntityName })
        }

        keyValueRepository.set(Keys.ACTIVE_PROFILE, profileId.toString())

        if (selectedProfileType == STUDENT) {
            Log.d("SetupUseCase", "Setting default lessons for class profile")
            selectedDefaultLessons.forEach {
                profileRepository.setDefaultLessonActivationState(profileId, it.key.vpId, it.value)
            }
        }

        onboardingSetupKeys.forEach { keyValueRepository.delete(it) }
        updateFcmTokenUseCase()
        return true
    }
}