package top.k88936.nextcloud_tv.ui.app.memories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.k88936.nextcloud_tv.data.model.Day
import top.k88936.nextcloud_tv.data.model.Photo
import top.k88936.nextcloud_tv.data.repository.AuthState
import top.k88936.nextcloud_tv.data.repository.IAuthRepository
import top.k88936.nextcloud_tv.data.repository.MemoriesRepository

data class TimelineState(
    val days: List<Day> = emptyList(),
    val photosByDay: Map<Int, List<Photo>> = emptyMap(),
    val allPhotos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)

class TimelineViewModel(
    private val authRepository: IAuthRepository,
    val memoriesRepository: MemoriesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TimelineState())
    val state: StateFlow<TimelineState> = _state.asStateFlow()

    var focusedItemId by mutableStateOf<Int?>(null)
        private set

    private val loadedDays = mutableSetOf<Int>()

    init {
        initializeAndLoadTimeline()
    }

    private fun initializeAndLoadTimeline() {
        val authState = authRepository.authState.value
        if (authState is AuthState.Authenticated) {
            loadDays()
        }
    }

    fun loadDays() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            focusedItemId = null
            loadedDays.clear()

            memoriesRepository.getDays()
                .fold(
                    onSuccess = { days ->
                        _state.value = _state.value.copy(
                            days = days,
                            isLoading = false
                        )
                        days.firstOrNull()?.dayid?.let { firstDayId ->
                            loadDay(firstDayId)
                        }
                    },
                    onFailure = { error ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load timeline"
                        )
                    }
                )
        }
    }

    fun loadDay(dayId: Int) {
        if (loadedDays.contains(dayId)) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)

            memoriesRepository.getDay(listOf(dayId))
                .fold(
                    onSuccess = { photos ->
                        loadedDays.add(dayId)
                        val currentPhotosByDay = _state.value.photosByDay.toMutableMap()
                        currentPhotosByDay[dayId] = photos

                        val allPhotos = currentPhotosByDay.values.flatten()
                            .sortedByDescending { it.epoch }

                        _state.value = _state.value.copy(
                            photosByDay = currentPhotosByDay,
                            allPhotos = allPhotos,
                            isLoadingMore = false
                        )
                    },
                    onFailure = { error ->
                        _state.value = _state.value.copy(isLoadingMore = false)
                    }
                )
        }
    }

    fun loadMoreDays(count: Int = 3) {
        val unloadedDays = _state.value.days
            .filter { !loadedDays.contains(it.dayid) }
            .take(count)
            .map { it.dayid }

        if (unloadedDays.isNotEmpty()) {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoadingMore = true)

                memoriesRepository.getDay(unloadedDays)
                    .fold(
                        onSuccess = { photos ->
                            unloadedDays.forEach { loadedDays.add(it) }

                            val currentPhotosByDay = _state.value.photosByDay.toMutableMap()
                            photos.groupBy { it.dayid }.forEach { (dayId, dayPhotos) ->
                                if (dayId != null) {
                                    currentPhotosByDay[dayId] = dayPhotos
                                }
                            }

                            val allPhotos = currentPhotosByDay.values.flatten()
                                .sortedByDescending { it.epoch }

                            _state.value = _state.value.copy(
                                photosByDay = currentPhotosByDay,
                                allPhotos = allPhotos,
                                isLoadingMore = false
                            )
                        },
                        onFailure = {
                            _state.value = _state.value.copy(isLoadingMore = false)
                        }
                    )
            }
        }
    }

    fun updateFocusedItemId(id: Int?) {
        focusedItemId = id
    }

    fun refresh() {
        loadedDays.clear()
        loadDays()
    }
}
