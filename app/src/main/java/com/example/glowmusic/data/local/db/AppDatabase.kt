package com.example.glowmusic.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.glowmusic.data.local.db.dao.PlayHistoryDao
import com.example.glowmusic.data.local.db.dao.PlaylistDao
import com.example.glowmusic.data.local.db.dao.TrackDao
import com.example.glowmusic.data.local.db.entity.FavoriteEntity
import com.example.glowmusic.data.local.db.entity.PlayHistoryEntity
import com.example.glowmusic.data.local.db.entity.PlaylistEntity
import com.example.glowmusic.data.local.db.entity.PlaylistTrackCrossRef
import com.example.glowmusic.data.local.db.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        FavoriteEntity::class,
        PlayHistoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playHistoryDao(): PlayHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "glow_music.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
