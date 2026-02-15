package top.k88936.webdav

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.set
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.net.URLDecoder

@Serializable
@XmlSerialName("response", "DAV:", "d")
private data class DavItem(
    @XmlElement(true)
    @XmlSerialName("href", "DAV:", "d")
    val href: String,
    @XmlElement(true)
    @XmlSerialName("propstat", "DAV:", "d")
    val propstat: PropStat
)

@Serializable
@XmlSerialName("propstat", "DAV:", "d")
private data class PropStat(
    @XmlElement(true)
    @XmlSerialName("prop", "DAV:", "d")
    val prop: Prop,
    @XmlElement(true)
    @XmlSerialName("status", "DAV:", "d")
    val status: String
)

@Serializable
@XmlSerialName("prop", "DAV:", "d")
private data class Prop(
    @XmlElement(true)
    @XmlSerialName("getlastmodified", "DAV:", "d")
    val getlastmodified: String,
    @XmlElement(true)
    @XmlSerialName("getcontentlength", "DAV:", "d")
    val getcontentlength: Long? = null,
    @XmlElement(true)
    @XmlSerialName("resourcetype", "DAV:", "d")
    val resourcetype: ResourceType,
    @XmlElement(true)
    @XmlSerialName("getetag", "DAV:", "d")
    val getetag: String,
    @XmlElement(true)
    @XmlSerialName("getcontenttype", "DAV:", "d")
    val getcontenttype: String? = null,
    @XmlElement(true)
    @XmlSerialName("quota-used-bytes", "DAV:", "d")
    val quotaUsedBytes: Long? = null,
    @XmlElement(true)
    @XmlSerialName("quota-available-bytes", "DAV:", "d")
    val quotaAvailableBytes: Long? = null
)

@Serializable
@XmlSerialName("resourcetype", "DAV:", "d")
data class ResourceType(
    @XmlElement(true)
    @XmlSerialName("collection", "DAV:", "d")
    val collection: Unit? = null
) {
}

object DavAPI {
    private val PropFindMethod = HttpMethod("PROPFIND")

    suspend fun listFolder(
        client: HttpClient,
        serverUrl: String,
        path: String = "/"
    ): Result<List<FileMetadata>> {

        val basePath = serverUrl.trimEnd('/')
        val subPath = path.trimStart('/')
        val url = "$basePath/$subPath"


        return runCatching {
            val response: HttpResponse = client.request(url) {
                method = PropFindMethod
                header("Depth", "1")
                contentType(ContentType.Application.Xml)
            }
            response.body<List<DavItem>>().map { it ->
                val itsURL = URLBuilder(serverUrl).apply {
                    set(path = it.href)
                }.buildString()
                FileMetadata(
                    url = itsURL,
                    lastModified = it.propstat.prop.getlastmodified,
                    name = URLDecoder.decode(itsURL.substringAfter(url).trimStart('/'), "UTF-8"),
                    path = itsURL.substringAfter(basePath),
                    size = it.propstat.prop.getcontentlength,
                    contentType = it.propstat.prop.getcontenttype,
                    isDirectory = it.propstat.prop.resourcetype.collection != null
                )
            }
        }
    }


}