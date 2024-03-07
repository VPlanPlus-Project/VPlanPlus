package es.jvbabi.vplanplus.shared.data

object VppIdServer {
    private val environment: ServerEnvironment = JuliusServerEnvironment()
    private val scheme = environment.scheme
    val host = environment.host
    const val API_VERSION = "v2"
}

private open class ServerEnvironment(
    open val scheme: String = "https",
    open val host: String = "id.vpp.jvbabi.es"
)

@Suppress("unused")
private class JuliusServerEnvironment : ServerEnvironment(
    "https",
    "vppid-development.test.jvbabi.es"
)