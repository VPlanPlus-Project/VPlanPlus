package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class GetProfilesUseCase(
    private val profileRepository: ProfileRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
) {
    operator fun invoke() = flow {
        profileRepository.getProfiles().distinctUntilChanged().collect { profiles ->
            val profilesBySchool = mutableMapOf<School, List<Profile>>()
            profiles.forEach { profile ->
                val school = when (profile.type) {
                    ProfileType.STUDENT -> classRepository.getClassById(profile.referenceId)!!.school
                    ProfileType.TEACHER -> teacherRepository.getTeacherById(profile.referenceId)!!.school
                    ProfileType.ROOM -> roomRepository.getRoomById(profile.referenceId)!!.school
                }
                if (profilesBySchool.containsKey(school)) {
                    profilesBySchool[school] = profilesBySchool[school]!!.plus(profile)
                } else {
                    profilesBySchool[school] = listOf(profile)
                }
            }
            emit(profilesBySchool)
        }
    }
}