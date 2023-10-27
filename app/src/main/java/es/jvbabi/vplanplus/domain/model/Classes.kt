package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "classes",
    indices = [Index(value = ["schoolId"])],
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["id"],
            childColumns = ["schoolId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Classes(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val schoolId: Long,
    val className: String,
) {
    override fun toString(): String {
        return "Classes(id=$id, schoolId='$schoolId', className='$className')"
    }
}