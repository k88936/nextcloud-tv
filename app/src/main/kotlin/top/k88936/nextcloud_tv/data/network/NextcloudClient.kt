package top.k88936.nextcloud_tv.data.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.xml.xml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.repository.AuthRepository
import top.k88936.nextcloud_tv.data.repository.AuthState

class NextcloudClient(
    private val authRepository: AuthRepository
) {
    private companion object {
        private const val TAG = "AuthenticatedHttpClient"
    }

    private var httpClient: HttpClient? = null
    private var currentCredentials: Credentials? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        val currentState = authRepository.authState.value
        if (currentState is AuthState.Authenticated) {
            Log.d(TAG, "Initial auth state is Authenticated, initializing client synchronously")
            initialize(currentState.credentials)
        }

        scope.launch {
            authRepository.authState.collectLatest { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        Log.d(TAG, "Auth state changed to Authenticated, initializing client")
                        initialize(state.credentials)
                    }

                    is AuthState.Unauthenticated -> {
                        Log.d(TAG, "Auth state changed to Unauthenticated, clearing client")
                        clear()
                    }

                    is AuthState.Initializing -> {
                        Log.d(TAG, "Auth state is Initializing")
                    }
                }
            }
        }
    }

    private fun initialize(credentials: Credentials) {
        if (currentCredentials == credentials && httpClient != null) {
            Log.d(TAG, "Client already initialized with same credentials, skipping")
            return
        }

        Log.d(
            TAG,
            "Initializing client for server: ${credentials.serverUrl}, user: ${credentials.loginName}"
        )
        currentCredentials = credentials
        httpClient?.close()
        httpClient = createHttpClient(credentials)
        Log.d(TAG, "HTTP client initialized successfully")
    }

    private fun createHttpClient(credentials: Credentials): HttpClient {
        Log.d(TAG, "Creating new HTTP client")
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
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
        }
    }

    fun getClient(): HttpClient? = httpClient
    fun getCredentials(): Credentials? = currentCredentials

    private fun clear() {
        Log.d(TAG, "Clearing HTTP client")
        httpClient?.close()
        httpClient = null
        currentCredentials = null
    }
}
