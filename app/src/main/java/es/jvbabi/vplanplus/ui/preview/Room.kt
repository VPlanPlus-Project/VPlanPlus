package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.Room
import java.util.UUID

object Room {
    fun generateRoomNames(count: Int): List<String> {
        return roomNames.shuffled().take(count)
    }

    fun generateRoom(school: es.jvbabi.vplanplus.domain.model.School = School.generateRandomSchools(1).first()): Room {
        return Room(
            roomId = UUID.randomUUID(),
            school = school,
            name = roomNames.random(),
        )
    }

    private val roomNames = listOf(
        (101..125).toList(),
        (201..225).toList(),
        (301..325).toList()
    ).flatten().map { it.toString() }

}