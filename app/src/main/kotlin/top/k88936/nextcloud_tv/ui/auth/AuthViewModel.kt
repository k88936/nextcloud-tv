package top.k88936.nextcloud_tv.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.k88936.nextcloud.auth.LoginFlowV2
import top.k88936.nextcloud.auth.PollResponse


data class AuthState(
    val step: AuthStep = AuthStep.SERVER_INPUT,
    val serverUrl: String = "https://ivo.lv.tab.digital",
    val loginUrl: String? = null,
    val pollToken: String? = null,
    val pollEndpoint: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val authResult: PollResponse? = null
)

enum class AuthStep {
    SERVER_INPUT,
    QR_CODE
}

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val TAG = "AUTH"

    fun updateServerUrl(url: String) {
        Log.d(TAG, "updateServerUrl: $url")
        _state.value = _state.value.copy(serverUrl = url, error = null)
    }

    fun initiateLogin() {
        val serverUrl = _state.value.serverUrl.trim()
        if (serverUrl.isBlank()) {
            Log.w(TAG, "initiateLogin: empty server URL")
            _state.value = _state.value.copy(error = "Please enter a server URL")
            return
        }

        Log.d(TAG, "initiateLogin: starting with serverUrl=$serverUrl")
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = LoginFlowV2.initiateLogin(serverUrl)

            result.fold(
                onSuccess = { response ->
                    Log.d(TAG, "initiateLogin: success, loginUrl=${response.login}")
                    _state.value = _state.value.copy(
                        step = AuthStep.QR_CODE,
                        loginUrl = response.login,
                        pollToken = response.poll.token,
                        pollEndpoint = response.poll.endpoint,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    Log.e(TAG, "initiateLogin error: ${error.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Failed to connect: ${error.message}"
                    )
                }
            )
        }
    }

    fun pollOnce() {
        val token = _state.value.pollToken ?: return
        val endpoint = _state.value.pollEndpoint ?: return

        Log.d(TAG, "pollOnce: token=$token, endpoint=$endpoint")
        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = LoginFlowV2.pollForAuth(token, endpoint)

            result.fold(
                onSuccess = { response ->
                    if (response != null) {
                        Log.d(TAG, "pollOnce: auth successful, server=${response.server}")
                        _state.value = _state.value.copy(
                            authResult = response,
                            isLoading = false
                        )
                    } else {
                        Log.w(TAG, "pollOnce: no response yet")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Authentication not completed yet. Please try again after scanning."
                        )
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "pollOnce: polling failed", error)
                    _state.value = _state.value.copy(
                        error = "Polling failed: ${error.message}",
                        isLoading = false
                    )
                }
            )
        }
    }

    fun goBack() {
        Log.d(TAG, "goBack: going back to server input")
        _state.value = _state.value.copy(
            step = AuthStep.SERVER_INPUT,
            loginUrl = null,
            pollToken = null,
            pollEndpoint = null,
            isLoading = false,
            error = null,
            authResult = null
        )
    }
}
