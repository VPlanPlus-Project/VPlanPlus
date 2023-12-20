package es.jvbabi.vplanplus.domain.usecase.onboarding

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository

class ProfileOptionsUseCase(
    private val schoolRepository: SchoolRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val kv: KeyValueRepository
) {

    suspend operator fun invoke(schoolId: Long, profileType: ProfileType): List<String> {
        val school = schoolRepository.getSchoolFromId(schoolId)
        return when (profileType) {
            ProfileType.STUDENT -> {
                if (school == null) kv.get("onboarding.school.$schoolId.classes")?.split(",") ?: emptyList()
                else classRepository.getClassesBySchool(school).map { it.name }
            }
            ProfileType.TEACHER -> {
                if (school == null) kv.get("onboarding.school.$schoolId.teachers")?.split(",") ?: emptyList()
                else teacherRepository.getTeachersBySchoolId(school.schoolId).map { it.acronym }
            }
            ProfileType.ROOM -> {
                if (school == null) kv.get("onboarding.school.$schoolId.rooms")?.split(",") ?: emptyList()
                else roomRepository.getRoomsBySchool(school).map { it.name }
            }
        }
    }
}