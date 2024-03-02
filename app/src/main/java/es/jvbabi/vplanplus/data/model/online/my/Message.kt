package es.jvbabi.vplanplus.data.model.online.my

import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.domain.model.Importance

data class MessageResponse(
    @SerializedName("items") val items: List<Message>
)

data class Message(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("not_before_version") val notBeforeVersion: Int,
    @SerializedName("not_after_version") val notAfterVersion: Int,
    @SerializedName("created")  val created: String,
    @SerializedName("school_id") val schoolId: Number,
    @SerializedName("expand")  val expand: Expand
)

data class Expand(
    @SerializedName("importance")  val importance: RawImportance
)

data class RawImportance(
    @SerializedName("value") val value: String
) {
    fun toImportance(): Importance {
        return when (value) {
            "normal" -> Importance.NORMAL
            "critical" -> Importance.CRITICAL
            else -> Importance.NORMAL
        }
    }
}
