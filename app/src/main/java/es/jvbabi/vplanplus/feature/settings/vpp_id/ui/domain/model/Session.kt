package es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.model

import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class Session(
    @SerializedName("type") val typeParam: String,
    val name: String,
    val id: Int,
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

    val createAt: LocalDateTime
        get() = DateUtils.getDateTimeFromTimestamp(createAtParam)

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
