package top.k88936.nextcloud.app

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import top.k88936.nextcloud.mock.MockCredential
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.local.ICredentialStore
import top.k88936.nextcloud_tv.data.repository.AuthRepository
import top.k88936.nextcloud_tv.data.repository.AuthState
import top.k88936.nextcloud_tv.data.repository.IAuthRepository

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
                single<IAuthRepository> { AuthRepository(get()) }
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
                MockCredential
            )
            val repo = AuthRepository(mockCredentialStore)
            val state = repo.authState.value
            (state is AuthState.Authenticated) shouldBe true
            state as AuthState.Authenticated
            state.credentials.serverURL shouldBe MockCredential.serverURL
            state.credentials.loginName shouldBe MockCredential.loginName
            state.credentials.appPassword shouldBe MockCredential.appPassword
        }

        "saveCredentials should update authState to Authenticated" {
            val repo = AuthRepository(mockCredentialStore)
            repo.saveCredentials(MockCredential)
            val state = repo.authState.value
            (state is AuthState.Authenticated) shouldBe true
            state as AuthState.Authenticated
            state.credentials.serverURL shouldBe MockCredential.serverURL
            state.credentials.loginName shouldBe MockCredential.loginName
            state.credentials.appPassword shouldBe MockCredential.appPassword
        }

        "logout should update authState to Unauthenticated" {
            mockCredentialStore.saveCredentials(
                MockCredential
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
            val expectedCredentials = MockCredential
            mockCredentialStore.saveCredentials(expectedCredentials)
            val repo = AuthRepository(mockCredentialStore)
            repo.getCredentials() shouldBe expectedCredentials
        }
    }
}
