package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class LogRecord(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val timestamp: Long,
    val tag: String,
    val message: String
)