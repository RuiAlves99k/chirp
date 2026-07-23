package com.ruialves.chat.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.ruialves.chat.database.dao.ChatDao
import com.ruialves.chat.database.dao.ChatMessageDao
import com.ruialves.chat.database.dao.ChatParticipantCrossRefDao
import com.ruialves.chat.database.dao.ChatParticipantDao
import com.ruialves.chat.database.entities.ChatEntity
import com.ruialves.chat.database.entities.ChatMessageEntity
import com.ruialves.chat.database.entities.ChatParticipantCrossRef
import com.ruialves.chat.database.entities.ChatParticipantEntity
import com.ruialves.chat.database.view.LastMessageView

@Database(
    entities = [
        ChatEntity::class,
        ChatParticipantEntity::class,
        ChatMessageEntity::class,
        ChatParticipantCrossRef::class
    ],
    views = [
        LastMessageView::class
    ],
    version = 1,
)
@ConstructedBy(ChirpChatDatabaseConstructor::class)
abstract class ChirpChatDatabase: RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val chatParticipantDao: ChatParticipantDao
    abstract val chatMessageDao: ChatMessageDao
    abstract val chatParticipantCrossRefDao: ChatParticipantCrossRefDao

    companion object {
        const val DB_NAME = "chirp.db"
    }
}
