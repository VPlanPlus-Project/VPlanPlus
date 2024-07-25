package es.jvbabi.vplanplus.data.model.homework

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import java.util.UUID

@Entity(
    tableName = "homework_profile_data",
    primaryKeys = ["homework_id", "profile_id"],
    indices = [
        Index(value = ["homework_id"]),
        Index(value = ["profile_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbHomework::class,
            parentColumns = ["id"],
            childColumns = ["homework_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbClassProfile::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbHomeworkProfileData(
    @ColumnInfo("homework_id") val homeworkId: Int,
    @ColumnInfo("profile_id") val profileId: UUID,
    @ColumnInfo("is_hidden") val isHidden: Boolean,
)