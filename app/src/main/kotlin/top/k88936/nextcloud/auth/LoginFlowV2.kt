package top.k88936.nextcloud.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginFlowInitResponse(
    val poll: PollInfo,
    val login: String
)

@Serializable
data class PollInfo(
    val token: String,
    val endpoint: String
)

@Serializable
data class PollResponse(
    val server: String,
    @SerialName("loginName")
    val loginName: String,
    @SerialName("appPassword")
    val appPassword: String
)

object LoginFlowV2 {
    suspend fun initiateLogin(serverUrl: String): Result<LoginFlowInitResponse> {
        return runCatching {
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }.use { client ->
                val url = serverUrl.trimEnd('/') + "/index.php/login/v2"
                val response: HttpResponse = client.post(url)
                response.body()
            }
        }
    }

    suspend fun pollForAuth(token: String, endpoint: String): Result<PollResponse?> {
        return runCatching {
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }.use { client ->
                val response: HttpResponse = client.post(endpoint) {
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody("token=$token")
                }
                if (response.status == HttpStatusCode.OK) {
                    response.body()
                } else {
                    null
                }
            }
        }
    }
}
