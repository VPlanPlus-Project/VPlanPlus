package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "calendar_events",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolCalendarEventRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbCalendarEvent(
    @PrimaryKey(autoGenerate = true) val calendarEventId: Long = 0,
    val date: LocalDate,
    val schoolCalendarEventRefId: Long,
    val calendarId: Long
)