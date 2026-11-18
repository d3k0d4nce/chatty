package ru.d3k0d4nce.chatty.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.d3k0d4nce.chatty.data.Message
import ru.d3k0d4nce.chatty.data.PAGE_SIZE
import ru.d3k0d4nce.chatty.data.chatApi

data class ChatState(
    val channels: List<String>? = null,
    val channelsFailed: Boolean = false,
    val selectedChat: String? = null,
    val loadedChat: String? = null,
    val messages: List<Message> = emptyList(),
    val loading: Boolean = false,
    val failed: Boolean = false,
    val hasMore: Boolean = true,
    val image: String? = null
)

class ChattyViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadChannels()
    }

    fun loadChannels() {
        _state.update { it.copy(channelsFailed = false) }
        viewModelScope.launch {
            try {
                val channels = chatApi.getChannels()
                _state.update { it.copy(channels = channels) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.update { it.copy(channelsFailed = true) }
            }
        }
    }

    fun openChat(name: String) {
        val snapshot = _state.value
        if (name == snapshot.selectedChat) return

        loadJob?.cancel()
        if (name == snapshot.loadedChat && (snapshot.messages.isNotEmpty() || !snapshot.hasMore)) {
            _state.update { it.copy(selectedChat = name, loading = false, failed = false) }
            return
        }

        _state.update {
            it.copy(
                selectedChat = name,
                loadedChat = name,
                messages = emptyList(),
                loading = false,
                failed = false,
                hasMore = true
            )
        }
        loadMore()
    }

    fun closeChat() {
        loadJob?.cancel()
        _state.update { it.copy(selectedChat = null, loading = false, failed = false) }
    }

    fun loadMore() {
        val snapshot = _state.value
        val name = snapshot.selectedChat ?: return
        if (snapshot.loading || !snapshot.hasMore) return

        val lastKnownId = snapshot.messages.lastOrNull()?.id ?: Long.MAX_VALUE
        _state.update { it.copy(loading = true, failed = false) }

        loadJob = viewModelScope.launch {
            try {
                val page = chatApi.getMessages(name, lastKnownId, PAGE_SIZE, true)
                _state.update {
                    if (it.selectedChat != name) it
                    else it.copy(
                        messages = it.messages + page,
                        loading = false,
                        hasMore = page.size == PAGE_SIZE
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.update {
                    if (it.selectedChat == name) it.copy(loading = false, failed = true) else it
                }
            }
        }
    }

    fun openImage(link: String) {
        _state.update { it.copy(image = link) }
    }

    fun closeImage() {
        _state.update { it.copy(image = null) }
    }
}
