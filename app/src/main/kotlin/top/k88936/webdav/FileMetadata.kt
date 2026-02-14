package top.k88936.webdav

data class FileMetadata(
    val url: String,
    val fileName: String?,
    val lastModified: String,
    val size: Long?, // in bit
    val contentType: String?,
    val isDirectory: Boolean
)