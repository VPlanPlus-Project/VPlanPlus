package es.jvbabi.vplanplus.domain.model

import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.ui.screens.home.MenuProfile

data class Profile(
    val id: Long,
    val originalName: String,
    val displayName: String,
    val type: ProfileType,
    val referenceId: Long,
    val defaultLessons: Map<DefaultLesson, Boolean>,
    val calendarType: ProfileCalendarType,
    val calendarId: Long?
) {
    fun toMenuProfile(): MenuProfile {
        return MenuProfile(id, originalName, displayName)
    }

    /**
     * Returns true if the default lesson is enabled for this profile
     * Returns also true if default lesson isn't found in profile
     */
    fun isDefaultLessonEnabled(vpId: Long?): Boolean {
        return defaultLessons.filterKeys { it.vpId == vpId }.values.firstOrNull()?:true
    }
}