package es.jvbabi.vplanplus.domain.model

import androidx.compose.runtime.Immutable
import es.jvbabi.vplanplus.data.model.combined.NotificationSettingJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Immutable
sealed class Profile(
    val id: UUID,
    val originalName: String,
    val displayName: String,
    val calendarType: ProfileCalendarType,
    val calendarId: Long?,
    val notificationsEnabled: Boolean = true,
    open val notificationSettings: CommonProfileNotificationSetting
) {
    fun getType(): ProfileType {
        return when (this) {
            is TeacherProfile -> ProfileType.TEACHER
            is RoomProfile -> ProfileType.ROOM
            is ClassProfile -> ProfileType.STUDENT
        }
    }

    abstract fun getSchool(): School

    fun toLogString(): String {
        return "${getType()} $displayName ($originalName) [$id]"
    }
}

class TeacherProfile(
    id: UUID,
    originalName: String,
    displayName: String,
    calendarType: ProfileCalendarType,
    calendarId: Long?,
    notificationsEnabled: Boolean,
    override val notificationSettings: TeacherProfileNotificationSetting,
    val teacher: Teacher
) : Profile(id, originalName, displayName, calendarType, calendarId, notificationsEnabled, notificationSettings) {
    override fun getSchool() = teacher.school
}

class RoomProfile(
    id: UUID,
    originalName: String,
    displayName: String,
    calendarType: ProfileCalendarType,
    calendarId: Long?,
    notificationsEnabled: Boolean,
    override val notificationSettings: RoomProfileNotificationSetting,
    val room: Room
) : Profile(id, originalName, displayName, calendarType, calendarId, notificationsEnabled, notificationSettings) {
    override fun getSchool() = room.school
}

class ClassProfile(
    id: UUID,
    originalName: String,
    displayName: String,
    calendarType: ProfileCalendarType,
    calendarId: Long?,
    notificationsEnabled: Boolean,
    override val notificationSettings: ClassProfileNotificationSetting,
    val group: Group,
    val isHomeworkEnabled: Boolean,
    val isAssessmentsEnabled: Boolean,
    val defaultLessons: Map<DefaultLesson, Boolean>,
    val vppId: VppId.ActiveVppId?
) : Profile(id, originalName, displayName, calendarType, calendarId, notificationsEnabled, notificationSettings) {
    /**
     * Returns true if the default lesson is enabled for this profile
     * Returns also true if default lesson isn't found in profile
     */
    fun isDefaultLessonEnabled(vpId: Int?): Boolean {
        return defaultLessons.filterKeys { it.vpId == vpId }.values.firstOrNull() ?: true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassProfile

        return id == other.id &&
                group == other.group &&
                isHomeworkEnabled == other.isHomeworkEnabled &&
                isAssessmentsEnabled == other.isAssessmentsEnabled &&
                defaultLessons == other.defaultLessons &&
                vppId == other.vppId &&
                originalName == other.originalName &&
                displayName == other.displayName &&
                calendarType == other.calendarType &&
                calendarId == other.calendarId &&
                notificationsEnabled == other.notificationsEnabled &&
                notificationSettings == other.notificationSettings
    }

    override fun getSchool() = group.school

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + isHomeworkEnabled.hashCode()
        result = 31 * result + isAssessmentsEnabled.hashCode()
        result = 31 * result + defaultLessons.hashCode()
        result = 31 * result + notificationsEnabled.hashCode()
        result = 31 * result + notificationSettings.hashCode()
        result = 31 * result + (vppId?.hashCode() ?: 0)
        return result
    }
}

enum class ProfileType {
    TEACHER, STUDENT, ROOM
}

enum class ProfileCalendarType {
    DAY, LESSON, NONE
}

data class NotificationSetting(
    val key: String,
    val enabled: Boolean? = null,
    private val defaultEnabled: Boolean
) {
    fun isEnabled(): Boolean {
        return enabled ?: defaultEnabled
    }
}

open class CommonProfileNotificationSetting(
    newPlanEnabled: Boolean? = null
) {
    val newPlanNotificationSetting = NotificationSetting("new_plan", newPlanEnabled, true)

    open val notificationSettings: List<NotificationSetting>
        get() = listOf(
            newPlanNotificationSetting
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommonProfileNotificationSetting

        return newPlanNotificationSetting == other.newPlanNotificationSetting
    }

    override fun hashCode(): Int {
        val result = newPlanNotificationSetting.hashCode()
        return result
    }

    fun toJson(): String {
        return Json.encodeToString(notificationSettings.mapNotNull {
            NotificationSettingJson(it.key, it.enabled ?: return@mapNotNull null)
        })
    }

    fun copy(
        newPlanEnabled: Boolean? = this.newPlanNotificationSetting.enabled
    ) = CommonProfileNotificationSetting(
        newPlanEnabled = newPlanEnabled
    )
}

class TeacherProfileNotificationSetting(
    commonProfileNotificationSetting: CommonProfileNotificationSetting = CommonProfileNotificationSetting()
): CommonProfileNotificationSetting(
    commonProfileNotificationSetting.newPlanNotificationSetting.enabled
) {
    fun copy(
        commonProfileNotificationSetting: CommonProfileNotificationSetting = this,
    ) = TeacherProfileNotificationSetting(
        commonProfileNotificationSetting = commonProfileNotificationSetting,
    )
}

class RoomProfileNotificationSetting(
    commonProfileNotificationSetting: CommonProfileNotificationSetting = CommonProfileNotificationSetting()
) : CommonProfileNotificationSetting(
    commonProfileNotificationSetting.newPlanNotificationSetting.enabled
) {
    fun copy(
        commonProfileNotificationSetting: CommonProfileNotificationSetting = this,
    ) = RoomProfileNotificationSetting(
        commonProfileNotificationSetting = commonProfileNotificationSetting,
    )
}

class ClassProfileNotificationSetting(
    commonProfileNotificationSetting: CommonProfileNotificationSetting = CommonProfileNotificationSetting(),
    onNewHomeworkEnabled: Boolean? = null,
    onNewAssessmentEnabled: Boolean? = null,
    isDailyNotificationEnabled: Boolean? = null
) : CommonProfileNotificationSetting(
    commonProfileNotificationSetting.newPlanNotificationSetting.enabled
) {
    val onNewHomeworkNotificationSetting = NotificationSetting("on_new_homework", onNewHomeworkEnabled, true)
    val onNewAssessmentSetting = NotificationSetting("on_new_assessment", onNewAssessmentEnabled, true)
    val dailyNotificationSetting = NotificationSetting("daily", isDailyNotificationEnabled, true)

    override val notificationSettings: List<NotificationSetting>
        get() = super.notificationSettings + listOf(
            onNewHomeworkNotificationSetting,
            onNewAssessmentSetting,
            dailyNotificationSetting
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CommonProfileNotificationSetting) return false
        if (!super.equals(other)) return false
        if (javaClass != other.javaClass) return false

        other as ClassProfileNotificationSetting

        if (onNewHomeworkNotificationSetting != other.onNewHomeworkNotificationSetting) return false
        if (onNewAssessmentSetting != other.onNewAssessmentSetting) return false
        if (dailyNotificationSetting != other.dailyNotificationSetting) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + onNewHomeworkNotificationSetting.hashCode()
        result = 31 * result + onNewAssessmentSetting.hashCode()
        result = 31 * result + dailyNotificationSetting.hashCode()
        return result
    }

    constructor(
        previous: ClassProfileNotificationSetting,
        onNewHomeworkEnabled: Boolean? = previous.onNewHomeworkNotificationSetting.enabled,
        onNewAssessmentEnabled: Boolean? = previous.onNewAssessmentSetting.enabled,
        isDailyNotificationEnabled: Boolean? = previous.dailyNotificationSetting.enabled
    ) : this(
        previous as CommonProfileNotificationSetting,
        onNewHomeworkEnabled = onNewHomeworkEnabled,
        onNewAssessmentEnabled = onNewAssessmentEnabled,
        isDailyNotificationEnabled = isDailyNotificationEnabled
    )

    fun copy(
        commonProfileNotificationSetting: CommonProfileNotificationSetting = this,
        onNewHomeworkEnabled: Boolean? = this.onNewHomeworkNotificationSetting.enabled,
        onNewAssessmentEnabled: Boolean? = this.onNewAssessmentSetting.enabled,
        isDailyNotificationEnabled: Boolean? = this.dailyNotificationSetting.enabled
    ) = ClassProfileNotificationSetting(
        commonProfileNotificationSetting = commonProfileNotificationSetting,
        onNewHomeworkEnabled = onNewHomeworkEnabled,
        onNewAssessmentEnabled = onNewAssessmentEnabled,
        isDailyNotificationEnabled = isDailyNotificationEnabled
    )
}