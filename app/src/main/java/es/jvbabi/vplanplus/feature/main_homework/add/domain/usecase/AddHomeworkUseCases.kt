package es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase

data class AddHomeworkUseCases(
    val getDefaultLessonsUseCase: GetDefaultLessonsUseCase,
    val canShowVppIdBannerUseCase: CanShowVppIdBannerUseCase,
    val hideVppIdBannerUseCase: HideVppIdBannerUseCase,
    val saveHomeworkUseCase: SaveHomeworkUseCase,
)