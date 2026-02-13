import io.kotlintest.specs.ShouldSpec
import kotlinx.coroutines.delay
import top.k88936.nextcloud.auth.LoginFlowV2

class AuthTest : ShouldSpec() {
    val serverUrl = "https://ivo.lv.tab.digital"

    init {
        should("finish auth flow") {
            val initResult = LoginFlowV2.initiateLogin(serverUrl)
            
            if (initResult.isFailure) {
                println("Failed to initiate login: ${initResult.exceptionOrNull()?.message}")
                return@should
            }
            
            val initResponse = initResult.getOrThrow()
            println("Please open this URL in your browser:")
            println(initResponse.login)

            // wait for user to finish his auth in browser.
            // after that our client will poll the server to get app password.
            println("Polling for authentication...")
            delay(16000)

            val pollResult = LoginFlowV2.pollForAuth(
                token = initResponse.poll.token,
                endpoint = initResponse.poll.endpoint
            )

            pollResult.fold(
                onSuccess = { response ->
                    if (response != null) {
                        println("Authentication successful!")
                        println("Server: ${response.server}")
                        println("LoginName: ${response.loginName}")
                        println("AppPassword: ${response.appPassword}")
                        return@should
                    } else {
                        println("Not yet authenticated (404)")
                    }
                },
                onFailure = { error ->
                    println("Poll error: ${error.message}")
                }
            )
            println("Polling finished without authentication")
        }
    }
}
