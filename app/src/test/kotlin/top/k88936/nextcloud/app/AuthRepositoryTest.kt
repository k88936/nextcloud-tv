package top.k88936.nextcloud.app

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import top.k88936.nextcloud.CredentialMock
import top.k88936.nextcloud.auth.PollResponse
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.local.ICredentialStore
import top.k88936.nextcloud_tv.data.repository.AuthRepository
import top.k88936.nextcloud_tv.data.repository.AuthState

class MockCredentialStore : ICredentialStore {
    private var credentials: Credentials? = null

    override fun hasCredentials(): Boolean = credentials != null

    override fun getCredentials(): Credentials? = credentials

    override fun saveCredentials(credentials: Credentials) {
        this.credentials = credentials
    }

    override fun clearCredentials() {
        credentials = null
    }
}

class AuthRepositoryTest : StringSpec() {

    private lateinit var mockCredentialStore: MockCredentialStore

    override fun beforeTest(testCase: io.kotlintest.TestCase) {
        mockCredentialStore = MockCredentialStore()
        startKoin {
            modules(module {
                single<ICredentialStore> { mockCredentialStore }
                single { AuthRepository(get()) }
            })
        }
    }

    override fun afterTest(testCase: io.kotlintest.TestCase, result: io.kotlintest.TestResult) {
        stopKoin()
    }

    init {
        "initial state should be Unauthenticated when no credentials exist" {
            val repo = AuthRepository(mockCredentialStore)
            repo.authState.value shouldBe AuthState.Unauthenticated
        }

        "initial state should be Authenticated when credentials exist" {
            mockCredentialStore.saveCredentials(
                Credentials(
                    serverUrl = CredentialMock.serverURL,
                    loginName = CredentialMock.loginName,
                    appPassword = CredentialMock.appPassword
                )
            )
            val repo = AuthRepository(mockCredentialStore)
            val state = repo.authState.value
            (state is AuthState.Authenticated) shouldBe true
            state as AuthState.Authenticated
            state.credentials.serverUrl shouldBe CredentialMock.serverURL
            state.credentials.loginName shouldBe CredentialMock.loginName
            state.credentials.appPassword shouldBe CredentialMock.appPassword
        }

        "saveAuth should update authState to Authenticated" {
            val pollResponse = PollResponse(
                server = CredentialMock.serverURL,
                loginName = CredentialMock.loginName,
                appPassword = CredentialMock.appPassword
            )
            val repo = AuthRepository(mockCredentialStore)
            repo.saveAuth(pollResponse)
            val state = repo.authState.value
            (state is AuthState.Authenticated) shouldBe true
            state as AuthState.Authenticated
            state.credentials.serverUrl shouldBe CredentialMock.serverURL
            state.credentials.loginName shouldBe CredentialMock.loginName
            state.credentials.appPassword shouldBe CredentialMock.appPassword
        }

        "logout should update authState to Unauthenticated" {
            mockCredentialStore.saveCredentials(
                Credentials(
                    serverUrl = CredentialMock.serverURL,
                    loginName = CredentialMock.loginName,
                    appPassword = CredentialMock.appPassword
                )
            )
            val repo = AuthRepository(mockCredentialStore)
            repo.logout()
            repo.authState.value shouldBe AuthState.Unauthenticated
            mockCredentialStore.hasCredentials() shouldBe false
        }

        "getCredentials should return null when not authenticated" {
            val repo = AuthRepository(mockCredentialStore)
            repo.getCredentials() shouldBe null
        }

        "getCredentials should return credentials when authenticated" {
            val expectedCredentials = Credentials(
                serverUrl = CredentialMock.serverURL,
                loginName = CredentialMock.loginName,
                appPassword = CredentialMock.appPassword
            )
            mockCredentialStore.saveCredentials(expectedCredentials)
            val repo = AuthRepository(mockCredentialStore)
            repo.getCredentials() shouldBe expectedCredentials
        }
    }
}
