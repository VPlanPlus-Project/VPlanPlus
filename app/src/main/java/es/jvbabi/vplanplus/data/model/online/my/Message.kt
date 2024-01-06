package es.jvbabi.vplanplus.data.model.online.my

import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.domain.model.Importance

data class MessageResponse(
    val items: List<Message>
)

data class Message(
    val id: String,
    val title: String,
    val content: String,
    @SerializedName("not_before_version") val notBeforeVersion: Int,
    @SerializedName("not_after_version") val notAfterVersion: Int,
    val created: String,
    @SerializedName("school_id") val schoolId: Number,
    val expand: Expand
)

data class Expand(
    val importance: RawImportance
)

data class RawImportance(
    val value: String
) {
    fun toImportance(): Importance {
        return when (value) {
            "normal" -> Importance.NORMAL
            "critical" -> Importance.CRITICAL
            else -> Importance.NORMAL
        }
    }
}
