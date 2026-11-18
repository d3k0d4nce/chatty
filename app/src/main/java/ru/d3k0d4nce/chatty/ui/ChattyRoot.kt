package ru.d3k0d4nce.chatty.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.d3k0d4nce.chatty.R

@Composable
fun ChattyRoot(viewModel: ChattyViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val image = state.image
    val chat = state.selectedChat

    BackHandler(enabled = image != null || chat != null) {
        if (image != null) viewModel.closeImage() else viewModel.closeChat()
    }

    if (image != null) {
        ImageViewer(link = image, onClose = viewModel::closeImage)
    } else {
        ChattyLayout(state = state, viewModel = viewModel)
    }
}

@Composable
private fun ChattyLayout(state: ChatState, viewModel: ChattyViewModel) {
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val chat = state.selectedChat

    if (!landscape) {
        if (chat == null) {
            ChatListScreen(state, viewModel, Modifier.fillMaxSize().safeDrawingPadding())
        } else {
            MessagesScreen(state, viewModel, Modifier.fillMaxSize().safeDrawingPadding())
        }
        return
    }

    Row(Modifier.fillMaxSize().safeDrawingPadding()) {
        ChatListScreen(state, viewModel, Modifier.weight(2f))
        VerticalDivider()
        if (chat == null) {
            Box(Modifier.weight(3f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.select_chat))
            }
        } else {
            MessagesScreen(state, viewModel, Modifier.weight(3f))
        }
    }
}

@Composable
private fun ChatListScreen(
    state: ChatState,
    viewModel: ChattyViewModel,
    modifier: Modifier = Modifier
) {
    val channels = state.channels
    Column(modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.chats_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        )
        HorizontalDivider()
        when {
            state.channelsFailed -> ErrorPane(
                onRetry = viewModel::loadChannels,
                modifier = Modifier.fillMaxSize()
            )

            channels == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            channels.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.chats_empty))
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(channels) { name ->
                    ChatRow(
                        name = name,
                        selected = name == state.selectedChat,
                        onClick = { viewModel.openChat(name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatRow(name: String, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = background,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp)
        )
    }
}

@Composable
internal fun ErrorPane(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.load_error))
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}
