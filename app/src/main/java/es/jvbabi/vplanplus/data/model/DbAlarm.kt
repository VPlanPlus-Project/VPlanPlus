package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import es.jvbabi.vplanplus.domain.model.Alarm
import java.time.ZonedDateTime

@Entity(
    tableName = "alarm",
)
data class DbAlarm(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "time") val time: ZonedDateTime,
    @ColumnInfo(name = "tags") val tags: String,
    @ColumnInfo(name = "data") val data: String,
) {
    fun toModel(): Alarm {
        return Alarm(
            id = id,
            time = time,
            tags = tags
                .drop(1)
                .dropLast(1)
                .split(";"),
            data = data
        )
    }
}