package kittoku.osc.dto

data class ServerConfig(
    val configVersion: Int = 1,
    var server: String = "",
    var port: Int = 443,
    var user: String = "",
    var password: String = "",
    var certificate: String = ""
) {
    companion object {
        fun create(): ServerConfig? {
            return ServerConfig()
        }
    }
}

