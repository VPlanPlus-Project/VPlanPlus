package es.jvbabi.vplanplus.shared.data

object VppIdServer {
    private const val scheme = "https"
    private const val host = "id.vpp.jvbabi.es"

    val url: String
        get() { return "$scheme://$host" }
}