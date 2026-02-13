package top.k88936.nextcloud_tv.ui.auth

import android.app.Activity
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun AuthScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            val viewModel: AuthViewModel = viewModel()
            val state by viewModel.state.collectAsState()

            BackHandler(enabled = state.step == AuthStep.QR_CODE) {
                viewModel.goBack()
            }

            Box(
                modifier = Modifier.padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                when (state.step) {
                    AuthStep.SERVER_INPUT -> ServerInputStep(
                        serverUrl = state.serverUrl,
                        isLoading = state.isLoading,
                        error = state.error,
                        onServerUrlChange = { it: String -> viewModel.updateServerUrl(it) },
                        onInitiateLogin = { viewModel.initiateLogin() },
                        onCancel = { activity?.finish() }
                    )

                    AuthStep.QR_CODE -> QrCodeStep(
                        loginUrl = state.loginUrl,
                        isLoading = state.isLoading,
                        error = state.error,
                        authResult = state.authResult,
                        onPoll = { viewModel.pollOnce() },
                        onCancel = { viewModel.goBack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ServerInputStep(
    serverUrl: String,
    isLoading: Boolean,
    error: String?,
    onServerUrlChange: (String) -> Unit,
    onInitiateLogin: () -> Unit,
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
            text = "Enter your Nextcloud server URL",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = serverUrl,
            onValueChange = onServerUrlChange,
            placeholder = { Text("https://cloud.example.com") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = { if (!isLoading) onInitiateLogin() }
            ),
            singleLine = true
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
                onClick = onInitiateLogin,
                enabled = !isLoading && serverUrl.isNotBlank(),
            ) {
                Text(if (isLoading) "Connecting..." else "Continue")
            }
        }
    }
}

@Composable
private fun QrCodeStep(
    loginUrl: String?,
    isLoading: Boolean,
    error: String?,
    authResult: top.k88936.nextcloud.auth.PollResponse?,
    onPoll: () -> Unit,
    onCancel: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val qrBitmap = remember(loginUrl) {
        loginUrl?.let { generateQrCode(it) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (authResult != null) {
            Text(
                text = "Authentication Successful!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Logged in as: ${authResult.loginName}",
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Server: ${authResult.server}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onCancel) {
                Text("Done")
            }
        } else {
            Text(
                text = "Auth in your browser",
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "QR Code for login",
                    modifier = Modifier.size(256.dp)
                )
            }

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
                    onClick = onPoll,
                    enabled = !isLoading,
                    modifier = Modifier.focusRequester(focusRequester)
                ) {
                    Text(if (isLoading) "Checking..." else "I've finished auth")
                }
            }
        }
    }
}

private fun generateQrCode(content: String): Bitmap {
    val hints = mapOf<EncodeHintType, Any>(
        EncodeHintType.MARGIN to 1,
        EncodeHintType.CHARACTER_SET to "UTF-8"
    )

    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512, hints)

    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap[x, y] =
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }

    return bitmap
}
