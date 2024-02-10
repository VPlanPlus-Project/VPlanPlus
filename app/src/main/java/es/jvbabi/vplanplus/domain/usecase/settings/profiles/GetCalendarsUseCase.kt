package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.repository.CalendarRepository

class GetCalendarsUseCase(
    private val calendarRepository: CalendarRepository
) {
    operator fun invoke() = calendarRepository.getCalendars()
}