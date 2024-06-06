package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.shared.data.FakeClassRepository
import es.jvbabi.vplanplus.shared.data.FakeDefaultLessonRepository
import es.jvbabi.vplanplus.shared.data.FakeHolidayRepository
import es.jvbabi.vplanplus.shared.data.FakeKeyValueRepository
import es.jvbabi.vplanplus.shared.data.FakeLessonTimesRepository
import es.jvbabi.vplanplus.shared.data.FakeNotificationRepository
import es.jvbabi.vplanplus.shared.data.FakeProfileRepository
import es.jvbabi.vplanplus.shared.data.FakeRoomRepository
import es.jvbabi.vplanplus.shared.data.FakeSchoolRepository
import es.jvbabi.vplanplus.shared.data.FakeStringRepository
import es.jvbabi.vplanplus.shared.data.FakeTeacherRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.format.DateTimeFormatter
import java.util.UUID

class SaveProfileUseCaseTestPreview {

    private lateinit var saveProfileUseCase: SaveProfileUseCase

    private lateinit var schoolRepository: SchoolRepository
    private lateinit var classRepository: ClassRepository
    private lateinit var teacherRepository: TeacherRepository
    private lateinit var roomRepository: RoomRepository
    private lateinit var defaultLessonRepository: DefaultLessonRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var holidayRepository: HolidayRepository
    private lateinit var lessonTimeRepository: LessonTimeRepository
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var stringRepository: StringRepository
    private lateinit var keyValueRepository: KeyValueRepository

    private lateinit var existingSchool: School
    private lateinit var newSchool: School

    @Before
    fun setUp() {
        schoolRepository = FakeSchoolRepository()
        classRepository = FakeClassRepository(schoolRepository as FakeSchoolRepository)
        teacherRepository = FakeTeacherRepository(schoolRepository as FakeSchoolRepository)
        roomRepository = FakeRoomRepository(schoolRepository as FakeSchoolRepository)
        defaultLessonRepository = FakeDefaultLessonRepository(classRepository, teacherRepository)
        profileRepository = FakeProfileRepository()
        holidayRepository = FakeHolidayRepository(schoolRepository as FakeSchoolRepository)
        lessonTimeRepository = FakeLessonTimesRepository()
        notificationRepository = FakeNotificationRepository()
        keyValueRepository = FakeKeyValueRepository()
        stringRepository = FakeStringRepository(
            mapOf(
                R.string.notification_profileName to "Profile %1\$s",
                R.string.notification_profileDescription to "This is a description"
            )
        )

        newSchool = School(
            schoolId = 42L,
            name = "New School",
            username = "new",
            password = "new",
            daysPerWeek = 5,
            fullyCompatible = true
        )

        runBlocking {
            (schoolRepository as FakeSchoolRepository).createExampleData()
            existingSchool = schoolRepository.getSchools().first()
            keyValueRepository.set(
                "onboarding.school.${existingSchool.schoolId}.defaultLessons",
                """[{"vpId":72,"className":"5a","subject":"DE","teacher":"Do"},{"vpId":73,"className":"5a","subject":"MA","teacher":"Ber"},{"vpId":74,"className":"5a","subject":"GE","teacher":"Jü"},{"vpId":75,"className":"5a","subject":"S","teacher":"Ju"},{"vpId":76,"className":"5a","subject":"MU","teacher":"Nei"},{"vpId":77,"className":"5a","subject":"KU","teacher":"Bau"},{"vpId":78,"className":"5a","subject":"GEO","teacher":"Ju"},{"vpId":79,"className":"5a","subject":"BIO","teacher":"Ze"},{"vpId":80,"className":"5a","subject":"TC","teacher":"Kin"},{"vpId":81,"className":"5a","subject":"ETH","teacher":"Uhl"},{"vpId":82,"className":"5a","subject":"REE","teacher":"Re"},{"vpId":83,"className":"5a","subject":"EN","teacher":"Czi"},{"vpId":162,"className":"5a","subject":"TC","teacher":"Krü"},{"vpId":166,"className":"5a","subject":"S","teacher":"Bl"},{"vpId":462,"className":"5a","subject":"CHO","teacher":"Irr"}]"""
            )

            keyValueRepository.set(
                "onboarding.school.${newSchool.schoolId}.classes",
                FakeClassRepository.classNames.joinToString (",")
            )

            keyValueRepository.set(
                "onboarding.school.${newSchool.schoolId}.teachers",
                FakeTeacherRepository.teacherNames.joinToString (",")
            )

            keyValueRepository.set(
                "onboarding.school.${newSchool.schoolId}.rooms",
                FakeRoomRepository.roomNames.joinToString (",")
            )

            keyValueRepository.set(
                "onboarding.school.${newSchool.schoolId}.holidays",
                FakeHolidayRepository.dates.joinToString (",")
            )

            keyValueRepository.set(
                "onboarding.school.${newSchool.schoolId}.lessonTimes",
                Gson().toJson(FakeClassRepository.classNames.map { className ->
                    FakeLessonTimesRepository.lessonTimesForClass(UUID.randomUUID()).map {
                        LessonTime(
                            className = className,
                            lessonNumber = it.lessonNumber,
                            startTime = it.start.format(DateTimeFormatter.ofPattern("HH:mm")),
                            endTime = it.end.format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                    }
                }.flatten())
            )

            FakeClassRepository.classNames.map {
                classRepository.createClass(existingSchool.schoolId, it)
            }

            FakeTeacherRepository.teacherNames.map {
                teacherRepository.createTeacher(existingSchool.schoolId, it)
            }

            FakeRoomRepository.roomNames.forEach {
                roomRepository.createRoom(Room(name = it, school = existingSchool))
            }
        }

        saveProfileUseCase = SaveProfileUseCase(
            schoolRepository = schoolRepository,
            classRepository = classRepository,
            teacherRepository = teacherRepository,
            roomRepository = roomRepository,
            defaultLessonRepository = defaultLessonRepository,
            profileRepository = profileRepository,
            holidayRepository = holidayRepository,
            lessonTimeRepository = lessonTimeRepository,
            notificationRepository = notificationRepository,
            stringRepository = stringRepository,
            kv = keyValueRepository
        )
    }

    @Test
    fun `Save student profile for existing school`() {
        runBlocking {
            val testClass = classRepository.getClassesBySchool(existingSchool).random()

            saveProfileUseCase(
                schoolId = existingSchool.schoolId,
                username = existingSchool.username,
                password = existingSchool.password,
                referenceName = testClass.name,
                type = ProfileType.STUDENT,
                enableHomework = false,
                onStatusUpdate = {}
            )

            assertThat(keyValueRepository.getOrDefault("onboarding.school.${existingSchool.schoolId}.defaultLessons", "[]")).isEqualTo("[]")
            assertThat(profileRepository.getProfiles().first().size).isEqualTo(1)
            assertThat(profileRepository.getProfiles().first().first().type).isEqualTo(ProfileType.STUDENT)
            assertThat(profileRepository.getProfiles().first().first().originalName).isEqualTo(testClass.name)
            assertThat(profileRepository.getProfiles().first().first().displayName).isEqualTo(testClass.name)
            assertThat(classRepository.getClassById(profileRepository.getProfiles().first().first().referenceId)).isEqualTo(testClass)

            `Is base-data stored correctly`(true, existingSchool)
        }
    }

    @Test
    fun `Save teacher profile for existing school`() {
        runBlocking {
            val testTeacher = teacherRepository.getTeachersBySchoolId(existingSchool.schoolId).random()

            saveProfileUseCase(
                schoolId = existingSchool.schoolId,
                username = existingSchool.username,
                password = existingSchool.password,
                referenceName = testTeacher.acronym,
                type = ProfileType.TEACHER,
                enableHomework = false,
                onStatusUpdate = {}
            )

            assertThat(keyValueRepository.getOrDefault("onboarding.school.${existingSchool.schoolId}.defaultLessons", "[]")).isEqualTo("[]")
            assertThat(profileRepository.getProfiles().first().size).isEqualTo(1)
            assertThat(profileRepository.getProfiles().first().first().type).isEqualTo(ProfileType.TEACHER)
            assertThat(profileRepository.getProfiles().first().first().originalName).isEqualTo(testTeacher.acronym)
            assertThat(profileRepository.getProfiles().first().first().displayName).isEqualTo(testTeacher.acronym)
            assertThat(teacherRepository.getTeacherById(profileRepository.getProfiles().first().first().referenceId)).isEqualTo(testTeacher)

            `Is base-data stored correctly`(true, existingSchool)
        }
    }

    @Test
    fun `Save room profile for existing school`() {
        runBlocking {
            val testRoom = roomRepository.getRoomsBySchool(existingSchool).random()

            saveProfileUseCase(
                schoolId = existingSchool.schoolId,
                username = existingSchool.username,
                password = existingSchool.password,
                referenceName = testRoom.name,
                type = ProfileType.ROOM,
                enableHomework = false,
                onStatusUpdate = {}
            )

            assertThat(keyValueRepository.getOrDefault("onboarding.school.${existingSchool.schoolId}.defaultLessons", "[]")).isEqualTo("[]")
            assertThat(profileRepository.getProfiles().first().size).isEqualTo(1)
            assertThat(profileRepository.getProfiles().first().first().type).isEqualTo(ProfileType.ROOM)
            assertThat(profileRepository.getProfiles().first().first().originalName).isEqualTo(testRoom.name)
            assertThat(profileRepository.getProfiles().first().first().displayName).isEqualTo(testRoom.name)
            assertThat(roomRepository.getRoomById(profileRepository.getProfiles().first().first().referenceId)).isEqualTo(testRoom)

            `Is base-data stored correctly`(true, existingSchool)
        }
    }

    @Test
    fun `Create student profile for new school`() {
        runBlocking {
            val testClass = FakeClassRepository.classNames.random()

            saveProfileUseCase(
                schoolId = newSchool.schoolId,
                username = newSchool.username,
                password = newSchool.password,
                referenceName = testClass,
                type = ProfileType.STUDENT,
                enableHomework = false,
                onStatusUpdate = {}
            )

            newSchool = schoolRepository.getSchools().first { it.schoolId == newSchool.schoolId }

            assertThat(keyValueRepository.getOrDefault("onboarding.school.${newSchool.schoolId}.defaultLessons", "[]")).isEqualTo("[]")
            assertThat(profileRepository.getProfiles().first().size).isEqualTo(1)
            assertThat(profileRepository.getProfiles().first().first().type).isEqualTo(ProfileType.STUDENT)
            assertThat(profileRepository.getProfiles().first().first().originalName).isEqualTo(testClass)
            assertThat(profileRepository.getProfiles().first().first().displayName).isEqualTo(testClass)

            `Is base-data stored correctly`(false, newSchool)

            assertThat(classRepository.getClassById(profileRepository.getProfiles().first().first().referenceId)?.name).isEqualTo(testClass)
        }
    }

    @Test
    fun `Create teacher profile for new school`() {
        runBlocking {
            val testTeacher = FakeTeacherRepository.teacherNames.random()

            saveProfileUseCase(
                schoolId = newSchool.schoolId,
                username = newSchool.username,
                password = newSchool.password,
                referenceName = testTeacher,
                type = ProfileType.TEACHER,
                enableHomework = false,
                onStatusUpdate = {}
            )

            newSchool = schoolRepository.getSchools().first { it.schoolId == newSchool.schoolId }

            assertThat(keyValueRepository.getOrDefault("onboarding.school.${newSchool.schoolId}.defaultLessons", "[]")).isEqualTo("[]")
            assertThat(profileRepository.getProfiles().first().size).isEqualTo(1)
            assertThat(profileRepository.getProfiles().first().first().type).isEqualTo(ProfileType.TEACHER)
            assertThat(profileRepository.getProfiles().first().first().originalName).isEqualTo(testTeacher)
            assertThat(profileRepository.getProfiles().first().first().displayName).isEqualTo(testTeacher)

            `Is base-data stored correctly`(false, newSchool)

            assertThat(teacherRepository.getTeacherById(profileRepository.getProfiles().first().first().referenceId)?.acronym).isEqualTo(testTeacher)
        }
    }

    @Test
    fun `Create room profile for new school`() {
        runBlocking {
            val testRoom = FakeRoomRepository.roomNames.random()

            saveProfileUseCase(
                schoolId = newSchool.schoolId,
                username = newSchool.username,
                password = newSchool.password,
                referenceName = testRoom,
                type = ProfileType.ROOM,
                enableHomework = false,
                onStatusUpdate = {}
            )

            newSchool = schoolRepository.getSchools().first { it.schoolId == newSchool.schoolId }

            assertThat(keyValueRepository.getOrDefault("onboarding.school.${newSchool.schoolId}.defaultLessons", "[]")).isEqualTo("[]")
            assertThat(profileRepository.getProfiles().first().size).isEqualTo(1)
            assertThat(profileRepository.getProfiles().first().first().type).isEqualTo(ProfileType.ROOM)
            assertThat(profileRepository.getProfiles().first().first().originalName).isEqualTo(testRoom)
            assertThat(profileRepository.getProfiles().first().first().displayName).isEqualTo(testRoom)

            `Is base-data stored correctly`(false, newSchool)

            assertThat(roomRepository.getRoomById(profileRepository.getProfiles().first().first().referenceId)?.name).isEqualTo(testRoom)
        }
    }

    private fun `Is base-data stored correctly`(schoolAlreadyExists: Boolean, school: School) {
        runBlocking {
            assertThat(classRepository.getClassesBySchool(school).size).isEqualTo(FakeClassRepository.classNames.size)
            assertThat(teacherRepository.getTeachersBySchoolId(school.schoolId).size).isEqualTo(FakeTeacherRepository.teacherNames.size)
            assertThat(roomRepository.getRoomsBySchool(school).size).isEqualTo(FakeRoomRepository.roomNames.size)

            if (schoolAlreadyExists) return@runBlocking

            assertThat(holidayRepository.getHolidaysBySchoolId(school.schoolId).size).isEqualTo(FakeHolidayRepository.dates.size)
            classRepository.getClassesBySchool(school).forEach { `class` ->
                assertThat(lessonTimeRepository.getLessonTimesByClass(`class`).size).isEqualTo(FakeLessonTimesRepository.lessonTimesForClass(`class`.classId).size)
            }
        }
    }
}