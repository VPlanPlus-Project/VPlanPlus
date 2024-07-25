package es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.HomeworkReminderUseCase

data class AdvancedSettingsUseCases(
    val deleteCacheUseCase: DeleteCacheUseCase,
    val getVppIdServerUseCase: GetVppIdServerUseCase,
    val setVppIdServerUseCase: SetVppIdServerUseCase,
    val updateFcmTokenUseCase: UpdateFcmTokenUseCase,
    val resetBalloonsUseCase: ResetBalloonsUseCase,
    val homeworkReminderUseCase: HomeworkReminderUseCase
)