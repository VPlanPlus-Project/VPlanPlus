package es.jvbabi.vplanplus.feature.main_home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import kotlinx.coroutines.flow.flow

class GetHolidaysUseCase(
    private val holidayRepository: HolidayRepository,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase
) {
    operator fun invoke() = flow {
        getCurrentIdentityUseCase().collect { identity ->
            if (identity?.school == null) {
                emit(emptyList())
                return@collect
            }
            emit(holidayRepository.getHolidaysBySchoolId(identity.school.schoolId).map { h -> h.date })
        }
    }
}