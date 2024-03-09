package es.jvbabi.vplanplus.domain.model

import java.time.ZonedDateTime

data class RoomBooking(
    val id: Long,
    val room: Room,
    val bookedBy: VppId?,
    val from: ZonedDateTime,
    val to: ZonedDateTime,
    val `class`: Classes
)
