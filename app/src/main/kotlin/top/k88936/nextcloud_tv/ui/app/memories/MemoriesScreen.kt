package top.k88936.nextcloud_tv.ui.app.memories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import org.koin.compose.viewmodel.koinViewModel
import top.k88936.nextcloud_tv.ui.Icon.memories.Favourites
import top.k88936.nextcloud_tv.ui.Icon.memories.OnThisDay
import top.k88936.nextcloud_tv.ui.Icon.memories.Timeline

sealed class MemoriesTab(
    val title: String,
) {
    data object TimelineTab : MemoriesTab("Timeline")
    data object FavouritesTab : MemoriesTab("Favourites")
    data object OnThisDayTab : MemoriesTab("On This Day")
}

val memoriesTabs = listOf(
    MemoriesTab.TimelineTab,
    MemoriesTab.FavouritesTab,
    MemoriesTab.OnThisDayTab
)

@Composable
fun MemoriesScreen(
    modifier: Modifier = Modifier,
    timelineViewModel: TimelineViewModel = koinViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var focusedTabIndex by remember { mutableIntStateOf(selectedTabIndex) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Memories",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        TabRow(
            selectedTabIndex = focusedTabIndex,
            modifier = Modifier.focusRestorer()
        ) {
            memoriesTabs.forEachIndexed { index, tab ->
                key(index) {
                    Tab(
                        selected = selectedTabIndex == index,
                        onFocus = { focusedTabIndex = index },
                        onClick = {
                            focusedTabIndex = index
                            selectedTabIndex = index
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (tab) {
                                    is MemoriesTab.TimelineTab -> androidx.compose.material.icons.Icons.Filled.Timeline
                                    is MemoriesTab.FavouritesTab -> androidx.compose.material.icons.Icons.Filled.Favourites
                                    is MemoriesTab.OnThisDayTab -> androidx.compose.material.icons.Icons.Filled.OnThisDay
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tab.title,
                                fontSize = androidx.compose.ui.unit.TextUnit.Unspecified,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        when (memoriesTabs[selectedTabIndex]) {
            is MemoriesTab.TimelineTab -> TimelineTab(
                modifier = Modifier.fillMaxSize(),
                viewModel = timelineViewModel
            )

            is MemoriesTab.FavouritesTab -> FavouritesTab(
                modifier = Modifier.fillMaxSize()
            )

            is MemoriesTab.OnThisDayTab -> OnThisDayTab(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
