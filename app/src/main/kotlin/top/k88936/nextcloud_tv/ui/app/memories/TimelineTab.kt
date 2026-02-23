package top.k88936.nextcloud_tv.ui.app.memories

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.k88936.nextcloud_tv.data.repository.MemoriesRepository
import top.k88936.nextcloud_tv.ui.components.FocusMaintainedLazyVerticalGrid
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import top.k88936.nextcloud_tv.data.model.Photo as MemoriesPhoto

@Composable
fun TimelineTab(
    modifier: Modifier = Modifier,
    viewModel: TimelineViewModel
) {
    val state by viewModel.state.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.isLoading) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusable()
            .focusRequester(focusRequester)
    ) {
        val gridState = rememberLazyGridState()

        when {
            state.error != null -> {
                val error = state.error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        if (error != null) {
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            state.allPhotos.isEmpty() && !state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No photos found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            state.allPhotos.isNotEmpty() -> {
                FocusMaintainedLazyVerticalGrid(
                    items = state.allPhotos,
                    key = { it.fileid },
                    focusedItemId = viewModel.focusedItemId,
                    onFocusChanged = { photo, isFocused ->
                        if (isFocused) {
                            viewModel.updateFocusedItemId(photo.fileid)
                        }
                    },
                    gridState = gridState,
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) { photo, itemFocusRequester, isFocused ->
                    PhotoCard(
                        photo = photo,
                        memoriesRepository = viewModel.memoriesRepository,
                        focusRequester = itemFocusRequester,
                        isFocused = isFocused
                    )
                }

                LaunchedEffect(gridState.firstVisibleItemIndex) {
                    val totalItems = state.allPhotos.size
                    val visibleIndex = gridState.firstVisibleItemIndex
                    if (visibleIndex + 20 > totalItems && !state.isLoadingMore) {
                        viewModel.loadMoreDays()
                    }
                }
            }
        }

        if (state.isLoading || state.isLoadingMore) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun PhotoCard(
    photo: MemoriesPhoto,
    memoriesRepository: MemoriesRepository,
    focusRequester: FocusRequester,
    isFocused: Boolean,
) {
    var previewBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(photo.fileid) {
        withContext(Dispatchers.IO) {
            memoriesRepository.getPreview(photo.fileid, photo.etag)
                .getOrNull()?.let { bytes ->
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }?.let { previewBitmap = it }
        }
    }

    Card(
        onClick = { },
        modifier = Modifier
            .aspectRatio(1f)
            .focusRequester(focusRequester),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (previewBitmap != null) {
                Image(
                    bitmap = previewBitmap!!.asImageBitmap(),
                    contentDescription = photo.basename,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Photos,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isFocused) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.9f))
                        .padding(8.dp)
                ) {
                    photo.basename?.let { name ->
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    photo.epoch?.let { timestamp ->
                        Text(
                            text = formatTimestamp(timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        format.format(date)
    } catch (_: Exception) {
        ""
    }
}
