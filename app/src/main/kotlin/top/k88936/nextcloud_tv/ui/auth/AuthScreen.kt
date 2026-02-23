package top.k88936.nextcloud_tv.ui.auth

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit = {},
    viewModel: AuthViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.authSuccess) {
        if (state.authSuccess) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .alpha(0.9f)
        ) {
            Box(
                modifier = Modifier.padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CredentialsInputStep(
                    serverUrl = state.serverUrl,
                    username = state.username,
                    password = state.password,
                    isLoading = state.isLoading,
                    error = state.error,
                    onServerUrlChange = { viewModel.updateServerUrl(it) },
                    onUsernameChange = { viewModel.updateUsername(it) },
                    onPasswordChange = { viewModel.updatePassword(it) },
                    onLogin = { viewModel.login() },
                    onCancel = { activity?.finish() }
                )
            }
        }
    }
}

@Composable
private fun CredentialsInputStep(
    serverUrl: String,
    username: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    onServerUrlChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onCancel: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Sign in to Nextcloud",
            color = MaterialTheme.colorScheme.inverseSurface,
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = serverUrl,
            onValueChange = onServerUrlChange,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.inverseSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.inverseSurface
            ),
            singleLine = true,
            placeholder = { Text("Server URL") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(0.8f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.inverseSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.inverseSurface
            ),
            singleLine = true,
            placeholder = { Text("Username") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(0.8f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = { if (!isLoading) onLogin() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.inverseSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.inverseSurface
            ),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text("Password") }
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onCancel,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onLogin,
                enabled = !isLoading && serverUrl.isNotBlank() && username.isNotBlank() && password.isNotBlank(),
            ) {
                Text(if (isLoading) "Signing in..." else "Sign in")
            }
        }
    }
}
