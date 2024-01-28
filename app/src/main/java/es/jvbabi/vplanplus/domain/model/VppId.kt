package es.jvbabi.vplanplus.domain.model

data class VppId(
    val id: Int,
    val name: String,
    val schoolName: String,
    val school: School?,
    val className: String,
    val classes: Classes?
)
