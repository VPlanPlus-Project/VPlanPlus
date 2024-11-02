package es.jvbabi.vplanplus.domain.model

import com.google.gson.annotations.SerializedName
import java.time.ZoneId
import java.time.ZonedDateTime

open class VppId(
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

    class Unknown : VppId(-1, "", null, -1, null, "", null, State.ACTIVE)
    fun isActive() = state == State.ACTIVE

    class ActiveVppId(
        id: Int,
        name: String,
        email: String?,
        schoolId: Int,
        school: School?,
        groupName: String,
        group: Group?,
        state: State = State.ACTIVE,
        cachedAt: ZonedDateTime = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
        val vppIdToken: String,
        val schulverwalterToken: String?
    ) : VppId(id, name, email, schoolId, school, groupName, group, state, cachedAt)

    override fun equals(other: Any?): Boolean {
        if (other !is VppId) return super.equals(other)
        return id == other.id
    }
}

enum class State {
    ACTIVE,
    DISABLED,
    CACHE
}