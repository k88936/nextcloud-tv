package top.k88936.nextcloud.app

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import kotlinx.coroutines.runBlocking
import top.k88936.nextcloud.mock.MockCredentialStore
import top.k88936.nextcloud_tv.data.repository.ClientRepository
import top.k88936.nextcloud_tv.data.repository.FilesRepository
import top.k88936.nextcloud_tv.data.repository.LoginResult

class FilesRepositoryTest : ShouldSpec() {
    private val credentialStore = MockCredentialStore()
    private val clientRepository = ClientRepository(credentialStore)
    private val repository = FilesRepository(clientRepository)

    init {
        runBlocking {
            val result = clientRepository.login()
            if (result is LoginResult.Error) {
                println("Login failed: ${result.message}")
            }
        }

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
