package es.jvbabi.vplanplus.domain.model

import com.google.gson.annotations.SerializedName

data class VppId(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String?,
    @SerializedName("school_id") val schoolId: Long,
    @SerializedName("school") val school: School?,
    @SerializedName("class_name") val className: String,
    @SerializedName("classes") val classes: Classes?,
    @SerializedName("state") val state: State = State.ACTIVE
) {
    fun isActive() = state == State.ACTIVE
}

enum class State {
    ACTIVE,
    DISABLED,
    CACHE
}