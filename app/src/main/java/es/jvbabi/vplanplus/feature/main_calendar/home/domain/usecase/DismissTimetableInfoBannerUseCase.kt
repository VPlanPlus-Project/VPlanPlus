package es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys

class DismissTimetableInfoBannerUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke() {
        keyValueRepository.set(Keys.SHOW_TIMETABLE_INFO_BANNER, "false")
    }
}