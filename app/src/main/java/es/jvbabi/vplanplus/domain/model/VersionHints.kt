package es.jvbabi.vplanplus.domain.model

import java.time.ZonedDateTime

data class VersionHints(
    val header: String,
    val content: String,
    val version: Int,
    val createdAt: ZonedDateTime
)
