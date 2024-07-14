package es.jvbabi.vplanplus.domain.model

data class Group(
    val groupId: Int,
    val name: String,
    val school: School,
    val isClass: Boolean
)