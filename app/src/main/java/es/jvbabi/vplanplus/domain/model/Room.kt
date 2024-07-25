package es.jvbabi.vplanplus.domain.model

data class Room(
    val roomId: Int,
    val name: String,
    val school: School,
)