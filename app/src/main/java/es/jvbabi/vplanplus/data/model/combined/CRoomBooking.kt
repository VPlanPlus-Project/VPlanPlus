package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.domain.model.RoomBooking

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
            room = room.toRoomModel(),
            bookedBy = vppId?.toModel(),
            from = roomBooking.from,
            to = roomBooking.to,
            `class` = classes.toClassModel()
        )
    }
}