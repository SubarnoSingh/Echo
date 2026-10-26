package com.echo.journal.data.repository

import com.echo.journal.data.local.EchoDao
import com.echo.journal.data.local.JournalEntryEntity
import com.echo.journal.data.local.MoodCheckInEntity
import com.echo.journal.domain.JournalEntry
import com.echo.journal.domain.Mood
import com.echo.journal.domain.MoodCheckIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EchoRepository(
    private val dao: EchoDao,
    private val now: () -> Long = System::currentTimeMillis,
) {
    val entries: Flow<List<JournalEntry>> =
        dao.observeEntries().map { rows -> rows.map(JournalEntryEntity::toDomain) }

    val checkIns: Flow<List<MoodCheckIn>> =
        dao.observeCheckIns().map { rows -> rows.map(MoodCheckInEntity::toDomain) }

    fun observeEntry(id: Long): Flow<JournalEntry?> =
        dao.observeEntry(id).map { it?.toDomain() }

    suspend fun findEntry(id: Long): JournalEntry? = dao.findEntry(id)?.toDomain()

    suspend fun saveEntry(
        id: Long?,
        title: String,
        body: String,
        mood: Mood?,
    ): Long {
        val timestamp = now()
        if (id != null && id > 0) {
            val existing = dao.findEntry(id)
            if (existing != null) {
                dao.updateEntry(
                    existing.copy(
                        title = title.trim(),
                        body = body.trim(),
                        moodKey = mood?.key,
                        updatedAt = timestamp,
                    ),
                )
                return id
            }
        }
        return dao.insertEntry(
            JournalEntryEntity(
                title = title.trim(),
                body = body.trim(),
                moodKey = mood?.key,
                createdAt = timestamp,
                updatedAt = timestamp,
            ),
        )
    }

    suspend fun deleteEntry(id: Long) {
        dao.deleteEntryById(id)
    }

    suspend fun addCheckIn(mood: Mood, note: String): Long = dao.insertCheckIn(
        MoodCheckInEntity(
            moodKey = mood.key,
            note = note.trim(),
            createdAt = now(),
        ),
    )

    suspend fun deleteCheckIn(id: Long) {
        dao.deleteCheckInById(id)
    }
}

internal fun JournalEntryEntity.toDomain(): JournalEntry = JournalEntry(
    id = id,
    title = title,
    body = body,
    mood = Mood.fromKey(moodKey),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun MoodCheckInEntity.toDomain(): MoodCheckIn = MoodCheckIn(
    id = id,
    mood = Mood.fromKey(moodKey) ?: Mood.NEUTRAL,
    note = note,
    createdAt = createdAt,
)
