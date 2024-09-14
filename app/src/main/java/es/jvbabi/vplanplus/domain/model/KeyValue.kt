package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "key_value",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class KeyValue(
    val id: String,
    val value: String,
)
