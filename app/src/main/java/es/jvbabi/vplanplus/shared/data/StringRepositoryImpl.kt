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
}