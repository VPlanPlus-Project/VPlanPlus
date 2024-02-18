package es.jvbabi.vplanplus.shared.data

object VppIdServer {
    private const val scheme = "http"
    private const val host = "surface-julius:8000"
    const val apiVersion = "v1.1"

    val url: String
        get() { return "$scheme://$host" }
}