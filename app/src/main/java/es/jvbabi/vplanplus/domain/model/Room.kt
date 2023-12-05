package es.jvbabi.vplanplus.domain.model

import java.util.UUID

data class Room(
    val roomId: UUID = UUID.randomUUID(),
    val name: String,
    val school: School,
)