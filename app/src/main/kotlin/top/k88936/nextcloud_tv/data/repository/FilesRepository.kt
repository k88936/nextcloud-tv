package top.k88936.nextcloud_tv.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.set
import io.ktor.serialization.kotlinx.xml.xml
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import top.k88936.nextcloud_tv.data.local.Credentials
import top.k88936.webdav.DavAPI
import top.k88936.webdav.FileMetadata

data class FilesState(
    val files: List<FileMetadata> = emptyList(),
    val currentPath: String = "/",
    val isLoading: Boolean = false,
    val error: String? = null
)

class FilesRepository {

    private var httpClient: HttpClient? = null
    private var currentCredentials: Credentials? = null

    fun initialize(credentials: Credentials) {
        if (currentCredentials == credentials && httpClient != null) return

        currentCredentials = credentials
        httpClient?.close()
        httpClient = createHttpClient(credentials)
    }

    private fun createHttpClient(credentials: Credentials): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                xml(XML {
                    xmlDeclMode = XmlDeclMode.Charset
                })
            }
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = credentials.loginName,
                            password = credentials.appPassword
                        )
                    }
                }
            }
        }
    }

    suspend fun listFiles(path: String = "/"): Result<List<FileMetadata>> {
        val client = httpClient ?: return Result.failure(IllegalStateException("Not authenticated"))
        val credentials =
            currentCredentials ?: return Result.failure(IllegalStateException("Not authenticated"))
        val baseUrl = URLBuilder(credentials.serverUrl).apply {
            set(path = "/remote.php/dav/files/${credentials.loginName}")
        }.buildString()
        return DavAPI.listFolder(client, baseUrl, path)
    }

    suspend fun getPreview(
        file: String = "",
        x: Long = 32,
        y: Long = 32,
        a: Int = 0,
        forceIcon: Int = 1,
        mode: String = "fill"
    ): Result<ByteArray> {
        val client = httpClient ?: return Result.failure(IllegalStateException("Not authenticated"))
        val credentials =
            currentCredentials ?: return Result.failure(IllegalStateException("Not authenticated"))
        return runCatching {
            val url = URLBuilder(credentials.serverUrl).apply {
                set(path = "/index.php/core/preview.png")
                parameters.append("file", file)
                parameters.append("x", x.toString())
                parameters.append("y", y.toString())
                parameters.append("a", a.toString())
                parameters.append("forceIcon", forceIcon.toString())
                parameters.append("mode", mode)
            }.buildString()
            val response = client.get(url)
            response.body()
        }
    }

    fun getPreviewUrl(
        file: String = "",
        x: Long = 32,
        y: Long = 32,
        a: Int = 0,
        forceIcon: Int = 1,
        mode: String = "fill"
    ): String? {
        val credentials = currentCredentials ?: return null
        return URLBuilder(credentials.serverUrl).apply {
            set(path = "/index.php/core/preview.png")
            parameters.append("file", file)
            parameters.append("x", x.toString())
            parameters.append("y", y.toString())
            parameters.append("a", a.toString())
            parameters.append("forceIcon", forceIcon.toString())
            parameters.append("mode", mode)
        }.buildString()
    }

    fun clear() {
        httpClient?.close()
        httpClient = null
        currentCredentials = null
    }
}
