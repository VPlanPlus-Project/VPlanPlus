package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "classes",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class Classes(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val className: String,
    val schoolId: String,
)