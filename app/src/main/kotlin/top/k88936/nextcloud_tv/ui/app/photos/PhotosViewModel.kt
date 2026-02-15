package top.k88936.nextcloud_tv.ui.app.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.k88936.nextcloud_tv.data.repository.AuthRepository
import top.k88936.nextcloud_tv.data.repository.AuthState
import top.k88936.nextcloud_tv.data.repository.FilesRepository
import top.k88936.webdav.FileMetadata

data class PhotosState(
    val photos: List<FileMetadata> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PhotosViewModel(
    private val authRepository: AuthRepository,
    val filesRepository: FilesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PhotosState())
    val state: StateFlow<PhotosState> = _state.asStateFlow()

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        val authState = authRepository.authState.value
        if (authState !is AuthState.Authenticated) {
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            loadPhotosRecursively("/")
        }
    }

    private suspend fun loadPhotosRecursively(path: String) {
        val result = filesRepository.listFiles(path)

        result.fold(
            onSuccess = { files ->
                val photos = files.filter {
                    !it.isDirectory && it.contentType?.startsWith("image/") == true
                }

                val directories = files.filter { it.isDirectory && !it.name.isNullOrEmpty() }

                val currentPhotos = _state.value.photos + photos
                _state.value = _state.value.copy(photos = currentPhotos)

                for (dir in directories) {
                    loadPhotosRecursively(dir.path)
                }

                if (path == "/" || directories.isEmpty()) {
                    _state.value = _state.value.copy(isLoading = false)
                }
            },
            onFailure = { error ->
                if (path == "/") {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load photos"
                    )
                }
            }
        )
    }

    fun refresh() {
        _state.value = _state.value.copy(photos = emptyList())
        loadPhotos()
    }
}
