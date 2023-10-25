package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "keyValue",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class KeyValue(
    @PrimaryKey(autoGenerate = false) val id: String,
    val value: String,
)
