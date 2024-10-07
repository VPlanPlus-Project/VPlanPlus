package es.jvbabi.vplanplus.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "school",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
    ]
)
data class DbSchool(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "sp24_school_id") val sp24SchoolId: Int,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("username") val username: String,
    @ColumnInfo("password") val password: String,
    @ColumnInfo("days_per_week") val daysPerWeek: Int,
    @ColumnInfo("fully_compatible") val fullyCompatible: Boolean,
    @ColumnInfo(name = "credentials_valid", defaultValue = "NULL") val credentialsValid: Boolean? = null,
    @ColumnInfo(name = "school_download_mode") val schoolDownloadMode: SchoolDownloadMode,
    @ColumnInfo(name = "can_use_timetable") val canUseTimetable: Boolean?,
)