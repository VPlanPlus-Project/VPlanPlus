package es.jvbabi.vplanplus.feature.main_homework.shared.domain.model

import java.time.DayOfWeek

data class PreferredHomeworkNotificationTime(
    val dayOfWeek: DayOfWeek,
    val secondsFromMidnight: Long,
    val overrideDefault: Boolean
)
