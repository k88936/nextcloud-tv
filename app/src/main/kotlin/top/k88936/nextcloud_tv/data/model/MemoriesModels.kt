package top.k88936.nextcloud_tv.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Day(
    val dayid: Int,
    val count: Int,
    val detail: List<Photo>? = null,
    @SerialName("haslocal")
    private val _hasLocal: Int? = null
) {
    val hasLocal: Boolean get() = _hasLocal == 1
}

@Serializable
data class Photo(
    val fileid: Int,
    val dayid: Int? = null,
    val etag: String? = null,
    val basename: String? = null,
    val mimetype: String? = null,
    val w: Int? = null,
    val h: Int? = null,
    @SerialName("isvideo")
    private val _isVideo: Int? = null,
    @SerialName("video_duration")
    val videoDuration: Int? = null,
    @SerialName("isfavorite")
    private val _isFavorite: Int? = null,
    @SerialName("islocal")
    private val _isLocal: Int? = null,
    @SerialName("ishidden")
    private val _isHidden: Int? = null,
    val liveid: String? = null,
    val shared_by: String? = null,
    val faceid: Int? = null,
    val facerect: FaceRect? = null,
    val auid: String? = null,
    val buid: String? = null,
    val epoch: Long? = null
) {
    var flag: Int = 0

    val isVideo: Boolean get() = _isVideo == 1
    val isFavorite: Boolean get() = _isFavorite == 1
    val isLocal: Boolean get() = _isLocal == 1
    val isHidden: Boolean get() = _isHidden == 1

    companion object {
        const val FLAG_PLACEHOLDER = 1 shl 0
        const val FLAG_LOAD_FAIL = 1 shl 1
        const val FLAG_IS_VIDEO = 1 shl 2
        const val FLAG_IS_FAVORITE = 1 shl 3
        const val FLAG_SELECTED = 1 shl 4
        const val FLAG_LEAVING = 1 shl 5
        const val FLAG_IS_LOCAL = 1 shl 6
    }
}

fun Photo.convertFlags(): Photo {
    if (isVideo) flag = flag or Photo.FLAG_IS_VIDEO
    if (isFavorite) flag = flag or Photo.FLAG_IS_FAVORITE
    if (isLocal) flag = flag or Photo.FLAG_IS_LOCAL
    return this
}

@Serializable
data class FaceRect(
    val w: Float,
    val h: Float,
    val x: Float,
    val y: Float
)

enum class DaysFilterType(val key: String) {
    FAVORITES("fav"),
    VIDEOS("vid"),
    FOLDER("folder"),
    ARCHIVE("archive"),
    ALBUM("albums"),
    RECOGNIZE("recognize"),
    FACERECOGNITION("facerecognition"),
    PLACE("places"),
    TAG("tags"),
    MAP_BOUNDS("mapbounds"),
    FACE_RECT("facerect"),
    RECURSIVE("recursive"),
    MONTH_VIEW("monthView"),
    REVERSE("reverse"),
    HIDDEN("hidden"),
    NO_PRELOAD("nopreload")
}

@Serializable
data class ImageInfo(
    val fileid: Int,
    val etag: String,
    val h: Int,
    val w: Int,
    val datetaken: Long? = null,
    val permissions: String? = null,
    val basename: String? = null,
    val mimetype: String? = null,
    val size: Long? = null,
    val mtime: Long? = null,
    val owneruid: String? = null,
    val ownername: String? = null,
    val filename: String? = null,
    val address: String? = null,
    val tags: Map<String, String>? = null,
    val exif: ExifInfo? = null,
    val clusters: ImageClusters? = null
)

@Serializable
data class ExifInfo(
    val Rotation: Int? = null,
    val Orientation: Int? = null,
    val ImageWidth: Int? = null,
    val ImageHeight: Int? = null,
    val Megapixels: Double? = null,
    val Title: String? = null,
    val Description: String? = null,
    val Make: String? = null,
    val Model: String? = null,
    val CreateDate: String? = null,
    val DateTimeOriginal: String? = null,
    val DateTimeEpoch: Long? = null,
    val OffsetTimeOriginal: String? = null,
    val OffsetTime: String? = null,
    val LocationTZID: String? = null,
    val ExposureTime: Double? = null,
    val ShutterSpeed: Double? = null,
    val ShutterSpeedValue: Double? = null,
    val Aperture: Double? = null,
    val ApertureValue: Double? = null,
    val ISO: Int? = null,
    val FNumber: Double? = null,
    val FocalLength: Double? = null,
    val GPSAltitude: Double? = null,
    val GPSLatitude: Double? = null,
    val GPSLongitude: Double? = null
)

@Serializable
data class ImageClusters(
    val albums: List<AlbumCluster>? = null,
    val recognize: List<FaceCluster>? = null,
    val facerecognition: List<FaceCluster>? = null
)

@Serializable
data class AlbumCluster(
    val cluster_id: Long,
    val cluster_type: String,
    val count: Int,
    val name: String,
    val album_id: Long? = null,
    val user: String? = null,
    val user_display: String? = null,
    val created: Long? = null,
    val location: String? = null,
    val last_added_photo: Int? = null,
    val last_added_photo_etag: String? = null,
    val update_id: Long? = null,
    val shared: Boolean? = null
)

@Serializable
data class FaceCluster(
    val cluster_id: Long,
    val cluster_type: String,
    val count: Int,
    val name: String,
    val user_id: String? = null
)
