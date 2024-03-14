package es.jvbabi.vplanplus.feature.home.domain.usecase

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.util.UUID

class GetDayForCurrentProfileUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val profileRepository: ProfileRepository,
    private val planRepository: PlanRepository
) {
    operator fun invoke(date: Date) = flow {
        val planDate = when (date) {
            Date.TODAY -> LocalDate.now()
            Date.NEXT -> {
                var date = LocalDate.now().plusDays(1L)
                while (date.dayOfWeek.value > 5) {
                    date = date.plusDays(1L)
                }
                date
            }
        }

        combine(
            keyValueRepository.getFlow(Keys.ACTIVE_PROFILE),
            keyValueRepository.getFlow(Keys.LESSON_VERSION_NUMBER)
        ) { activeProfileId, rawLessonVersion ->
            val version = rawLessonVersion?.toLongOrNull() ?: -1L

            if (activeProfileId == null) return@combine null
            val profile = profileRepository.getProfileById(UUID.fromString(activeProfileId)).first() ?: return@combine null
            return@combine planRepository.getDayForProfile(profile, planDate, version).first()
        }.collect {
            emit(it)
        }
    }
}

enum class Date {
    TODAY,
    NEXT
}