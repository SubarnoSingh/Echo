package com.echo.journal.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    indices = [Index("createdAt"), Index("updatedAt")],
)
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val moodKey: String?,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    tableName = "mood_check_ins",
    indices = [Index("createdAt")],
)
data class MoodCheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moodKey: String,
    val note: String,
    val createdAt: Long,
)
