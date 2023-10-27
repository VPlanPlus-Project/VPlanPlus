package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "keyValue",
)
data class KeyValue(
    @PrimaryKey(autoGenerate = false) val id: String,
    val value: String,
)
