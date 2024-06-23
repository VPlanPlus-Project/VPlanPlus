package es.jvbabi.vplanplus.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import es.jvbabi.vplanplus.shared.data.BasicAuthentication

@Entity(
    tableName = "school",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
    ]
)
data class School(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "sp24_school_id") val sp24SchoolId: Int,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("username") val username: String,
    @ColumnInfo("password") val password: String,
    @ColumnInfo("days_per_week") val daysPerWeek: Int,
    @ColumnInfo("fully_compatible") val fullyCompatible: Boolean,
    @ColumnInfo(name = "credentials_valid", defaultValue = "NULL") val credentialsValid: Boolean? = null
) {
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + sp24SchoolId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + daysPerWeek
        result = 31 * result + fullyCompatible.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as School

        if (id != other.id) return false
        if (sp24SchoolId != other.sp24SchoolId) return false
        if (name != other.name) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (daysPerWeek != other.daysPerWeek) return false
        return fullyCompatible == other.fullyCompatible
    }

    fun buildAccess(): SchoolSp24Access {
        return SchoolSp24Access(id, sp24SchoolId, username, password)
    }
}

data class SchoolSp24Access(
    val schoolId: Int,
    val sp24SchoolId: Int,
    val username: String,
    val password: String
) {
    fun buildSp24Authentication(): BasicAuthentication {
        return BasicAuthentication(username, password)
    }

    fun buildVppAuthentication(): BasicAuthentication {
        return BasicAuthentication("$username@$sp24SchoolId", password)
    }
}