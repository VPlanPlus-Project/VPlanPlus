package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class IsInfoExpandedUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() = flow {
        keyValueRepository.getFlowOrDefault(Keys.INFO_CLOSED_FOR_DATE, "none").collect {
            if (it == "none" || LocalDate.parse(it) != LocalDate.now()) emit(true)
            else emit(false)
        }
    }
}