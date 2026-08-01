package com.ruialves.chat.data.network

import com.ruialves.chat.data.dto.websocket.WebSocketMessageDto
import com.ruialves.chat.data.lifecycle.AppLifecycleObserver
import com.ruialves.chat.domain.models.ConnectionState
import com.ruialves.core.data.networking.UrlConstants
import com.ruialves.core.domain.auth.SessionStorage
import com.ruialves.core.domain.logging.ChirpLogger
import com.ruialves.core.domain.util.ConnectionError
import com.ruialves.core.domain.util.EmptyResult
import com.ruialves.core.domain.util.Result.Failure
import com.ruialves.core.domain.util.Result.Success
import com.ruialves.feature.chat.data.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

class KtorWebSocketConnector(
    private val httpClient: HttpClient,
    private val applicationScope: CoroutineScope,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val connectionErrorHandler: ConnectionErrorHandler,
    private val connectionRetryHandler: ConnectionRetryHandler,
    private val appLifecycleObserver: AppLifecycleObserver,
    private val connectivityObserver: ConnectivityObserver,
) {

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private var currentSession: WebSocketSession? = null

    private val isConnected = connectivityObserver
        .isConnected
        .debounce(1.seconds)
        .stateIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    private val isInForeground = appLifecycleObserver
        .isInForeground
        .onEach { isInForeground ->
            if (isInForeground) {
                connectionRetryHandler.resetDelay()
            }
        }
        .stateIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    val messages = combine(
        sessionStorage.observeAuthInfo(),
        isConnected,
        isInForeground,
    ) { authInfo, isConnected, isInForeground ->
        when {
            authInfo == null -> {
                ChirpLogger.i { "No authentication details. Clearing session and disconnecting..." }
                _connectionState.value = ConnectionState.DISCONNECTED
                currentSession?.close()
                currentSession = null
                connectionRetryHandler.resetDelay()
                null
            }

            !isInForeground -> {
                ChirpLogger.i { "App in background disconnecting socket proactively" }
                _connectionState.value = ConnectionState.DISCONNECTED
                currentSession?.close()
                currentSession = null
                null
            }

            !isConnected -> {
                ChirpLogger.i { "Device is disconnected, closing WebSocket connection." }
                _connectionState.value = ConnectionState.ERROR_NETWORK
                currentSession?.close()
                currentSession = null
                null
            }

            else -> {
                ChirpLogger.i { "App in foreground & connected. Establishing connection.." }
                if (_connectionState.value !in listOf(
                        ConnectionState.CONNECTING,
                        ConnectionState.CONNECTED
                    )
                ) {
                    _connectionState.value = ConnectionState.CONNECTING
                }

                authInfo
            }
        }
    }.flatMapLatest { authInfo ->
        if (authInfo == null) {
            emptyFlow()
        } else {
            createWebSocketFlow(authInfo.accessToken)
                // Catch block to transform exceptions for platform compatibility
                .catch { e ->
                    ChirpLogger.e(e) { "Exception in WebSocket" }
                    currentSession?.close()
                    currentSession = null
                    val transformedException = connectionErrorHandler.transformException(e)
                    throw transformedException
                }
                .retryWhen { t, attempt ->
                    ChirpLogger.i { "Connection failed on attempt $attempt" }

                    val shouldRetry = connectionRetryHandler.shouldRetry(t, attempt)
                    if (shouldRetry){
                        _connectionState.value = ConnectionState.CONNECTING
                        connectionRetryHandler.applyRetryDelay(attempt)
                    }

                    shouldRetry
                }
                // Catch block for non-retriable errors
                .catch { e ->
                    ChirpLogger.e(e) { "Unhandled WebSocket error" }
                    _connectionState.value = connectionErrorHandler.getConnectionStateForError(e)
                }
        }
    }

    private fun createWebSocketFlow(accessToken: String) = callbackFlow {
        _connectionState.value = ConnectionState.CONNECTING

        currentSession = httpClient.webSocketSession(
            urlString = "${UrlConstants.BASE_URL_WS}/chat"
        ) {
            header("Authorization", "Bearer $accessToken")
            header("X-API-Key", BuildKonfig.API_KEY)
        }

        currentSession?.let { session ->
            _connectionState.value = ConnectionState.CONNECTED

            session
                .incoming
                .consumeAsFlow()
                .buffer(
                    capacity = 100
                )
                .collect { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            ChirpLogger.i { "Received raw text frame: $text" }

                            val messageDto = json.decodeFromString<WebSocketMessageDto>(text)
                            send(messageDto)
                        }

                        is Frame.Ping -> {
                            ChirpLogger.d { "Received ping from server. Sending pong..." }
                            session.send(Frame.Pong(frame.data))
                        }

                        else -> Unit
                    }
                }
        } ?: throw Exception("Failed to establish WebSocket connection")

        awaitClose {
            launch(NonCancellable) {
                ChirpLogger.i { "Disconnecting from WebSocket session..." }
                _connectionState.value = ConnectionState.DISCONNECTED
                currentSession?.close()
                currentSession = null
            }
        }
    }

    suspend fun sendMessage(message: String): EmptyResult<ConnectionError>{
        val connectionState = connectionState.value

        if (currentSession == null || connectionState != ConnectionState.CONNECTED) {
            return Failure(ConnectionError.NOT_CONNECTED)
        }

        return try {
            currentSession?.send(message)
            Success(Unit)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            ChirpLogger.e(e) { "Unable to send WebSocket message" }
            Failure(ConnectionError.MESSAGE_SEND_FAILED)
        }
    }

}
