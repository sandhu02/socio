package com.example.cusotmauthtest.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusotmauthtest.retrofit.ChatMessage
import com.example.cusotmauthtest.retrofit.SendNotificationRequest
import com.example.cusotmauthtest.retrofit.getAuthApiInstance
import com.example.cusotmauthtest.retrofit.getBaseUrl
import com.example.cusotmauthtest.retrofit.getToken
import com.example.cusotmauthtest.retrofit.getUsername
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

val defaultConversation = listOf(
    ChatMessage(
        text = "did you see the oncall stream? latency was nuts",
        isFromMe = false,
        senderUsername = "mira.r"
    ),
    ChatMessage(
        text = "yes, turn server was the culprit",
        isFromMe = true,
        senderUsername = "me"
    ),
    ChatMessage(
        text = "Audio call · 12:41",
        isFromMe = false,
        isCallEvent = true
    ),
    ChatMessage(
        text = "ok rebase done, pushing now",
        isFromMe = false,
        senderUsername = "mira.r"
    ),
    ChatMessage(
        text = "send me the clip when it's up",
        isFromMe = true,
        senderUsername = "me"
    )
)

data class ChatUiState(
    val senderMessage: String = "",
    val messageList: List<ChatMessage> = defaultConversation,
    val connectionMessage: String = "Online",
    val isConnected: Boolean = true
)

class ChatViewModel(val context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private var mSocket: Socket? = null

    fun updateSenderMessage(newMessage: String) {
        _uiState.update { it.copy(senderMessage = newMessage) }
    }

    fun connect() {
        try {
            val token = getToken(context) ?: ""
            val baseUrl = getBaseUrl(context)

            val options = IO.Options().apply {
                reconnection = true
                forceNew = true
                auth = mapOf("token" to token)
            }

            mSocket = IO.socket(baseUrl, options).apply {
                on(Socket.EVENT_CONNECT) {
                    Log.d("Socket", "Connected to server")
                    _uiState.update {
                        it.copy(
                            connectionMessage = "Connected",
                            isConnected = true
                        )
                    }
                }

                on(Socket.EVENT_CONNECT_ERROR) { args ->
                    val err = args.firstOrNull()?.toString() ?: "Unknown error"
                    Log.e("Socket", "Connection error: $err")
                    _uiState.update {
                        it.copy(
                            connectionMessage = "Connection error: $err",
                            isConnected = false
                        )
                    }
                }

                on(Socket.EVENT_DISCONNECT) {
                    Log.d("Socket", "Disconnected")
                    _uiState.update {
                        it.copy(
                            connectionMessage = "Disconnected",
                            isConnected = false
                        )
                    }
                }

                on("chat message", onMessageReceived)
                connect()
            }
        } catch (e: Exception) {
            Log.e("Socket", "Failed to init socket: ${e.message}")
            _uiState.update { it.copy(connectionMessage = "Error: ${e.localizedMessage}") }
        }
    }

    fun sendMessage(message: String, toUserName: String) {
        if (message.isBlank()) return

        try {
            mSocket?.emit(
                "chat message",
                JSONObject().apply {
                    put("toUserName", toUserName)
                    put("message", message)
                }
            )

            // Add sent message to chat list
            val sentMsg = ChatMessage(
                text = message,
                isFromMe = true,
                senderUsername = getUsername(context)
            )
            _uiState.update {
                it.copy(
                    messageList = it.messageList + sentMsg,
                    senderMessage = ""
                )
            }

            // Also trigger push notification so user receives it when app is in background
            viewModelScope.launch {
                try {
                    val myUsername = getUsername(context) ?: "Someone"
                    getAuthApiInstance(context)?.sendNotification(
                        username = toUserName,
                        request = SendNotificationRequest(
                            title = "Message from $myUsername",
                            body = message
                        )
                    )
                } catch (e: Exception) {
                    Log.w("ChatViewModel", "Could not send push notification: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("Socket", "Error sending message: ${e.message}")
        }
    }

    private val onMessageReceived = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val text = data.getString("message")
            val from = if (data.has("from")) data.getString("from") else "Unknown"
            val chatMsg = ChatMessage(
                text = text,
                isFromMe = false,
                senderUsername = from
            )
            _uiState.update { it.copy(messageList = it.messageList + chatMsg) }
        } catch (e: Exception) {
            Log.e("Socket", "Error parsing message: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            mSocket?.disconnect()
            mSocket?.off()
            mSocket = null
        } catch (e: Exception) {
            Log.e("Socket", "Error disconnecting: ${e.message}")
        }
    }

    init {
        connect()
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}