package top.k88936.nextcloud_tv.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.xml.xml
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.local.ICredentialStore

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data class Authenticated(val credentials: Credentials) : AuthState()
}

sealed class LoginResult {
    data object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class ClientRepository(
    private val credentialStore: ICredentialStore
) {
    private companion object {
        private const val TAG = "ClientRepository"
        private const val OCS_API_REQUEST_HEADER = "OCS-APIRequest"
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var httpClient: HttpClient? = null

    fun getClient(): HttpClient? = httpClient

    fun getCredentials(): Credentials? = credentialStore.getCredentials()

    suspend fun login(credentials: Credentials? = null): LoginResult {
        val creds = credentials ?: credentialStore.getCredentials()
        ?: return LoginResult.Error("No credentials available")

        val serverUrl = creds.serverURL.trimEnd('/')
        Log.d(TAG, "Logging in to $serverUrl as ${creds.loginName}")

        httpClient?.close()
        val client = createHttpClient(creds)

        return try {
            val response = client.post("$serverUrl/login") {
                headers {
                    append("Origin", serverUrl)
                }
                setBody(
                    FormDataContent(
                        parameters {
                            append("user", creds.loginName)
                            append("password", creds.appPassword)
                            append("rememberme", "1")
                        }
                    )
                )
            }

            Log.d(TAG, "Login response status: ${response.status}")

            val setCookieHeaders = response.headers.getAll("set-cookie") ?: emptyList()
            val hasNcToken = setCookieHeaders.any { it.contains("nc_token") }

            if (hasNcToken) {
                Log.d(TAG, "Login successful - nc_token found")
                credentialStore.saveCredentials(creds)
                _authState.value = AuthState.Authenticated(creds)
                httpClient = client
                LoginResult.Success
            } else {
                Log.w(TAG, "Login failed - nc_token not found")
                httpClient?.close()
                httpClient = null
                LoginResult.Error("Invalid credentials")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error: ${e.message}", e)
            httpClient?.close()
            httpClient = null
            LoginResult.Error(e.message ?: "Login failed")
        }
    }

    fun logout() {
        Log.d(TAG, "Logging out")
        credentialStore.clearCredentials()
        httpClient?.close()
        httpClient = null
        _authState.value = AuthState.Unauthenticated
    }

    private fun createHttpClient(credentials: Credentials): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
                xml(XML {
                    xmlDeclMode = XmlDeclMode.Charset
                })
            }
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = credentials.loginName,
                            password = credentials.appPassword
                        )
                    }
                }
            }
            install(HttpCookies)
            defaultRequest {
                header(OCS_API_REQUEST_HEADER, "true")
            }
        }
    }
}
