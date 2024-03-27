package es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.util.UUID

class GetHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val keyValueRepository: KeyValueRepository,
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
            val activeProfile = UUID.fromString(data[0] as String)
            val vppId = (data[2] as Identity?)?.vppId
            (data[1] as List<Homework>).filter { it.classes.classId == activeProfile || it.createdBy == null || it.createdBy == vppId }
        }.collect {
            emit(it)
        }
    }
}