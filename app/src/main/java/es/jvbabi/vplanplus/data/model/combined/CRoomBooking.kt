package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.ZoneId

data class CRoomBooking(
    @Embedded val roomBooking: DbRoomBooking,
    @Relation(
        parentColumn = "booked_by",
        entityColumn = "id",
        entity = DbVppId::class
    ) val vppId: CVppId?,
    @Relation(
        parentColumn = "room_id",
        entityColumn = "id",
        entity = DbRoom::class
    ) val room: CRoom
) {
    fun toModel(): RoomBooking {
        return RoomBooking(
            id = roomBooking.id,
            room = room.toModel(),
            bookedBy = vppId?.toModel(),
            from = roomBooking.from.toZonedLocalDateTime().atZone(ZoneId.systemDefault()),
            to = roomBooking.to.toZonedLocalDateTime().atZone(ZoneId.systemDefault()),
        )
    }
}