package top.k88936.nextcloud_tv.data.repository

import kotlinx.coroutines.flow.StateFlow
import top.k88936.nextcloud_tv.data.local.Credentials

interface IAuthRepository {
    val authState: StateFlow<AuthState>
    fun saveCredentials(credentials: Credentials)
    fun logout()
    fun getCredentials(): Credentials?
}
