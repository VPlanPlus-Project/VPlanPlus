package es.jvbabi.vplanplus.data.repository

import kotlinx.coroutines.delay

class InternetRepository(
    val fail: Boolean
) {
    suspend inline fun <reified T> simulateNetworkCall(response: T): T? {
        delay(200)
        return if (this.fail) null else response
    }
}