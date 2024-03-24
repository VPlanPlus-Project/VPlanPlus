package es.jvbabi.vplanplus.feature.main_timetable.domain.usecase

import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Profile
import java.time.LocalDate

data class TimetableUseCases(
    val getDataUseCase: GetDataUseCase
)

data class TimetableData(
    val profile: Profile? = null,
    val version: Long = 0L,
    val days: Map<LocalDate, Day> = emptyMap(),
)