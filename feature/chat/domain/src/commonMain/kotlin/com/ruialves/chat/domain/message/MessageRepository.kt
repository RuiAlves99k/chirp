package com.ruialves.chat.domain.message

import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult

interface MessageRepository {
    suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local>
}
