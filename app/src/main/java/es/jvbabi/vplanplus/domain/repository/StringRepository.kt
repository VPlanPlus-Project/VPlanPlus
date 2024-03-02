package es.jvbabi.vplanplus.domain.repository

interface StringRepository {
    fun getString(key: Int): String

    fun getString(key: Int, vararg formatArgs: Any): String

    fun getPlural(key: Int, quantity: Int): String
    fun getPlural(key: Int, quantity: Int, vararg formatArgs: Any): String
}