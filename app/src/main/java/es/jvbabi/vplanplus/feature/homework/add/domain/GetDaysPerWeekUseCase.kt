package es.jvbabi.vplanplus.feature.homework.add.domain

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import kotlinx.coroutines.flow.first

class GetDaysPerWeekUseCase(
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase
) {
    suspend operator fun invoke(): Int {
        val school = getCurrentIdentityUseCase().first()?.school ?: return 5
        return school.daysPerWeek
    }
}