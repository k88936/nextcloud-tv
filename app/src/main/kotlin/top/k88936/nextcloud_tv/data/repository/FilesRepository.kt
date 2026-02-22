package top.k88936.nextcloud_tv.data.repository

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.set
import top.k88936.nextcloud_tv.data.network.INextcloudClient
import top.k88936.webdav.DavAPI
import top.k88936.webdav.FileMetadata

data class FilesState(
    val files: List<FileMetadata> = emptyList(),
    val currentPath: String = "/",
    val isLoading: Boolean = false,
    val error: String? = null,
    val focusedFilePath: String? = null
)

class FilesRepository(
    private val nextcloudClient: INextcloudClient
) {
    private companion object {
        private const val TAG = "FilesRepository"
    }

    suspend fun listFiles(path: String = "/"): Result<List<FileMetadata>> {
        Log.d(TAG, "listFiles: path=$path")
        val client = nextcloudClient.getClient()
            ?: run {
                Log.w(TAG, "listFiles: not authenticated (no client)")
                return Result.failure(IllegalStateException("Not authenticated"))
            }
        val credentials = nextcloudClient.getCredentials()
            ?: run {
                Log.w(TAG, "listFiles: not authenticated (no credentials)")
                return Result.failure(IllegalStateException("Not authenticated"))
            }
        val baseUrl = URLBuilder(credentials.serverUrl).apply {
            set(path = "/remote.php/dav/files/${credentials.loginName}")
        }.buildString()
        Log.d(TAG, "listFiles: calling DavAPI.listFolder with baseUrl=$baseUrl, path=$path")
        return DavAPI.listFolder(client, baseUrl, path).also { result ->
            result.fold(
                onSuccess = { files ->
                    Log.d(
                        TAG,
                        "listFiles: success, found ${files.size} items"
                    )
                },
                onFailure = { error -> Log.e(TAG, "listFiles: failed - ${error.message}", error) }
            )
        }
    }

    suspend fun getPreview(
        file: String = "",
        x: Long = 32,
        y: Long = 32,
        a: Int = 0,
        forceIcon: Int = 1,
        mode: String = "fill"
    ): Result<ByteArray> {
        Log.d(TAG, "getPreview: file=$file, x=$x, y=$y, mode=$mode")
        val client = nextcloudClient.getClient()
            ?: run {
                Log.w(TAG, "getPreview: not authenticated (no client)")
                return Result.failure(IllegalStateException("Not authenticated"))
            }
        val credentials = nextcloudClient.getCredentials()
            ?: run {
                Log.w(TAG, "getPreview: not authenticated (no credentials)")
                return Result.failure(IllegalStateException("Not authenticated"))
            }
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
            Log.d(TAG, "getPreview: requesting $url")
            val response = client.get(url)
            val bytes = response.body<ByteArray>()
            Log.d(TAG, "getPreview: success, received ${bytes.size} bytes")
            bytes
        }.onFailure { error ->
            Log.e(TAG, "getPreview: failed - ${error.message}", error)
        }
    }

    suspend fun getFileContent(file: FileMetadata): Result<ByteArray> {
        Log.d(TAG, "getFileContent: file=${file.path}, url=${file.url}")
        val client = nextcloudClient.getClient()
            ?: run {
                Log.w(TAG, "getFileContent: not authenticated (no client)")
                return Result.failure(IllegalStateException("Not authenticated"))
            }
        return runCatching {
            val response = client.get(file.url)
            val bytes = response.body<ByteArray>()
            Log.d(TAG, "getFileContent: success, received ${bytes.size} bytes")
            bytes
        }.onFailure { error ->
            Log.e(TAG, "getFileContent: failed - ${error.message}", error)
        }
    }

    suspend fun getFileContentByUrl(url: String): Result<ByteArray> {
        Log.d(TAG, "getFileContentByUrl: url=$url")
        val client = nextcloudClient.getClient()
            ?: run {
                Log.w(TAG, "getFileContentByUrl: not authenticated (no client)")
                return Result.failure(IllegalStateException("Not authenticated"))
            }
        return runCatching {
            val response = client.get(url)
            val bytes = response.body<ByteArray>()
            Log.d(TAG, "getFileContentByUrl: success, received ${bytes.size} bytes")
            bytes
        }.onFailure { error ->
            Log.e(TAG, "getFileContentByUrl: failed - ${error.message}", error)
        }
    }
}
