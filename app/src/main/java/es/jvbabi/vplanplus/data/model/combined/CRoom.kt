package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.domain.model.DbSchool
import es.jvbabi.vplanplus.domain.model.Room

data class CRoom(
    @Embedded val room: DbRoom,
    @Relation(
        parentColumn = "school_id",
        entityColumn = "id",
        entity = DbSchool::class
    ) val school: CSchool
) {
    fun toModel(): Room {
        return Room(
            roomId = room.id,
            name = room.name,
            school = school.toModel()
        )
    }
}