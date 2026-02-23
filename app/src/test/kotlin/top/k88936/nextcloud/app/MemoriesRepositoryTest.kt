package top.k88936.nextcloud.app

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import io.ktor.client.plugins.cookies.cookies
import top.k88936.nextcloud.mock.MockAuthRepository
import top.k88936.nextcloud.mock.MockCredential
import top.k88936.nextcloud_tv.data.model.DaysFilterType
import top.k88936.nextcloud_tv.data.model.Photo
import top.k88936.nextcloud_tv.data.model.convertFlags
import top.k88936.nextcloud_tv.data.network.NextcloudClient
import top.k88936.nextcloud_tv.data.repository.MemoriesRepository

class MemoriesRepositoryTest : ShouldSpec() {
    private val credentials = MockCredential

    private val authRepository =
        MockAuthRepository(credentials)
    private val nextcloudClient = NextcloudClient(authRepository)
    private val repository = MemoriesRepository(nextcloudClient)

    init {
        should("getDays returns list of days") {
            println(nextcloudClient.getClient()?.cookies(credentials.serverURL))
            val result = repository.getDays()
            result.isSuccess shouldBe true
            val days = result.getOrThrow()
            println("Found ${days.size} days:")
            days.take(5).forEach { println("  - Day ${it.dayid}: ${it.count} photos") }
        }

        should("getDays with favorites filter") {
            val result =
                repository.getDays(mapOf(DaysFilterType.FAVORITES.key to "1"))
            result.isSuccess shouldBe true
            val days = result.getOrThrow()
            println("Found ${days.size} days with favorites:")
            days.forEach { println("  - Day ${it.dayid}: ${it.count} photos") }
        }

        should("getDays with videos filter") {
            val result =
                repository.getDays(mapOf(DaysFilterType.VIDEOS.key to "1"))
            result.isSuccess shouldBe true
            val days = result.getOrThrow()
            println("Found ${days.size} days with videos:")
            days.forEach { println("  - Day ${it.dayid}: ${it.count} videos") }
        }

        should("getDay returns photos for specific day") {
            val daysResult = repository.getDays()
            daysResult.isSuccess shouldBe true
            val days = daysResult.getOrThrow()

            if (days.isNotEmpty()) {
                val firstDay = days.first()
                println("Fetching photos for day ${firstDay.dayid}")
                val result = repository.getDay(listOf(firstDay.dayid))
                result.isSuccess shouldBe true
                val photos = result.getOrThrow()
                println("Found ${photos.size} photos:")
                photos.take(5).forEach { photo ->
                    println("  - ${photo.basename} (${photo.w}x${photo.h}, ${photo.mimetype})")
                }
                photos.isNotEmpty() shouldBe true
            } else {
                println("No days found, skipping day fetch test")
            }
        }

        should("getDay returns photos for multiple days") {
            val daysResult = repository.getDays()
            daysResult.isSuccess shouldBe true
            val days = daysResult.getOrThrow()

            if (days.size >= 2) {
                val dayIds = days.take(2).map { it.dayid }
                println("Fetching photos for days: $dayIds")
                val result = repository.getDay(dayIds)
                result.isSuccess shouldBe true
                val photos = result.getOrThrow()
                println("Found ${photos.size} photos from multiple days")
                photos.isNotEmpty() shouldBe true
            } else {
                println("Less than 2 days found, skipping multiple days test")
            }
        }

        should("getPreview returns image bytes") {
            val daysResult = repository.getDays()
            daysResult.isSuccess shouldBe true
            val days = daysResult.getOrThrow()

            if (days.isNotEmpty()) {
                val photosResult = repository.getDay(listOf(days.first().dayid))
                photosResult.isSuccess shouldBe true
                val photos = photosResult.getOrThrow()

                if (photos.isNotEmpty()) {
                    val firstPhoto = photos.first()
                    println("Getting preview for fileid: ${firstPhoto.fileid}")
                    val result = repository.getPreview(firstPhoto.fileid, firstPhoto.etag)
                    result.isSuccess shouldBe true
                    val bytes = result.getOrThrow()
                    println("Preview size: ${bytes.size} bytes")
                    bytes.isNotEmpty() shouldBe true
                } else {
                    println("No photos found, skipping preview test")
                }
            } else {
                println("No days found, skipping preview test")
            }
        }

        should("getImageInfo returns image metadata") {
            val daysResult = repository.getDays()
            daysResult.isSuccess shouldBe true
            val days = daysResult.getOrThrow()

            if (days.isNotEmpty()) {
                val photosResult = repository.getDay(listOf(days.first().dayid))
                photosResult.isSuccess shouldBe true
                val photos = photosResult.getOrThrow()

                if (photos.isNotEmpty()) {
                    val firstPhoto = photos.first()
                    println("Getting info for fileid: ${firstPhoto.fileid}")
                    val result = repository.getImageInfo(firstPhoto.fileid)
                    result.isSuccess shouldBe true
                    val info = result.getOrThrow()
                    println("Image info: ${info.basename}, ${info.w}x${info.h}, ${info.mimetype}")
                    info.fileid shouldBe firstPhoto.fileid
                } else {
                    println("No photos found, skipping image info test")
                }
            } else {
                println("No days found, skipping image info test")
            }
        }

        should("buildDaysUrl creates correct URL") {
            val url = repository.buildDaysUrl(credentials)
            println("Days URL: $url")
            url.contains("/apps/memories/api/days") shouldBe true

            val urlWithFilter = repository.buildDaysUrl(credentials, mapOf("fav" to "1"))
            println("Days URL with filter: $urlWithFilter")
            urlWithFilter.contains("fav=1") shouldBe true
        }

        should("buildDayUrl creates correct URL") {
            val url = repository.buildDayUrl(credentials, listOf(12345, 12346))
            println("Day URL: $url")
            url.contains("/apps/memories/api/days/12345,12346") shouldBe true
        }

        should("buildPreviewUrl creates correct URL") {
            val url = repository.buildPreviewUrl(credentials, 12345)
            println("Preview URL: $url")
            url.contains("/apps/memories/api/image/preview/12345") shouldBe true

            val urlWithEtag = repository.buildPreviewUrl(credentials, 12345, "abc123")
            println("Preview URL with etag: $urlWithEtag")
            urlWithEtag.contains("etag=abc123") shouldBe true
        }

        should("Photo.convertFlags sets correct flags") {
            val photo = Photo(
                fileid = 1,
                _isVideo = 1,
                _isFavorite = 1,
                _isLocal = 1
            ).convertFlags()

            (photo.flag and Photo.Companion.FLAG_IS_VIDEO) shouldBe Photo.Companion.FLAG_IS_VIDEO
            (photo.flag and Photo.Companion.FLAG_IS_FAVORITE) shouldBe Photo.Companion.FLAG_IS_FAVORITE
            (photo.flag and Photo.Companion.FLAG_IS_LOCAL) shouldBe Photo.Companion.FLAG_IS_LOCAL
            println("Flags correctly converted: ${photo.flag}")
        }

        should("getOnThisDay returns photos from past years") {
            val result = repository.getOnThisDay()
            result.isSuccess shouldBe true
            val photos = result.getOrThrow()
            println("Found ${photos.size} photos from 'On This Day'")

            if (photos.isNotEmpty()) {
                println("Sample photos:")
                photos.take(5).forEach { photo ->
                    println("  - ${photo.basename} (dayid: ${photo.dayid}, ${photo.w}x${photo.h})")
                }
            } else {
                println("No photos found for 'On This Day'")
            }
        }

        should("getOnThisDay photos have valid dayid") {
            val result = repository.getOnThisDay()
            result.isSuccess shouldBe true
            val photos = result.getOrThrow()

            if (photos.isNotEmpty()) {
                val allHaveDayId = photos.all { it.dayid != null && it.dayid > 0 }
                allHaveDayId shouldBe true
                println("All ${photos.size} photos have valid dayid")
            }
        }

        should("getOnThisDay filters out hidden photos and videos") {
            val result = repository.getOnThisDay()
            result.isSuccess shouldBe true
            val photos = result.getOrThrow()

            val visiblePhotos = photos.filter { !it.isHidden }
            println("Visible photos: ${visiblePhotos.size} of ${photos.size}")

            val videos = photos.filter { it.isVideo }
            println("Videos found: ${videos.size}")
        }
    }
}
