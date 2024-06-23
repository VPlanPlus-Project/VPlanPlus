package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.flow

class GetHolidaysUseCase(
    private val holidayRepository: HolidayRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    operator fun invoke() = flow {
        getCurrentProfileUseCase().collect { profile ->
            if (profile?.getSchool() == null) {
                emit(emptyList())
                return@collect
            }
            emit(holidayRepository.getHolidaysBySchoolId(profile.getSchool().id).map { h -> h.date })
        }
    }
}