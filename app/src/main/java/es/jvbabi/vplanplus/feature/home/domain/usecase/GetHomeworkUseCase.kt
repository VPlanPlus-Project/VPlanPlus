package es.jvbabi.vplanplus.feature.home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.util.UUID

class GetHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val keyValueRepository: KeyValueRepository,
) {
    @Suppress("UNCHECKED_CAST")
    operator fun invoke() = flow {
        combine(
            listOf(
                keyValueRepository.getFlow(Keys.ACTIVE_PROFILE),
                homeworkRepository.getAll()
            )
        ) { data ->
            val activeProfile = UUID.fromString(data[0] as String)
            (data[1] as List<Homework>).filter { it.classes.classId == activeProfile || it.createdBy == null }
        }.collect {
            emit(it)
        }
    }
}