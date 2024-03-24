package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "key_value",
)
data class KeyValue(
    @PrimaryKey(autoGenerate = false) val id: String,
    val value: String,
)
