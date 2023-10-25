package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weeks",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class Week(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val schoolId: String,
    val week: Int,
    val start: Long,
    val end: Long,
    val type: String
)
