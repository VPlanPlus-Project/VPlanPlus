package es.jvbabi.vplanplus.ui.common.dialog.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.GetDayUseCase

data class SelectDateUseCases(
    val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    val getDayUseCase: GetDayUseCase
)
