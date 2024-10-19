package es.jvbabi.vplanplus.feature.ndp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Entity(
    tableName = "ndp_profile_time",
    primaryKeys = ["profile_id", "date"],
    indices = [
        Index("profile_id"),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbClassProfile::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbNdpProfileTime(
    @ColumnInfo(name = "profile_id") val profileId: UUID,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "time") val time: LocalTime,
    @ColumnInfo(name = "has_completed") val hasCompleted: Boolean,
)