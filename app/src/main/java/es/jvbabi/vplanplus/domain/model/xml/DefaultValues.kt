package es.jvbabi.vplanplus.domain.model.xml

object DefaultValues {
    fun isEmpty(s: String): Boolean {
        return listOf("&amp;nbsp;", "&nbsp;", " ").contains(s)
    }
}