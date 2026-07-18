package com.bakudapa.adventure.feature.summit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PendingSummitLog::class], version = 1, exportSchema = false)
abstract class SummitLogDatabase : RoomDatabase() {
    abstract fun summitLogDao(): SummitLogDao

    companion object {
        @Volatile
        private var INSTANCE: SummitLogDatabase? = null

        fun getInstance(context: Context): SummitLogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SummitLogDatabase::class.java,
                    "summit_logs_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
