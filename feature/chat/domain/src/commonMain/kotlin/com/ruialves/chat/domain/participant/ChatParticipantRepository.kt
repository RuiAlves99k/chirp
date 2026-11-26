package com.ruialves.chat.domain.participant

import com.ruialves.chat.domain.models.ChatParticipant
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.Result

interface ChatParticipantRepository {
    suspend fun fetchLocalParticipant(): Result<ChatParticipant, DataError>
}
