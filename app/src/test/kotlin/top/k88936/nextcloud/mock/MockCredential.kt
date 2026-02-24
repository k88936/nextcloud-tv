package top.k88936.nextcloud.mock

import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.local.ICredentialStore


val MockCredential: Credentials = Credentials(
    serverURL = "https://nextcloud.k88936.top",
    loginName = "test",
    appPassword = "test"
//    appPassword = "s8WoR-gXNef-yp8SF-zyDJW-H4Z2N",

//    val serverURL ="https://ivo.lv.tab.digital"
//    val loginName="t11"
//    val appPassword="UbwEsuMydRIDnucfTHtLYB5jzlJVppZK7aIwR2GJ30SttWJmUDev6WO1q033jAbAHncm7dz9"
)

class MockCredentialStore(
) : ICredentialStore {
    private var storedCredentials: Credentials = MockCredential

    override fun hasCredentials(): Boolean = true

    override fun getCredentials(): Credentials = storedCredentials

    override fun saveCredentials(credentials: Credentials) {
    }

    override fun clearCredentials() {
    }
}
