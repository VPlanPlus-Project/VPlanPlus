package es.jvbabi.vplanplus.domain.model

import java.time.LocalDateTime

data class RoomBooking(
    val id: Long,
    val room: Room,
    val bookedBy: VppId?,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val `class`: Classes
)
