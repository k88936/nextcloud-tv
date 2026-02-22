package top.k88936.nextcloud.app

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.xml.xml
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.nextcloud_tv.data.network.INextcloudClient
import top.k88936.nextcloud_tv.data.repository.FilesRepository

class TestNextcloudClient(
    private val client: HttpClient,
    private val credentials: Credentials
) : INextcloudClient {
    override fun getClient(): HttpClient = client
    override fun getCredentials(): Credentials = credentials
}

class FilesRepositoryTest : ShouldSpec() {
    private val client = HttpClient {
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(
                        CredentialMock.loginName,
                        CredentialMock.appPassword
                    )
                }
            }
        }
        install(ContentNegotiation) {
            xml(XML {
                xmlDeclMode = XmlDeclMode.Charset
            })
        }
    }

    private val credentials = Credentials(
        serverUrl = CredentialMock.serverURL,
        loginName = CredentialMock.loginName,
        appPassword = CredentialMock.appPassword
    )

    private val nextcloudClient = TestNextcloudClient(client, credentials)
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
