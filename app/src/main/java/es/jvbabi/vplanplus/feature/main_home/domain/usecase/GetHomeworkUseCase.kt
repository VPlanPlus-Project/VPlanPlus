package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID

class GetHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val keyValueRepository: KeyValueRepository,
    private val profileRepository: ProfileRepository,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase
) {
    @Suppress("UNCHECKED_CAST")
    operator fun invoke() = flow {
        combine(
            listOf(
                keyValueRepository.getFlow(Keys.ACTIVE_PROFILE),
                homeworkRepository.getAll(),
                getCurrentIdentityUseCase()
            )
        ) { data ->
            val activeProfile = profileRepository.getProfileById(UUID.fromString(data[0] as String)).first()
            val vppId = (data[2] as Identity?)?.profile?.vppId
            (data[1] as List<Homework>).filter { it.classes.classId == activeProfile?.referenceId || it.createdBy == null || it.createdBy == vppId }
        }.collect {
            emit(it)
        }
    }
}