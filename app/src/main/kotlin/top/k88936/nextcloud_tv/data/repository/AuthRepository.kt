package top.k88936.nextcloud_tv.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.k88936.nextcloud.auth.PollResponse
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.local.ICredentialStore

class AuthRepository(
    private val credentialStore: ICredentialStore
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

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
    }

    fun saveAuth(response: PollResponse) {
        val credentials = Credentials(
            serverUrl = response.server,
            loginName = response.loginName,
            appPassword = response.appPassword
        )
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
}

sealed class AuthState {
    data object Initializing : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val credentials: Credentials) : AuthState()
}
