package com.echo.journal.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EchoDao {
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun observeEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    fun observeEntry(id: Long): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun findEntry(id: Long): JournalEntryEntity?

    @Insert
    suspend fun insertEntry(entry: JournalEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)

    @Query("SELECT * FROM mood_check_ins ORDER BY createdAt DESC")
    fun observeCheckIns(): Flow<List<MoodCheckInEntity>>

    @Insert
    suspend fun insertCheckIn(checkIn: MoodCheckInEntity): Long

    @Delete
    suspend fun deleteCheckIn(checkIn: MoodCheckInEntity)

    @Query("DELETE FROM mood_check_ins WHERE id = :id")
    suspend fun deleteCheckInById(id: Long)
}
