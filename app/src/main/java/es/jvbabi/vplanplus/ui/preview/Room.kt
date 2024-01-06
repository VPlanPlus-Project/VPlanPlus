package es.jvbabi.vplanplus.ui.preview

import kotlin.random.Random

object Room {
    fun generateRoomNames(count: Int): List<String> {
        return roomNames.shuffled().take(count)
    }

    val roomNames = listOf(
        (101..125).toList(),
        (201..225).toList(),
        (301..325).toList()
    ).flatten().map { it.toString() }

    fun generateAvailabilityMap(count: Int = 12): List<Boolean> {
        return List(count) { Random.nextBoolean() }
    }
}