package es.jvbabi.vplanplus.feature.main_grades.domain.usecase

data class GradeUseCases(
    val isEnabledUseCase: IsEnabledUseCase,
    val getGradesUseCase: GetGradesUseCase,
    val showBannerUseCase: ShowBannerUseCase,
    val hideBannerUseCase: HideBannerUseCase,
    val calculateAverageUseCase: CalculateAverageUseCase,
    val isBiometricEnabled: IsBiometricEnabledUseCase,
    val canShowEnableBiometricBannerUseCase: CanShowEnableBiometricBannerUseCase,
    val hideEnableBiometricBannerUseCase: HideEnableBiometricBannerUseCase,
    val setBiometricUseCase: SetBiometricUseCase,
    val isBiometricSetUpUseCase: IsBiometricSetUpUseCase,
    val requestBiometricUseCase: RequestBiometricUseCase
)