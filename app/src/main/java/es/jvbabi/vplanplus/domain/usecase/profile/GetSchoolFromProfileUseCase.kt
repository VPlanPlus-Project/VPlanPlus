package es.jvbabi.vplanplus.domain.usecase.profile

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository

class GetSchoolFromProfileUseCase(
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
) {
    suspend operator fun invoke(profile: Profile): School {
        return when (profile.type) {
            ProfileType.STUDENT -> classRepository.getClassById(profile.referenceId)!!.school
            ProfileType.TEACHER -> teacherRepository.getTeacherById(profile.referenceId)!!.school
            ProfileType.ROOM -> roomRepository.getRoomById(profile.referenceId)!!.school
        }
    }
}