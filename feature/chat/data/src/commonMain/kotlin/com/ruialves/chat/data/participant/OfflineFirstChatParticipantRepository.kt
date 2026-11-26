package com.ruialves.chat.data.participant

import com.ruialves.chat.domain.models.ChatParticipant
import com.ruialves.chat.domain.participant.ChatParticipantRepository
import com.ruialves.chat.domain.participant.ChatParticipantService
import com.ruialves.core.domain.auth.SessionStorage
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.Result
import com.ruialves.core.domain.util.onSuccess
import kotlinx.coroutines.flow.first

class OfflineFirstChatParticipantRepository(
    private val sessionStorage: SessionStorage,
    private val chatParticipantService: ChatParticipantService
) : ChatParticipantRepository {
    override suspend fun fetchLocalParticipant(): Result<ChatParticipant, DataError> {
        return chatParticipantService
            .getLocalParticipant()
            .onSuccess { participant ->
                val currentAuthInfo = sessionStorage.observeAuthInfo().first()
                sessionStorage.set(
                    currentAuthInfo?.copy(
                        user = currentAuthInfo.user.copy(
                            id = participant.userId,
                            username = participant.username,
                            profilePictureUrl = participant.profilePictureUrl
                        )
                    )
                )
            }
    }
}
