package top.k88936.nextcloud.app

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import top.k88936.nextcloud.TestCredential
import top.k88936.nextcloud.auth.PollResponse
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.network.NextcloudClient
import top.k88936.nextcloud_tv.data.repository.AuthState
import top.k88936.nextcloud_tv.data.repository.FilesRepository
import top.k88936.nextcloud_tv.data.repository.IAuthRepository

class TestAuthRepository(
    private val credentials: Credentials
) : IAuthRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Authenticated(credentials))
    override val authState: StateFlow<AuthState> = _authState

    override fun saveAuth(response: PollResponse) {}
    override fun logout() {}
    override fun getCredentials(): Credentials = credentials
}

class FilesRepositoryTest : ShouldSpec() {
    private val credentials = TestCredential

    private val authRepository = TestAuthRepository(credentials)
    private val nextcloudClient = NextcloudClient(authRepository)
    private val repository = FilesRepository(nextcloudClient)

    init {
        should("listFiles returns files from root directory") {
            val result = repository.listFiles("/")
            result.isSuccess shouldBe true
            val files = result.getOrThrow()
            println("Found ${files.size} items in root:")
            files.forEach { println("  - ${it.name} (${if (it.isDirectory) "dir" else "${it.size} bytes"})") }
            files.isNotEmpty() shouldBe true
        }

        should("listFiles returns files from subdirectory") {
            val rootResult = repository.listFiles("/")
            rootResult.isSuccess shouldBe true
            val rootFiles = rootResult.getOrThrow()
            val dir = rootFiles.find { it.isDirectory }
            if (dir != null) {
                println("Listing directory: ${dir.path}")
                val result = repository.listFiles(dir.path)
                result.isSuccess shouldBe true
                val files = result.getOrThrow()
                println("Found ${files.size} items in ${dir.name}")
            }
        }

        should("getPreview returns image bytes for existing file") {
            val rootResult = repository.listFiles("/")
            rootResult.isSuccess shouldBe true
            val files = rootResult.getOrThrow()
            val imageFile = files.find {
                !it.isDirectory && (it.contentType?.startsWith("image/") == true || it.name.endsWith(
                    ".jpg",
                    true
                ) || it.name.endsWith(".png", true))
            }
            if (imageFile != null) {
                println("Getting preview for: ${imageFile.path}")
                val result = repository.getPreview(imageFile.path, x = 64, y = 64)
                result.isSuccess shouldBe true
                val bytes = result.getOrThrow()
                println("Preview size: ${bytes.size} bytes")
                bytes.isNotEmpty() shouldBe true
            } else {
                println("No image file found, skipping preview test")
            }
        }

        should("getFileContent returns bytes for existing file") {
            val rootResult = repository.listFiles("/")
            rootResult.isSuccess shouldBe true
            val files = rootResult.getOrThrow()
            val textFile = files.find {
                !it.isDirectory && (it.contentType?.startsWith("text/") == true || it.name.endsWith(
                    ".txt",
                    true
                ) || it.name.endsWith(".md", true))
            }
            if (textFile != null) {
                println("Getting content for: ${textFile.path}")
                val result = repository.getFileContent(textFile)
                result.isSuccess shouldBe true
                val bytes = result.getOrThrow()
                println("Content size: ${bytes.size} bytes")
                bytes.isNotEmpty() shouldBe true
            } else {
                println("No text file found, skipping content test")
            }
        }

        should("getFileContentByUrl returns bytes from direct URL") {
            val rootResult = repository.listFiles("/")
            rootResult.isSuccess shouldBe true
            val files = rootResult.getOrThrow()
            val file = files.find { !it.isDirectory }
            if (file != null) {
                println("Getting content by URL: ${file.url}")
                val result = repository.getFileContentByUrl(file.url)
                result.isSuccess shouldBe true
                val bytes = result.getOrThrow()
                println("Content size: ${bytes.size} bytes")
                bytes.isNotEmpty() shouldBe true
            } else {
                println("No file found, skipping URL content test")
            }
        }
    }
}
