package es.jvbabi.vplanplus.feature.homework.shared.domain.model

import java.time.DayOfWeek
import java.time.LocalDateTime

data class PreferredHomeworkNotificationTime(
    val dayOfWeek: DayOfWeek,
    val time: LocalDateTime,
    val overrideDefault: Boolean
)
