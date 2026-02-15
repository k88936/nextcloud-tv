package top.k88936.nextcloud_tv.ui.screens.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.k88936.nextcloud_tv.data.repository.AuthRepository
import top.k88936.nextcloud_tv.data.repository.AuthState
import top.k88936.nextcloud_tv.data.repository.FilesRepository
import top.k88936.nextcloud_tv.data.repository.FilesState
import top.k88936.webdav.FileMetadata

class FilesViewModel(
    private val authRepository: AuthRepository,
    val filesRepository: FilesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FilesState())
    val state: StateFlow<FilesState> = _state.asStateFlow()

    init {
        initializeAndLoadFiles()
    }

    private fun initializeAndLoadFiles() {
        val authState = authRepository.authState.value
        if (authState is AuthState.Authenticated) {
            filesRepository.initialize(authState.credentials)
            loadFiles("/")
        }
    }

    fun loadFiles(path: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, currentPath = path)
            
            val result = filesRepository.listFiles(path)
            
            result.fold(
                onSuccess = { files ->
                    val sortedFiles = files.filter { !it.name.isNullOrEmpty() }
                        .sortedWith(compareBy<FileMetadata> { !it.isDirectory }.thenBy {
                            it.name?.lowercase() ?: ""
                        })
                    _state.value = _state.value.copy(
                        files = sortedFiles,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load files"
                    )
                }
            )
        }
    }

    fun navigateToDirectory(directory: FileMetadata) {
        if (directory.isDirectory) {
            loadFiles(directory.path)
        }
    }

    fun navigateUp() {
        val currentPath = _state.value.currentPath
        if (currentPath != "/") {
            val pathWithoutTrailingSlash = currentPath.trimEnd('/')
            val parentPath = pathWithoutTrailingSlash.substringBeforeLast('/')
            loadFiles(if (parentPath.isEmpty()) "/" else "$parentPath/")
        }
    }

    fun refresh() {
        loadFiles(_state.value.currentPath)
    }

}
