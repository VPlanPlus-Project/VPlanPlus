package es.jvbabi.vplanplus.data.model.vppid

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.domain.model.DbSchool
import es.jvbabi.vplanplus.domain.model.State
import java.time.ZonedDateTime

@Entity(
    tableName = "vpp_id",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["school_id"]),
        Index(value = ["group_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbSchool::class,
            parentColumns = ["id"],
            childColumns = ["school_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbVppId(
    @ColumnInfo("id") val id: Int,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("email") val email: String?,
    @ColumnInfo("school_id") val schoolId: Int,
    @ColumnInfo("group_name") val groupName: String,
    @ColumnInfo("group_id") val classId: Int?,
    @ColumnInfo("state") val state: State,
    @ColumnInfo("cached_at") val cachedAt: ZonedDateTime?
)