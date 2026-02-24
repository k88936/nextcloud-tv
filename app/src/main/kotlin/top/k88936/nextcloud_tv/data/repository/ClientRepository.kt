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
import io.ktor.client.statement.HttpResponse
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.xml.xml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.local.ICredentialStore

sealed class AuthState {
    data object Initializing : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val credentials: Credentials) : AuthState()
}

class ClientRepository(
    private val credentialStore: ICredentialStore
) {
    private companion object {
        private const val TAG = "ClientRepository"
        private const val OCS_API_REQUEST_HEADER = "OCS-APIRequest"
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var httpClient: HttpClient? = null
    private var currentCredentials: Credentials? = null
    private var cookieInitialized: Boolean = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        checkExistingAuth()
    }

    private fun checkExistingAuth() {
        val hasCredentials = credentialStore.hasCredentials()
        _authState.value = if (hasCredentials) {
            AuthState.Authenticated(requireNotNull(credentialStore.getCredentials()))
        } else {
            AuthState.Unauthenticated
        }

        val currentState = _authState.value
        if (currentState is AuthState.Authenticated) {
            Log.d(TAG, "Initial auth state is Authenticated, initializing client synchronously")
            initializeHttpClient(currentState.credentials)
        }

        scope.launch {
            authState.collect { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        Log.d(TAG, "Auth state changed to Authenticated, initializing client")
                        initializeHttpClient(state.credentials)
                    }

                    is AuthState.Unauthenticated -> {
                        Log.d(TAG, "Auth state changed to Unauthenticated, clearing client")
                        clearHttpClient()
                    }

                    is AuthState.Initializing -> {
                        Log.d(TAG, "Auth state is Initializing")
                    }
                }
            }
        }
    }

    fun saveCredentials(credentials: Credentials) {
        credentialStore.saveCredentials(credentials)
        _authState.value = AuthState.Authenticated(credentials)
    }

    fun logout() {
        credentialStore.clearCredentials()
        _authState.value = AuthState.Unauthenticated
    }

    fun getCredentials(): Credentials? {
        return credentialStore.getCredentials()
    }

    fun getClient(): HttpClient? = httpClient

    private fun initializeHttpClient(credentials: Credentials) {
        if (currentCredentials == credentials && httpClient != null && cookieInitialized) {
            Log.d(TAG, "Client already initialized with same credentials, skipping")
            return
        }

        Log.d(
            TAG,
            "Initializing client for server: ${credentials.serverURL}, user: ${credentials.loginName}"
        )
        currentCredentials = credentials
        cookieInitialized = false
        httpClient?.close()
        httpClient = createHttpClient(credentials)

        runBlocking(Dispatchers.IO) {
            try {
                initCookie(credentials)
                cookieInitialized = true
                Log.d(TAG, "Cookie initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize cookie: ${e.message}", e)
            }
        }
        Log.d(TAG, "HTTP client initialized successfully")
    }

    private suspend fun initCookie(credentials: Credentials) {
        val client = httpClient ?: return
        val baseUrl = credentials.serverURL.trimEnd('/')

        Log.d(TAG, "Initializing cookie via login request to $baseUrl/login")

        val response: HttpResponse = client.post("$baseUrl/login") {
            headers {
                append("Origin", baseUrl)
            }
            setBody(
                FormDataContent(
                    parameters {
                        append("user", credentials.loginName)
                        append("password", credentials.appPassword)
                        append("rememberme", "1")
                    }
                )
            )
        }

        Log.d(TAG, "Login response status: ${response.status}")
    }

    private fun createHttpClient(credentials: Credentials): HttpClient {
        Log.d(TAG, "Creating new HTTP client")
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

    private fun clearHttpClient() {
        Log.d(TAG, "Clearing HTTP client")
        httpClient?.close()
        httpClient = null
        currentCredentials = null
        cookieInitialized = false
    }
}
