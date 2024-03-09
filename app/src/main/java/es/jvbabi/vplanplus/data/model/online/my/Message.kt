package es.jvbabi.vplanplus.data.model.online.my

import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.model.Importance

data class MessageResponse(
    @SerializedName("data") val items: List<Message>
)

data class Message(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("not_before_version") val notBeforeVersion: Int,
    @SerializedName("not_after_version") val notAfterVersion: Int,
    @SerializedName("not_before_date") val notBeforeDate: Long,
    @SerializedName("sp24_school_id") val schoolId: Int?,
    @SerializedName("priority") val priority: String
) {
    fun toMessage(): es.jvbabi.vplanplus.domain.model.Message {
        return es.jvbabi.vplanplus.domain.model.Message(
            id = id,
            title = title,
            content = content,
            date = ZonedDateTimeConverter().timestampToZonedDateTime(notBeforeDate),
            isRead = false,
            importance = when (priority.lowercase()) {
                "low" -> Importance.LOW
                "medium" -> Importance.MEDIUM
                "high" -> Importance.HIGH
                else -> Importance.MEDIUM
            },
            fromVersion = notBeforeVersion,
            toVersion = notAfterVersion,
            schoolId = schoolId?.toLong()
        )
    }
}