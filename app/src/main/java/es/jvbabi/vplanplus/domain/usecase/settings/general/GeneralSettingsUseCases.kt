package es.jvbabi.vplanplus.domain.usecase.settings.general

data class GeneralSettingsUseCases(
    val getColorsUseCase: GetColorsUseCase,
    val getSettingsUseCase: GetSettingsUseCase,
    val updateSettingsUseCase: UpdateSettingsUseCase,
)