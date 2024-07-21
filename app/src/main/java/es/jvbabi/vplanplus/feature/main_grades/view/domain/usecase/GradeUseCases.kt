package es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase

data class GradeUseCases(
    val isEnabledUseCase: IsEnabledUseCase,
    val getGradesUseCase: GetGradesUseCase,
    val showCalculationDisclaimerBannerUseCase: ShowCalculationDisclaimerBannerUseCase,
    val hideCalculationDisclaimerBannerUseCase: HideCalculationDisclaimerBannerUseCase,
    val isBiometricEnabled: IsBiometricEnabledUseCase,
    val canShowEnableBiometricBannerUseCase: CanShowEnableBiometricBannerUseCase,
    val hideEnableBiometricBannerUseCase: HideEnableBiometricBannerUseCase,
    val setBiometricUseCase: SetBiometricUseCase,
    val isBiometricSetUpUseCase: IsBiometricSetUpUseCase,
    val requestBiometricUseCase: RequestBiometricUseCase
)