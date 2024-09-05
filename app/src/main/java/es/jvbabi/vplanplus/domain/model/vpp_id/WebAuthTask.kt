package es.jvbabi.vplanplus.domain.model.vpp_id

import es.jvbabi.vplanplus.domain.model.VppId
import java.time.ZonedDateTime

data class WebAuthTask(
    val taskId: Int,
    val emojis: List<String>,
    val validUntil: ZonedDateTime,
    val vppId: VppId.ActiveVppId,
)