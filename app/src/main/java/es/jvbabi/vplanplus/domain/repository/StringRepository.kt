package es.jvbabi.vplanplus.domain.repository

interface StringRepository {
    fun getString(key: Int): String

    fun getString(key: Int, vararg formatArgs: Any): String
}