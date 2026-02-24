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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import org.koin.compose.viewmodel.koinViewModel
import top.k88936.nextcloud_tv.data.repository.MemoriesRepository

@Composable
fun OnThisDayTab(
    modifier: Modifier = Modifier,
    viewModel: OnThisDayViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading && state.yearGroups.isNotEmpty()) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusable()
            .focusRequester(focusRequester)
    ) {
        when {
            state.error != null -> {
                ErrorView(error = state.error)
            }

            state.yearGroups.isEmpty() && !state.isLoading -> {
                EmptyView()
            }

            state.yearGroups.isNotEmpty() -> {
                OnThisDayContent(
                    yearGroups = state.yearGroups,
                    memoriesRepository = viewModel.memoriesRepository,
                    focusRequester = focusRequester
                )
            }
        }

        if (state.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
private fun OnThisDayContent(
    yearGroups: List<YearGroup>,
    memoriesRepository: MemoriesRepository,
    focusRequester: FocusRequester
) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "On This Day",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)
        )

        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = yearGroups,
                key = { it.year }
            ) { yearGroup ->
                YearGroupCard(
                    yearGroup = yearGroup,
                    memoriesRepository = memoriesRepository,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun YearGroupCard(
    yearGroup: YearGroup,
    memoriesRepository: MemoriesRepository,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var previewBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val cardFocusRequester = remember { FocusRequester() }

    LaunchedEffect(yearGroup.previewPhoto.fileid) {
        withContext(Dispatchers.IO) {
            memoriesRepository.getPreview(
                yearGroup.previewPhoto.fileid,
                yearGroup.previewPhoto.etag
            ).getOrNull()?.let { bytes ->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }?.let { previewBitmap = it }
        }
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(4f / 3f)
            .focusRequester(cardFocusRequester),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (previewBitmap != null) {
                Image(
                    bitmap = previewBitmap!!.asImageBitmap(),
                    contentDescription = yearGroup.text,
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = yearGroup.text,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(12.dp)
                )
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
            text = "No memories found for today",
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
