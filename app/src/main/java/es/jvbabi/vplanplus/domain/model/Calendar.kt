package es.jvbabi.vplanplus.domain.model

data class Calendar(
    val id: Long,
    val displayName: String,
    val owner: String
) : Comparable<Calendar> {
    override fun compareTo(other: Calendar): Int {
        return other.id.compareTo(this.id)
    }
}