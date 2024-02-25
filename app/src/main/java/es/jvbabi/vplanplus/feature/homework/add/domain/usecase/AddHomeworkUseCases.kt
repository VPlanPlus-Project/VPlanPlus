package es.jvbabi.vplanplus.feature.homework.add.domain.usecase

data class AddHomeworkUseCases(
    val getDefaultLessonsUseCase: GetDefaultLessonsUseCase,
    val canShowVppIdBannerUseCase: CanShowVppIdBannerUseCase,
    val hideVppIdBannerUseCase: HideVppIdBannerUseCase,
)