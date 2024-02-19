package es.jvbabi.vplanplus.shared.data

object VppIdServer {
    private const val scheme = "https"
    private const val host = "id.vpp.jvbabi.es"
    const val apiVersion = "v1.1"

    val url: String
        get() { return "$scheme://$host" }
}