package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "calendar_events",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolCalendarEventRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["calendarEventId"],
    indices = [
        Index("calendarEventId", unique = true),
        Index("schoolCalendarEventRefId")
    ]
)
data class DbCalendarEvent(
    val calendarEventId: UUID = UUID.randomUUID(),
    val date: LocalDate,
    val schoolCalendarEventRefId: Long,
    val calendarId: Long
)