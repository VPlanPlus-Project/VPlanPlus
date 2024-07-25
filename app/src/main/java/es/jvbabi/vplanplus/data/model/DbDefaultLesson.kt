package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "default_lesson",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["vp_id"]),
        Index(value = ["class_id"]),
        Index(value = ["teacher_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["class_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["teacher_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class DbDefaultLesson(
    @ColumnInfo("id") val id: UUID,
    @ColumnInfo("vp_id") val vpId: Int,
    @ColumnInfo("subject") val subject: String,
    @ColumnInfo("teacher_id") val teacherId: UUID?,
    @ColumnInfo("class_id") val classId: Int,
)