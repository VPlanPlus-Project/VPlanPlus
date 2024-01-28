package es.jvbabi.vplanplus.data.model

import androidx.room.Entity

@Entity
data class DbVppIdToken(
    val id: String,
    val vppId: Int,
    val token: String
)