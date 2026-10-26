package com.echo.journal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [JournalEntryEntity::class, MoodCheckInEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class EchoDatabase : RoomDatabase() {
    abstract fun echoDao(): EchoDao

    companion object {
        private const val DATABASE_NAME = "echo.db"

        @Volatile
        private var instance: EchoDatabase? = null

        fun get(context: Context): EchoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    EchoDatabase::class.java,
                    DATABASE_NAME,
                ).build().also { instance = it }
            }
    }
}
