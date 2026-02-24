package top.k88936.nextcloud_tv.ui.app.memories

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.k88936.nextcloud_tv.data.repository.MemoriesRepository
import top.k88936.nextcloud_tv.navigation.LocalNavController
import top.k88936.nextcloud_tv.ui.components.FocusMaintainedLazyColumn
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import top.k88936.nextcloud_tv.data.model.Day as MemoriesDay
import top.k88936.nextcloud_tv.data.model.Photo as MemoriesPhoto

sealed class TimelineRow {
    data class DayRow(
        val photos: List<MemoriesPhoto>,
        val dayId: Int,
        val dayName: String? = null,
        val photoCount: Int = 0,
        val rowHeight: Int = 200
    ) : TimelineRow()
}

@Composable
fun TimelineTab(
    modifier: Modifier = Modifier,
    viewModel: TimelineViewModel
) {
    val state by viewModel.state.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val navController = LocalNavController.current

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) {
            focusRequester.requestFocus()
        }
    }

    fun handleSelectPhoto(photo: MemoriesPhoto) {
        val imageUrl = viewModel.memoriesRepository.getFullImageUrl(photo) ?: return
        val encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
        val encodedName =
            URLEncoder.encode(photo.basename ?: "Photo", StandardCharsets.UTF_8.toString())
        navController.navigate("photo_viewer?url=$encodedUrl&name=$encodedName")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        when {
            state.error != null -> {
                ErrorView(error = state.error)
            }

            state.days.isEmpty() && !state.isLoading -> {
                EmptyView()
            }

            state.days.isNotEmpty() -> {
                TimelineContent(
                    days = state.days,
                    photosByDay = state.photosByDay,
                    memoriesRepository = viewModel.memoriesRepository,
                    focusRequester = focusRequester,
                    onLoadMore = { viewModel.loadMoreDays() },
                    isLoadingMore = state.isLoadingMore,
                    focusedItemId = viewModel.focusedItemId,
                    onFocusChanged = { row, isFocused ->
                        if (isFocused) {
                            val key = when (row) {
                                is TimelineRow.DayRow -> "day-row-${row.dayId}-${row.photos.firstOrNull()?.fileid}"
                            }
                            viewModel.updateFocusedItemId(key)
                        }
                    },
                    onSelectPhoto = { photo -> handleSelectPhoto(photo) }
                )
            }
        }

        if (state.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
private fun TimelineContent(
    days: List<MemoriesDay>,
    photosByDay: Map<Int, List<MemoriesPhoto>>,
    memoriesRepository: MemoriesRepository,
    focusRequester: FocusRequester,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    focusedItemId: String?,
    onFocusChanged: (TimelineRow, Boolean) -> Unit,
    onSelectPhoto: (MemoriesPhoto) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val rowHeight = 180.dp
    val photoWidth = 160.dp
    val numCols = maxOf(3, (screenWidthDp / photoWidth).toInt())

    val sortedDays = remember(days) {
        days.sortedByDescending { it.dayid }
    }

    val timelineRows = remember(sortedDays, photosByDay, numCols) {
        val rows = mutableListOf<TimelineRow>()

        for (day in sortedDays) {
            val photos = photosByDay[day.dayid] ?: continue
            if (photos.isEmpty()) continue

            val sortedPhotos = photos.sortedByDescending { it.epoch }
            val chunks = sortedPhotos.chunked(numCols)

            chunks.forEachIndexed { index, photoChunk ->
                rows.add(
                    TimelineRow.DayRow(
                        photos = photoChunk,
                        dayId = day.dayid,
                        dayName = if (index == 0) getHeadRowName(day.dayid) else null,
                        photoCount = if (index == 0) photos.size else 0,
                        rowHeight = rowHeight.value.toInt()
                    )
                )
            }
        }
        rows
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FocusMaintainedLazyColumn(
            items = timelineRows,
            key = { row ->
                when (row) {
                    is TimelineRow.DayRow -> "day-row-${row.dayId}-${row.photos.firstOrNull()?.fileid}"
                }
            },
            focusedItemId = focusedItemId,
            onFocusChanged = onFocusChanged,
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester),
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 8.dp,
                top = 56.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) { row, itemFocusRequester, isFocused ->
            when (row) {
                is TimelineRow.DayRow -> {
                    val focusedPhotoId = focusedItemId?.split("-")?.lastOrNull()?.toIntOrNull()
                    DayRowContent(
                        photos = row.photos,
                        dayName = row.dayName,
                        photoCount = row.photoCount,
                        memoriesRepository = memoriesRepository,
                        focusedPhotoId = focusedPhotoId,
                        onSelectPhoto = onSelectPhoto,
                        rowFocusRequester = itemFocusRequester,
                        isRowFocused = isFocused,
                    )
                }
            }
        }

        LaunchedEffect(timelineRows.size) {
            if (!isLoadingMore && timelineRows.isNotEmpty()) {
                onLoadMore()
            }
        }
    }
}

@Composable
private fun DayRowContent(
    photos: List<MemoriesPhoto>,
    dayName: String?,
    photoCount: Int,
    memoriesRepository: MemoriesRepository,
    focusedPhotoId: Int?,
    onSelectPhoto: (MemoriesPhoto) -> Unit,
    rowFocusRequester: FocusRequester,
    isRowFocused: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (dayName != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(start = 8.dp, top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "($photoCount)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            photos.forEachIndexed { index, photo ->
                val isFirstPhoto = index == 0
                PhotoCard(
                    photo = photo,
                    memoriesRepository = memoriesRepository,
                    isFocused = focusedPhotoId == photo.fileid,
                    onSelect = { onSelectPhoto(photo) },
                    focusRequester = if (isFirstPhoto) rowFocusRequester else remember { FocusRequester() },
                    modifier = Modifier.weight(1f)
                )
            }

            repeat(maxOf(0, 5 - photos.size)) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PhotoCard(
    photo: MemoriesPhoto,
    memoriesRepository: MemoriesRepository,
    isFocused: Boolean,
    onSelect: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
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
        onClick = onSelect,
        modifier = modifier.focusRequester(focusRequester),
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

@Composable
private fun ErrorView(error: String?) {
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

@Composable
private fun EmptyView() {
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

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Text("Loading...", style = MaterialTheme.typography.bodyLarge)
    }
}

private fun getHeadRowName(dayId: Int): String {
    val date = Date(dayId.toLong() * 86400 * 1000)
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)

    calendar.time = date
    val dateYear = calendar.get(Calendar.YEAR)

    val showYear = dateYear != currentYear

    return try {
        val pattern = if (showYear) {
            "EEE, MMM d, yyyy"
        } else {
            "EEE, MMM d"
        }
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        format.format(date)
    } catch (_: Exception) {
        "Unknown date"
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
