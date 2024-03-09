package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import io.ktor.http.HttpStatusCode

class ProfileOptionsUseCase(
    private val schoolRepository: SchoolRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val vppIdRepository: VppIdRepository,
    private val kv: KeyValueRepository
) {

    suspend operator fun invoke(schoolId: Long, username: String, password: String, profileType: ProfileType): List<Pair<String, Int>> {
        val school = schoolRepository.getSchoolFromId(schoolId)
        val countMapping: Map<String, Int> = if (profileType == ProfileType.STUDENT) {
            val response = vppIdRepository.fetchUsersPerClass(schoolId, username, password)
            if (response.response != HttpStatusCode.OK || response.data == null) emptyMap()
            else response.data.classes.associate { it.className to it.users }
        } else {
            emptyMap()
        }

        return when (profileType) {
            ProfileType.STUDENT -> {
                if (school == null) kv.get("onboarding.school.$schoolId.classes")?.split(",")?.map { it to countMapping.getOrDefault(it, 0) } ?: emptyList()
                else classRepository.getClassesBySchool(school).map { it.name to countMapping.getOrDefault(it.name, 0) }
            }
            ProfileType.TEACHER -> {
                if (school == null) kv.get("onboarding.school.$schoolId.teachers")?.split(",")?.map { it to 0 } ?: emptyList()
                else teacherRepository.getTeachersBySchoolId(school.schoolId).map { it.acronym to 0 }
            }
            ProfileType.ROOM -> {
                if (school == null) kv.get("onboarding.school.$schoolId.rooms")?.split(",")?.map { it to 0 } ?: emptyList()
                else roomRepository.getRoomsBySchool(school).map { it.name to 0 }
            }
        }
    }
}