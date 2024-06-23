package es.jvbabi.vplanplus.domain.model

import com.google.gson.annotations.SerializedName
import java.time.ZoneId
import java.time.ZonedDateTime

data class VppId(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String?,
    @SerializedName("school_id") val schoolId: Int,
    @SerializedName("school") val school: School?,
    @SerializedName("class_name") val groupName: String,
    @SerializedName("classes") val group: Group?,
    @SerializedName("state") val state: State = State.ACTIVE,
    @SerializedName("cached_at") val cachedAt: ZonedDateTime = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
) {
    fun isActive() = state == State.ACTIVE
}

enum class State {
    ACTIVE,
    DISABLED,
    CACHE
}