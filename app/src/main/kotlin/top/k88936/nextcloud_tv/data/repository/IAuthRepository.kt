package top.k88936.nextcloud_tv.data.repository

import kotlinx.coroutines.flow.StateFlow
import top.k88936.nextcloud.auth.PollResponse
import top.k88936.nextcloud_tv.data.local.Credentials

interface IAuthRepository {
    val authState: StateFlow<AuthState>
    fun saveAuth(response: PollResponse)
    fun logout()
    fun getCredentials(): Credentials?
}
