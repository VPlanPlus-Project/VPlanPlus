package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.util.UUID

class GetCurrentSchoolUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val profileRepository: ProfileRepository,
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(): Flow<School?> = flow{
        keyValueRepository.getFlow(Keys.ACTIVE_PROFILE).distinctUntilChanged().collect {
            if (it == null) emit(null)
            else profileRepository.getProfileById(UUID.fromString(it)).distinctUntilChanged().collect { profile ->
                emit(when (profile?.type) {
                    ProfileType.STUDENT -> classRepository.getClassById(profile.referenceId)?.school
                    ProfileType.TEACHER -> teacherRepository.getTeacherById(profile.referenceId)?.school
                    ProfileType.ROOM -> roomRepository.getRoomById(profile.referenceId)?.school
                    else -> null
                })
            }
        }
    }
}