package es.jvbabi.vplanplus.util

import es.jvbabi.vplanplus.BuildConfig

object VersionTools {
    fun isDevelopmentVersion(): Boolean {
        return BuildConfig.VERSION_NAME.matches(Regex(".*-dev-\\d+"))
    }
}