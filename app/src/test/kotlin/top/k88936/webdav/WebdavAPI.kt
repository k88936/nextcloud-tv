package top.k88936.webdav

import io.kotlintest.specs.ShouldSpec
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.xml.xml
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import top.k88936.nextcloud.TestCredential

class WebdavAPI : ShouldSpec() {
    private val client = HttpClient {
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(
                        TestCredential.loginName,
                        TestCredential.appPassword
                    )
                }
            }
        }
        install(ContentNegotiation) {
            xml(XML {
                xmlDeclMode = XmlDeclMode.Charset
            })
        }
        defaultRequest {
            header("OCS-APIRequest", "true")
        }
    }

    private val webDavUrl =
        TestCredential.serverURL + "/remote.php/dav/files/${TestCredential.loginName}"

    init {
        should("list root folder") {
            val result = DavAPI.listFolder(client, webDavUrl, "/")
            result.fold(
                onSuccess = { resources ->
                    println("Found ${resources.size} items:")
                    println(resources)
                },
                onFailure = { error ->
                    println("Error: ${error.message}")
                    error.printStackTrace()
                }
            )
        }
    }
}
