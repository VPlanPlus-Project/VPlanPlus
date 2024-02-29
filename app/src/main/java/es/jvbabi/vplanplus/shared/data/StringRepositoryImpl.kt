package es.jvbabi.vplanplus.shared.data

import android.content.Context
import es.jvbabi.vplanplus.domain.repository.StringRepository

class StringRepositoryImpl(
    val context: Context
) : StringRepository {
    override fun getString(key: Int): String {
        return context.getString(key)
    }

    override fun getString(key: Int, vararg formatArgs: Any): String {
        return context.getString(key, *formatArgs)
    }

    override fun getPlural(key: Int, quantity: Int): String {
        return context.resources.getQuantityString(key, quantity)
    }

    override fun getPlural(key: Int, quantity: Int, vararg formatArgs: Any): String {
        return context.resources.getQuantityString(key, quantity, *formatArgs)
    }

}