package top.k88936.nextcloud_tv.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.repository.ClientRepository
import top.k88936.nextcloud_tv.data.repository.LoginResult

data class AuthUiState(
    val serverUrl: String = "https://",
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val authSuccess: Boolean = false
)

class AuthViewModel(
    private val clientRepository: ClientRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

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

            val credentials = Credentials(
                serverURL = serverUrl,
                loginName = username,
                appPassword = password
            )

            when (val result = clientRepository.login(credentials)) {
                is LoginResult.Success -> {
                    Log.d(TAG, "Login successful")
                    _state.value = _state.value.copy(authSuccess = true, isLoading = false)
                }

                is LoginResult.Error -> {
                    Log.w(TAG, "Login failed: ${result.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}
