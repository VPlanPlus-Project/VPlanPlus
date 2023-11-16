package es.jvbabi.vplanplus.domain.model

data class Room(
    val roomId: Long = 0,
    val name: String,
    val school: School,
)