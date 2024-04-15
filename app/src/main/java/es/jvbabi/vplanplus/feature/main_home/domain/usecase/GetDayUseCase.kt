package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class GetDayUseCase(
    private val planRepository: PlanRepository,
    private val getCurrentDataVersionUseCase: GetCurrentDataVersionUseCase,
) {
    operator fun invoke(date: LocalDate, profile: Profile)= flow {
        getCurrentDataVersionUseCase().collect { version ->
            emit(planRepository.getDayForProfile(profile, date, version).first())
        }
    }
}