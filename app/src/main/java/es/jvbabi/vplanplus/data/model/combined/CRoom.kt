package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School

data class CRoom(
    @Embedded val room: DbRoom,
    @Relation(
        parentColumn = "schoolRoomRefId",
        entityColumn = "schoolId",
        entity = School::class
    ) val school: School
) {
    fun toModel(): Room {
        return Room(
            roomId = room.roomId,
            name = room.name,
            school = school
        )
    }
}