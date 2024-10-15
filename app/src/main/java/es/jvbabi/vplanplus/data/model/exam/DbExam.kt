package es.jvbabi.vplanplus.data.model.exam

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.vppid.DbVppId
import java.time.LocalDate
import java.time.ZonedDateTime

@Entity(
    tableName = "exams",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["created_by"]),
        Index(value = ["group_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbVppId::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = CASCADE
        )
    ]
)
data class DbExam(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "subject") val subject: Int?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "created_by") val createdBy: Int?,
    @ColumnInfo(name = "group_id") val groupId: Int,
    @ColumnInfo(name = "created_at") val createdAt: ZonedDateTime
)