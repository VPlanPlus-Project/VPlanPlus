package es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model

import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import java.time.ZonedDateTime

data class Session(
    @SerializedName("session_type") val typeParam: String,
    @SerializedName("session_name") val name: String,
    @SerializedName("session_id") val id: Int,
    @SerializedName("created_at") private val createAtParam: Long,
    @SerializedName("is_current") val isCurrent: Boolean
) {
    constructor(
        type: SessionType,
        name: String,
        id: Int,
        createAt: ZonedDateTime,
        isCurrent: Boolean
    ) : this(
        when (type) {
            SessionType.VPLANPLUS -> "a"
            SessionType.WEB -> "w"
        },
        name,
        id,
        ZonedDateTimeConverter().zonedDateTimeToTimestamp(createAt),
        isCurrent
    )

    val createAt: ZonedDateTime
        get() = ZonedDateTimeConverter().timestampToZonedDateTime(createAtParam/1000)

    val type: SessionType
        get() = when (typeParam) {
            "a" -> SessionType.VPLANPLUS
            "w" -> SessionType.WEB
            else -> throw IllegalArgumentException("Unknown session type: $typeParam")
        }
}

enum class SessionType {
    VPLANPLUS,
    WEB
}
