package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.Importance
import es.jvbabi.vplanplus.domain.model.Message
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random

object News {
    fun generateNews(): List<Message> {
        val messages = mutableListOf<Message>()
        repeat(5) { i ->
            messages.add(Message(
                id = UUID.randomUUID().toString(),
                title = "Example $i",
                content = "Example $i <b>with</b> HTML " + Text.LOREM_IPSUM_100,
                date = ZonedDateTime.now().minusDays(5L-i),
                isRead = Random.nextBoolean(),
                importance = if (Random.nextBoolean()) Importance.MEDIUM else Importance.MEDIUM,
                schoolId = 10000000,
                fromVersion = 0,
                toVersion = 200
            ))
        }
        return messages
    }
}