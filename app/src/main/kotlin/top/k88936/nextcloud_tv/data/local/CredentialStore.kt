package top.k88936.nextcloud_tv.data.local

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

data class Credentials(
    val serverURL: String,
    val loginName: String,
    val appPassword: String
)

interface ICredentialStore {
    fun saveCredentials(credentials: Credentials)
    fun getCredentials(): Credentials?
    fun hasCredentials(): Boolean
    fun clearCredentials()
}

class CredentialStore(context: Context) : ICredentialStore {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun saveCredentials(credentials: Credentials) {
        sharedPreferences.edit {
            putString(KEY_SERVER_URL, credentials.serverURL)
                .putString(KEY_LOGIN_NAME, credentials.loginName)
                .putString(KEY_APP_PASSWORD, credentials.appPassword)
        }
    }

    override fun getCredentials(): Credentials? {
        val serverUrl = sharedPreferences.getString(KEY_SERVER_URL, null) ?: return null
        val loginName = sharedPreferences.getString(KEY_LOGIN_NAME, null) ?: return null
        val appPassword = sharedPreferences.getString(KEY_APP_PASSWORD, null) ?: return null
        return Credentials(serverUrl, loginName, appPassword)
    }

    override fun hasCredentials(): Boolean {
        return sharedPreferences.contains(KEY_SERVER_URL) &&
                sharedPreferences.contains(KEY_LOGIN_NAME) &&
                sharedPreferences.contains(KEY_APP_PASSWORD)
    }

    override fun clearCredentials() {
        sharedPreferences.edit()
            .remove(KEY_SERVER_URL)
            .remove(KEY_LOGIN_NAME)
            .remove(KEY_APP_PASSWORD)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "nextcloud_tv_credentials"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_LOGIN_NAME = "login_name"
        private const val KEY_APP_PASSWORD = "app_password"
    }
}
