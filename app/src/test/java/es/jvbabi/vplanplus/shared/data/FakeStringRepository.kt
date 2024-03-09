package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.repository.StringRepository

class FakeStringRepository(
    private val strings: Map<Int, String> = mutableMapOf()
) : StringRepository {

    override fun getString(key: Int): String {
        return strings[key] ?: throw IllegalArgumentException("String $key not found")
    }

    override fun getString(key: Int, vararg formatArgs: Any): String {
        return strings[key]?.format(*formatArgs) ?: throw IllegalArgumentException("String $key not found")
    }

    override fun getPlural(key: Int, quantity: Int): String {
        TODO("Not yet implemented")
    }

    override fun getPlural(key: Int, quantity: Int, vararg formatArgs: Any): String {
        TODO("Not yet implemented")
    }
}