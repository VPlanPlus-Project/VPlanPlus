package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.ZoneId

data class CRoomBooking(
    @Embedded val roomBooking: DbRoomBooking,
    @Relation(
        parentColumn = "class",
        entityColumn = "id",
        entity = DbSchoolEntity::class
    ) val classes: CSchoolEntity,
    @Relation(
        parentColumn = "bookedBy",
        entityColumn = "id"
    ) val vppId: DbVppId?,
    @Relation(
        parentColumn = "roomId",
        entityColumn = "id",
        entity = DbSchoolEntity::class
    ) val room: CSchoolEntity
) {
    fun toModel(): RoomBooking {
        return RoomBooking(
            id = roomBooking.id,
            room = room.toRoomModel(),
            bookedBy = vppId?.toModel(),
            from = roomBooking.from.toZonedLocalDateTime().atZone(ZoneId.systemDefault()),
            to = roomBooking.to.toZonedLocalDateTime().atZone(ZoneId.systemDefault()),
            `class` = classes.toClassModel()
        )
    }
}