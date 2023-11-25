package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.security.MessageDigest
import java.time.LocalDate

class ProfileUseCases(
    private val profileRepository: ProfileRepository,
    private val classRepository: ClassRepository,
    private val keyValueRepository: KeyValueRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val lessonRepository: LessonRepository,
    private val calendarRepository: CalendarRepository
) {

    suspend fun deleteDefaultLessonsFromProfile(profileId: Long) {
        profileRepository.deleteDefaultLessonsFromProfile(profileId = profileId)
    }

    suspend fun createStudentProfile(classId: Long, name: String): Long {
        return profileRepository.createProfile(
            referenceId = classId,
            type = ProfileType.STUDENT,
            name = name,
            customName = name
        )
    }

    suspend fun createTeacherProfile(teacherId: Long, name: String) {
        profileRepository.createProfile(
            referenceId = teacherId,
            type = ProfileType.TEACHER,
            name = name,
            customName = name
        )
    }

    suspend fun setCalendarType(profileId: Long, calendarType: ProfileCalendarType) {
        profileRepository.updateProfile(
            profileRepository.getDbProfileById(profileId = profileId)!!
                .copy(calendarMode = calendarType)
        )
    }

    suspend fun setCalendarId(profileId: Long, calendarId: Long) {
        profileRepository.updateProfile(
            profileRepository.getDbProfileById(profileId = profileId)!!.copy(calendarId = calendarId)
        )
    }

    suspend fun setDisplayName(profileId: Long, displayName: String) {
        profileRepository.updateProfile(
            profileRepository.getDbProfileById(profileId = profileId)!!.copy(customName = displayName)
        )
    }

    suspend fun enableDefaultLesson(profileId: Long, vpId: Long) {
        profileRepository.enableDefaultLesson(vpId = vpId, profileId = profileId)
    }

    suspend fun disableDefaultLesson(profileId: Long, vpId: Long) {
        profileRepository.disableDefaultLesson(profileId = profileId, vpId = vpId)
    }

    suspend fun createRoomProfile(roomId: Long, name: String) {
        profileRepository.createProfile(
            referenceId = roomId,
            type = ProfileType.ROOM,
            name = name,
            customName = name
        )
    }

    suspend fun getProfileByTeacherId(teacherId: Long): Profile {
        return profileRepository.getProfileByReferenceId(
            referenceId = teacherId,
            type = ProfileType.TEACHER
        )
    }

    suspend fun getProfileByRoomId(roomId: Long): Profile {
        return profileRepository.getProfileByReferenceId(
            referenceId = roomId,
            type = ProfileType.ROOM
        )
    }

    suspend fun getActiveProfileFlow(): Flow<Profile?> = flow {
        keyValueRepository.getFlow(key = Keys.ACTIVE_PROFILE).collect {
            if (it == null) {
                emit(null)
                return@collect
            }
            profileRepository.getProfileById(it.toLong()).collect { p ->
                emit(p)
            }
        }
    }

    fun getLessonsForProfile(profile: Profile, date: LocalDate, version: Long? = null) = flow {
        when (profile.type) {
            ProfileType.STUDENT -> {
                val `class` = classRepository.getClassById(id = profile.referenceId)
                lessonRepository.getLessonsForClass(`class`.classId, date, version)
            }
            ProfileType.TEACHER -> {
                val teacher = teacherRepository.getTeacherById(id = profile.referenceId)
                lessonRepository.getLessonsForTeacher(teacher!!.teacherId, date, version)
            }
            ProfileType.ROOM -> {
                val room = roomRepository.getRoomById(profile.referenceId)
                lessonRepository.getLessonsForRoom(room.roomId, date, version)
            }
        }.collect {
            emit(it)
        }
    }

    suspend fun getActiveProfile(): Profile? {
        val activeProfileId = keyValueRepository.get(key = Keys.ACTIVE_PROFILE) ?: return null
        return profileRepository.getProfileById(id = activeProfileId.toLong()).first()
    }

    fun getProfiles(): Flow<List<Profile>> {
        return profileRepository.getProfiles()
    }

    suspend fun deleteProfile(profileId: Long) {
        profileRepository.deleteProfile(profileId = profileId)
    }

    suspend fun getProfilesBySchoolId(schoolId: Long): List<Profile> {
        return profileRepository.getProfilesBySchoolId(schoolId = schoolId)
    }

    suspend fun getSchoolFromProfileId(profileId: Long): School {
        val profile = profileRepository.getProfileById(id = profileId).first()
        return when (profile!!.type) {
            ProfileType.STUDENT -> classRepository.getClassById(id = profile.referenceId).school
            ProfileType.TEACHER -> teacherRepository.getTeacherById(id = profile.referenceId)!!.school
            ProfileType.ROOM -> roomRepository.getRoomById(profile.referenceId).school
        }
    }

    /**
     * Create checksum of plan for given date
     * @param date date of plan
     */
    suspend fun getPlanSum(
        profile: Profile,
        date: LocalDate,
        includeHiddenLessons: Boolean = true,
        planVersion: Long? = null
    ): String {
        val plan = when (profile.type) {
            ProfileType.STUDENT -> {
                val `class` = classRepository.getClassById(id = profile.referenceId)
                lessonRepository.getLessonsForClassDirect(
                    classId = `class`.classId,
                    date = date,
                    version = planVersion
                        ?: keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong()
                )
            }

            ProfileType.TEACHER -> {
                val teacher = teacherRepository.getTeacherById(id = profile.referenceId)
                lessonRepository.getLessonsForTeacherDirect(
                    teacherId = teacher!!.teacherId,
                    date = date,
                    version = planVersion
                        ?: keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong()
                )
            }

            ProfileType.ROOM -> {
                val room = roomRepository.getRoomById(profile.referenceId)
                lessonRepository.getLessonsForRoomDirect(
                    roomId = room.roomId, date = date,
                    version = planVersion
                        ?: keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong()
                )
            }
        }

        return MessageDigest.getInstance("SHA-256")
            .digest(plan.second.filter { includeHiddenLessons || profile.isDefaultLessonEnabled(it.vpId) }
                .joinToString { lesson ->
                    lesson.rooms.joinToString { it } + lesson.originalSubject + (lesson.changedSubject
                        ?: "") + lesson.teachers.joinToString { it }
                }.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    fun getProfileById(profileId: Long): Flow<Profile?> {
        return profileRepository.getProfileById(id = profileId)
    }

    suspend fun getCalendarFromProfile(profile: Profile): Calendar? {
        if (profile.calendarId == null) return null
        return calendarRepository.getCalendarById(id = profile.calendarId)
    }
}