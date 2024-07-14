package es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ShowBannerUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke(): Flow<Boolean> = flow {
        keyValueRepository.getFlow(Keys.SHOW_BS_BANNER).collect {
            if (it == null) emit(true)
            else emit(it.toBoolean())
        }
    }
}