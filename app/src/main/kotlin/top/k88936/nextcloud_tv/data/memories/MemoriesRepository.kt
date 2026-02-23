package top.k88936.nextcloud_tv.data.memories

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.set
import kotlinx.serialization.Serializable
import top.k88936.nextcloud_tv.data.network.NextcloudClient

data class MemoriesState(
    val days: List<Day> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MemoriesRepository(
    private val nextcloudClient: NextcloudClient
) {
    private companion object {
        private const val TAG = "MemoriesRepository"
        private const val BASE_PATH = "/apps/memories/api"
    }

    suspend fun getDays(filters: Map<String, String> = emptyMap()): Result<List<Day>> {
        Log.d(TAG, "getDays: filters=$filters")
        val client = nextcloudClient.getClient()
            ?: return Result.failure(IllegalStateException("Not authenticated"))
        val credentials = nextcloudClient.getCredentials()
            ?: return Result.failure(IllegalStateException("Not authenticated"))

        return runCatching {
            val url = URLBuilder(credentials.serverURL).apply {
                set(path = "$BASE_PATH/days")
                filters.forEach { (key, value) ->
                    parameters.append(key, value)
                }
            }.buildString()

            Log.d(TAG, "getDays: requesting $url")
            val response = client.get(url)
            val days = response.body<List<Day>>()
            Log.d(TAG, "getDays: success, found ${days.size} days")
            days
        }.onFailure { error ->
            Log.e(TAG, "getDays: failed - ${error.message}", error)
        }
    }

    suspend fun getDay(
        dayIds: List<Int>,
        filters: Map<String, String> = emptyMap()
    ): Result<List<Photo>> {
        Log.d(TAG, "getDay: dayIds=$dayIds, filters=$filters")
        val client = nextcloudClient.getClient()
            ?: return Result.failure(IllegalStateException("Not authenticated"))
        val credentials = nextcloudClient.getCredentials()
            ?: return Result.failure(IllegalStateException("Not authenticated"))

        return runCatching {
            val url = URLBuilder(credentials.serverURL).apply {
                set(path = "$BASE_PATH/days/${dayIds.joinToString(",")}")
                filters.forEach { (key, value) ->
                    parameters.append(key, value)
                }
            }.buildString()

            Log.d(TAG, "getDay: requesting $url")
            val response = client.get(url)
            val photos = response.body<List<Photo>>().map { it.convertFlags() }
            Log.d(TAG, "getDay: success, found ${photos.size} photos")
            photos
        }.onFailure { error ->
            Log.e(TAG, "getDay: failed - ${error.message}", error)
        }
    }

    suspend fun getPreview(fileid: Int, etag: String? = null): Result<ByteArray> {
        Log.d(TAG, "getPreview: fileid=$fileid, etag=$etag")
        val client = nextcloudClient.getClient()
            ?: return Result.failure(IllegalStateException("Not authenticated"))
        val credentials = nextcloudClient.getCredentials()
            ?: return Result.failure(IllegalStateException("Not authenticated"))

        return runCatching {
            val url = URLBuilder(credentials.serverURL).apply {
                set(path = "$BASE_PATH/image/preview/$fileid")
                etag?.let { parameters.append("etag", it) }
            }.buildString()

            Log.d(TAG, "getPreview: requesting $url")
            val response = client.get(url)
            val bytes = response.bodyAsBytes()
            Log.d(TAG, "getPreview: success, received ${bytes.size} bytes")
            bytes
        }.onFailure { error ->
            Log.e(TAG, "getPreview: failed - ${error.message}", error)
        }
    }

    suspend fun getMultiPreview(fileids: List<Int>): Result<ByteArray> {
        Log.d(TAG, "getMultiPreview: fileids=${fileids.size}")
        val client = nextcloudClient.getClient()
            ?: return Result.failure(IllegalStateException("Not authenticated"))
        val credentials = nextcloudClient.getCredentials()
            ?: return Result.failure(IllegalStateException("Not authenticated"))

        return runCatching {
            val url = URLBuilder(credentials.serverURL).apply {
                set(path = "$BASE_PATH/image/multipreview")
            }.buildString()

            @Serializable
            data class MultiPreviewRequest(val fileids: List<Int>)

            Log.d(TAG, "getMultiPreview: requesting $url")
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(MultiPreviewRequest(fileids))
            }
            val bytes = response.bodyAsBytes()
            Log.d(TAG, "getMultiPreview: success, received ${bytes.size} bytes")
            bytes
        }.onFailure { error ->
            Log.e(TAG, "getMultiPreview: failed - ${error.message}", error)
        }
    }

    suspend fun getImageInfo(fileid: Int): Result<ImageInfo> {
        Log.d(TAG, "getImageInfo: fileid=$fileid")
        val client = nextcloudClient.getClient()
            ?: return Result.failure(IllegalStateException("Not authenticated"))
        val credentials = nextcloudClient.getCredentials()
            ?: return Result.failure(IllegalStateException("Not authenticated"))

        return runCatching {
            val url = URLBuilder(credentials.serverURL).apply {
                set(path = "$BASE_PATH/image/info/$fileid")
            }.buildString()

            Log.d(TAG, "getImageInfo: requesting $url")
            val response = client.get(url)
            val info = response.body<ImageInfo>()
            Log.d(TAG, "getImageInfo: success for file ${info.basename}")
            info
        }.onFailure { error ->
            Log.e(TAG, "getImageInfo: failed - ${error.message}", error)
        }
    }

    fun buildDaysUrl(
        credentials: top.k88936.nextcloud_tv.data.local.Credentials,
        filters: Map<String, String> = emptyMap()
    ): String {
        return URLBuilder(credentials.serverURL).apply {
            set(path = "$BASE_PATH/days")
            filters.forEach { (key, value) ->
                parameters.append(key, value)
            }
        }.buildString()
    }

    fun buildDayUrl(
        credentials: top.k88936.nextcloud_tv.data.local.Credentials,
        dayIds: List<Int>,
        filters: Map<String, String> = emptyMap()
    ): String {
        return URLBuilder(credentials.serverURL).apply {
            set(path = "$BASE_PATH/days/${dayIds.joinToString(",")}")
            filters.forEach { (key, value) ->
                parameters.append(key, value)
            }
        }.buildString()
    }

    fun buildPreviewUrl(
        credentials: top.k88936.nextcloud_tv.data.local.Credentials,
        fileid: Int,
        etag: String? = null
    ): String {
        return URLBuilder(credentials.serverURL).apply {
            set(path = "$BASE_PATH/image/preview/$fileid")
            etag?.let { parameters.append("etag", it) }
        }.buildString()
    }
}
