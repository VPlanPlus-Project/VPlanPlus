package es.jvbabi.vplanplus.domain.model

import com.google.gson.annotations.SerializedName

data class VppId(
    val id: Int,
    val name: String,
    @SerializedName("school_id") val schoolId: Long,
    val school: School?,
    @SerializedName("class_name") val className: String,
    val classes: Classes?,
    val state: State = State.ACTIVE
)

enum class State {
    ACTIVE,
    DISABLED
}