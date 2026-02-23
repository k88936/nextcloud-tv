package top.k88936.nextcloud.mock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.repository.AuthState
import top.k88936.nextcloud_tv.data.repository.IAuthRepository

class MockAuthRepository(
    private val credentials: Credentials
) : IAuthRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Authenticated(credentials))
    override val authState: StateFlow<AuthState> = _authState

    override fun saveCredentials(credentials: Credentials) {}
    override fun logout() {}
    override fun getCredentials(): Credentials = credentials
}