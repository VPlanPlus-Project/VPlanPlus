package es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase

import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingDefaultLesson
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.domain.usecase.OnboardingInitClass
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PrepareNewProfileForSchoolUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val schoolRepository: SchoolRepository,
    private val groupRepository: GroupRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val vppIdRepository: VppIdRepository,
    private val defaultLessonRepository: DefaultLessonRepository
) {
    suspend operator fun invoke(schoolId: Int) {
        val school = schoolRepository.getSchoolFromId(schoolId) ?: return
        keyValueRepository.set("onboarding.school_id", schoolId.toString())
        keyValueRepository.set("onboarding.is_first_profile", false.toString())

        val groups = groupRepository.getGroupsBySchool(school)
        val groupInfo = vppIdRepository.fetchUsersPerClass(school.buildAccess()).orEmpty()
        keyValueRepository.set(
            "onboarding.classes",
            Json.encodeToString(groups.map { OnboardingInitClass(
                id = it.groupId,
                name = it.name,
                lessonTimes = emptyList(),
                users = groupInfo.find { gi -> gi.groupId == it.groupId }?.users ?: 0
            ) })
        ).toString()

        val teachers = teacherRepository.getTeachersBySchoolId(school.id).map { it.acronym }
        val rooms = roomRepository.getRoomsBySchool(school).map { it.name }
        keyValueRepository.set("onboarding.teachers", Json.encodeToString(teachers)).toString()
        keyValueRepository.set("onboarding.rooms", Json.encodeToString(rooms)).toString()

        val defaultLessons = defaultLessonRepository.getDefaultLessonsBySchool(school)
        keyValueRepository.set("onboarding.default_lessons", Json.encodeToString(defaultLessons.map {
            OnboardingDefaultLesson(
                clazz = it.`class`.name,
                teacher = it.teacher?.acronym,
                vpId = it.vpId,
                subject = it.subject
            )
        })).toString()
    }
}