package es.jvbabi.vplanplus.domain.model

import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import java.util.UUID

data class Profile(
    val id: UUID,
    val originalName: String,
    val displayName: String,
    val type: ProfileType,
    val referenceId: UUID,
    val defaultLessons: Map<DefaultLesson, Boolean>,
    val calendarType: ProfileCalendarType,
    val calendarId: Long?
) {

    /**
     * Returns true if the default lesson is enabled for this profile
     * Returns also true if default lesson isn't found in profile
     */
    fun isDefaultLessonEnabled(vpId: Long?): Boolean {
        return defaultLessons.filterKeys { it.vpId == vpId }.values.firstOrNull()?:true
    }
}