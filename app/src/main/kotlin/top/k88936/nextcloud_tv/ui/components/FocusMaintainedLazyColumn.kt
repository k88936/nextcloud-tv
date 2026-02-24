package top.k88936.nextcloud_tv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.unit.dp

@Composable
fun <T : Any> FocusMaintainedLazyColumn(
    items: List<T>,
    key: (T) -> Any,
    focusedItemId: Any?,
    onFocusChanged: (T, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    itemContent: @Composable LazyItemScope.(T, FocusRequester, Boolean) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement
    ) {
        items(items, key = key) { item ->
            val focusRequester = remember { FocusRequester() }
            val itemKey = key(item)
            val isFocusedTarget = itemKey == focusedItemId

            LaunchedEffect(itemKey, focusedItemId) {
                if (isFocusedTarget) {
                    focusRequester.requestFocus()
                }
            }

            var isFocused by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusEvent { focusState ->
                        val wasFocused = isFocused
                        isFocused = focusState.hasFocus
                        if (wasFocused != isFocused) {
                            onFocusChanged(item, isFocused)
                        }
                    }
            ) {
                itemContent(item, focusRequester, isFocused)
            }
        }
    }
}
