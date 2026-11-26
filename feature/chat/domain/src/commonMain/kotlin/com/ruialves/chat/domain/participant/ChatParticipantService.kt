package com.ruialves.chat.domain.participant

import com.ruialves.chat.domain.models.ChatParticipant
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.Result

interface ChatParticipantService {
    suspend fun searchParticipant(
        query: String,
    ): Result<ChatParticipant, DataError.Remote>

    suspend fun getLocalParticipant(): Result<ChatParticipant, DataError.Remote>
}
