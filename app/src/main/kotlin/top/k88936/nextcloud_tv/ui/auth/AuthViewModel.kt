package top.k88936.nextcloud_tv.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.repository.IAuthRepository


data class AuthState(
    val serverUrl: String = "https://",
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val authSuccess: Boolean = false
)

class AuthViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val TAG = "AUTH"

    fun updateServerUrl(url: String) {
        Log.d(TAG, "updateServerUrl: $url")
        _state.value = _state.value.copy(serverUrl = url, error = null)
    }

    fun updateUsername(username: String) {
        Log.d(TAG, "updateUsername: $username")
        _state.value = _state.value.copy(username = username, error = null)
    }

    fun updatePassword(password: String) {
        Log.d(TAG, "updatePassword: ***")
        _state.value = _state.value.copy(password = password, error = null)
    }

    fun login() {
        val serverUrl = _state.value.serverUrl.trim().trimEnd('/')
        val username = _state.value.username.trim()
        val password = _state.value.password

        if (serverUrl.isBlank()) {
            _state.value = _state.value.copy(error = "Please enter a server URL")
            return
        }
        if (username.isBlank()) {
            _state.value = _state.value.copy(error = "Please enter a username")
            return
        }
        if (password.isBlank()) {
            _state.value = _state.value.copy(error = "Please enter a password")
            return
        }

        Log.d(TAG, "login: starting with serverUrl=$serverUrl, username=$username")
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val client = HttpClient(OkHttp) {
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        })
                    }
                    install(HttpCookies)
                    defaultRequest {
                        header("OCS-APIRequest", "true")
                    }
                }

                val response: HttpResponse = client.post("$serverUrl/login") {
                    headers {
                        append("Origin", serverUrl)
                    }
                    setBody(
                        FormDataContent(
                            parameters {
                                append("user", username)
                                append("password", password)
                                append("rememberme", "1")
                            }
                        )
                    )
                }

                client.close()

                Log.d(TAG, "Login response status: ${response.status}")
                Log.d(TAG, "Login response headers: ${response.headers}")

                val setCookieHeaders = response.headers.getAll("set-cookie") ?: emptyList()
                Log.d(TAG, "Set-Cookie headers: $setCookieHeaders")

                val hasNcToken = setCookieHeaders.any { it.contains("nc_token") }

                if (hasNcToken) {
                    Log.d(TAG, "Login successful - nc_token found in cookies")
                    val credentials = Credentials(
                        serverURL = serverUrl,
                        loginName = username,
                        appPassword = password
                    )
                    authRepository.saveCredentials(credentials)
                    _state.value = _state.value.copy(authSuccess = true, isLoading = false)
                } else {
                    Log.w(TAG, "Login failed - nc_token not found in cookies")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Login failed: Invalid credentials"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "login error: ${e.message}", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Login failed: ${e.message}"
                )
            }
        }
    }
}
