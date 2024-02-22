package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import java.util.Base64

@Entity(
    tableName = "school",
    primaryKeys = ["schoolId"],
    indices = [
        Index(value = ["schoolId"], unique = true),
    ]
)
data class School(
    val schoolId: Long,
    val name: String,
    val username: String,
    val password: String,
    val daysPerWeek: Int,
    val fullyCompatible: Boolean
) {
    override fun hashCode(): Int {
        var result = schoolId.hashCode()
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

        if (schoolId != other.schoolId) return false
        if (name != other.name) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (daysPerWeek != other.daysPerWeek) return false
        return fullyCompatible == other.fullyCompatible
    }

    fun buildToken(): String {
        return Base64.getEncoder().encode("$schoolId+$username+$password".toByteArray()).toString(Charsets.UTF_8)
    }
}