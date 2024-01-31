package es.jvbabi.vplanplus.domain

import es.jvbabi.vplanplus.domain.usecase.Response

data class DataResponse<T>(
    val data: T,
    val response: Response
)