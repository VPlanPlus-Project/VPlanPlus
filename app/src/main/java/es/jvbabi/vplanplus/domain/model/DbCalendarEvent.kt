package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "calendar_events",
    indices = [
        Index(value = ["date", "schoolId", "calendarId"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["id"],
            childColumns = ["schoolId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbCalendarEvent(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val date: LocalDate,
    val schoolId: Long,
    val calendarId: Long
)