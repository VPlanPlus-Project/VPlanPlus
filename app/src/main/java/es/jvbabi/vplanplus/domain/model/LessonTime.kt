package es.jvbabi.vplanplus.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.atDate
import es.jvbabi.vplanplus.util.DateUtils.atStartOfDay
import es.jvbabi.vplanplus.util.TimeSpan
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID


/**
 * @author Julius Babies
 * Represents a lesson time.
 * @param id The id of the lesson time. (automatically set by the database)
 * @param groupId The id of the class.
 * @param lessonNumber The number of the lesson at this day.
 * @param from The start time of the lesson in seconds starting from 00:00:00
 * @param to The end time of the lesson in seconds starting from 00:00:00
 */
@Entity(
    tableName = "lesson_time",
    foreignKeys = [
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["id", "group_id", "lesson_number"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["group_id"]),
    ]
)
data class LessonTime(
    @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("group_id") val groupId: Int,
    @ColumnInfo("lesson_number") val lessonNumber: Int,
    @ColumnInfo("start") val from: Long,
    @ColumnInfo("end") val to: Long,
) {

    @get:Ignore
    val end: ZonedDateTime
        get() = ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Europe/Berlin")).atStartOfDay().atBeginningOfTheWorld().plusSeconds(this.to)


    @get:Ignore
    val start: ZonedDateTime
        get() = ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Europe/Berlin")).atStartOfDay().atBeginningOfTheWorld().plusSeconds(this.from)

    fun toTimeSpan(withDate: ZonedDateTime? = null): TimeSpan {
        return TimeSpan(run {
            if (withDate != null) start.atDate(withDate)
            else start
        }, run {
            if (withDate != null) end.atDate(withDate)
            else end
        })
    }
}