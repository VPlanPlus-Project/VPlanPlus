package es.jvbabi.vplanplus.feature.homework.shared.domain.model

import java.time.DayOfWeek

data class PreferredHomeworkNotificationTime(
    val dayOfWeek: DayOfWeek,
    val hour: Int,
    val minute: Int,
    val overrideDefault: Boolean
)
