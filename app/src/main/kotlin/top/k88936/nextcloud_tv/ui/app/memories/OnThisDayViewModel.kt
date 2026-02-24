package top.k88936.nextcloud_tv.ui.app.memories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.k88936.nextcloud_tv.data.model.Photo
import top.k88936.nextcloud_tv.data.repository.ClientRepository
import top.k88936.nextcloud_tv.data.repository.MemoriesRepository
import java.util.Calendar

data class YearGroup(
    val year: Int,
    val text: String,
    val photos: List<Photo>,
    val previewPhoto: Photo
)

data class OnThisDayState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val yearGroups: List<YearGroup> = emptyList()
)

class OnThisDayViewModel(
    private val clientRepository: ClientRepository,
    val memoriesRepository: MemoriesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnThisDayState())
    val state: StateFlow<OnThisDayState> = _state.asStateFlow()

    init {
        loadOnThisDay()
    }

    fun loadOnThisDay() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            memoriesRepository.getOnThisDay()
                .fold(
                    onSuccess = { photos ->
                        val yearGroups = processPhotos(photos)
                        _state.value = OnThisDayState(
                            isLoading = false,
                            yearGroups = yearGroups
                        )
                    },
                    onFailure = { error ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load On This Day"
                        )
                    }
                )
        }
    }

    private fun processPhotos(photos: List<Photo>): List<YearGroup> {
        val filteredPhotos = photos.filter { photo ->
            !photo.isHidden &&
                    photo.basename?.startsWith(".") != true &&
                    !photo.isVideo
        }

        if (filteredPhotos.isEmpty()) return emptyList()

        data class MutableYearGroup(
            val year: Int,
            val text: String,
            val photos: MutableList<Photo> = mutableListOf()
        )

        val groups = mutableListOf<MutableYearGroup>()
        var currentYear = Int.MAX_VALUE
        var currentText = ""
        val now = Calendar.getInstance()
        val currentYearNow = now.get(Calendar.YEAR)

        for (photo in filteredPhotos) {
            val dayId = photo.dayid ?: continue
            val dateTaken = dayIdToDate(dayId)
            val year = dateTaken.get(Calendar.YEAR)

            if (year != currentYear) {
                val text = getYearsAgoText(dateTaken, currentYearNow)
                if (text != currentText) {
                    groups.add(
                        MutableYearGroup(
                            year = year,
                            text = text
                        )
                    )
                    currentText = text
                }
                currentYear = year
            }

            groups.lastOrNull()?.photos?.add(photo)
        }

        return groups.map { group ->
            val selectedPhotos = group.photos.takeRandom(10)
            val previewPhoto = selectPreviewPhoto(selectedPhotos)
            YearGroup(
                year = group.year,
                text = group.text,
                photos = selectedPhotos,
                previewPhoto = previewPhoto
            )
        }
    }

    private fun dayIdToDate(dayId: Int): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dayId.toLong() * 24L * 60L * 60L * 1000L
        return calendar
    }

    private fun getYearsAgoText(date: Calendar, currentYear: Int): String {
        val yearsAgo = currentYear - date.get(Calendar.YEAR)
        return when {
            yearsAgo <= 0 -> "This year"
            yearsAgo == 1 -> "1 year ago"
            else -> "$yearsAgo years ago"
        }
    }

    private fun selectPreviewPhoto(photos: List<Photo>): Photo {
        val landscapePhotos = photos.filter { photo ->
            val w = photo.w ?: 0
            val h = photo.h ?: 0
            w > h
        }
        return if (landscapePhotos.isNotEmpty()) {
            landscapePhotos.random()
        } else {
            photos.random()
        }
    }

    private fun <T> List<T>.takeRandom(n: Int): List<T> {
        if (size <= n) return this
        val indices = indices.shuffled().take(n)
        return indices.map { this[it] }
    }

    fun refresh() {
        loadOnThisDay()
    }
}
