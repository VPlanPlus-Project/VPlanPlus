package es.jvbabi.vplanplus.domain

import es.jvbabi.vplanplus.domain.usecase.Response

data class OnlineResponse<T>(
    val data: T,
    val response: Response
)
