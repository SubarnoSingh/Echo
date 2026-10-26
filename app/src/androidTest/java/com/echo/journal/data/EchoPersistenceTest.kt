package com.echo.journal.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.echo.journal.data.local.EchoDatabase
import com.echo.journal.data.repository.EchoRepository
import com.echo.journal.domain.Mood
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EchoPersistenceTest {
    private lateinit var context: Context
    private lateinit var database: EchoDatabase
    private lateinit var repository: EchoRepository
    private val databaseName = "echo-persistence-test.db"
    private var now = 1_000L

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(databaseName)
        openDatabase()
    }

    private fun openDatabase() {
        database = Room.databaseBuilder(context, EchoDatabase::class.java, databaseName).build()
        repository = EchoRepository(database.echoDao(), now = { now })
    }

    @After
    fun tearDown() {
        database.close()
        context.deleteDatabase(databaseName)
    }

    @Test
    fun entriesPersistAcrossDatabaseReopen() = runBlocking {
        val id = repository.saveEntry(
            id = null,
            title = "  Evening walk  ",
            body = " The sky was violet. \n",
            mood = Mood.CALM,
        )
        now = 2_000L
        repository.addCheckIn(Mood.GRATEFUL, "  A kind message  ")
        database.close()

        openDatabase()
        val entries = repository.entries.first()
        val checkIns = repository.checkIns.first()

        assertEquals(1, entries.size)
        assertEquals(id, entries.single().id)
        assertEquals("Evening walk", entries.single().title)
        assertEquals("The sky was violet.", entries.single().body)
        assertEquals(Mood.CALM, entries.single().mood)
        assertEquals("A kind message", checkIns.single().note)
        assertEquals(Mood.GRATEFUL, checkIns.single().mood)
    }

    @Test
    fun editPreservesCreationAndUpdatesTimestamp() = runBlocking {
        val originalTime = now
        val id = repository.saveEntry(null, "First", "Before", Mood.NEUTRAL)
        now = originalTime + 500
        repository.saveEntry(id, "Second", "After", Mood.HAPPY)

        val entry = repository.findEntry(id)
        assertNotNull(entry)
        assertEquals("Second", entry!!.title)
        assertEquals("After", entry.body)
        assertEquals(Mood.HAPPY, entry.mood)
        assertEquals(originalTime, entry.createdAt)
        assertEquals(originalTime + 500, entry.updatedAt)
    }

    @Test
    fun deleteRemovesEntryAndCheckIn() = runBlocking {
        val entryId = repository.saveEntry(null, "Temporary", "Text", null)
        val checkIn = repository.checkIns.first().size
        assertEquals(0, checkIn)
        repository.addCheckIn(Mood.SAD, "")
        repository.deleteEntry(entryId)
        repository.checkIns.first().forEach { repository.deleteCheckIn(it.id) }

        assertNull(repository.findEntry(entryId))
        assertTrue(repository.entries.first().isEmpty())
        assertTrue(repository.checkIns.first().isEmpty())
    }

    @Test
    fun entriesAreOrderedNewestFirst() = runBlocking {
        now = 100L
        repository.saveEntry(null, "Oldest", "one", null)
        now = 200L
        repository.saveEntry(null, "Middle", "two", null)
        now = 300L
        repository.saveEntry(null, "Newest", "three", null)

        assertEquals(listOf("Newest", "Middle", "Oldest"), repository.entries.first().map { it.title })
    }

    @Test
    fun invalidStoredMoodDegradesToNoMood() = runBlocking {
        database.openHelper.writableDatabase.execSQL(
            "INSERT INTO journal_entries (title, body, moodKey, createdAt, updatedAt) VALUES ('Legacy', 'Text', 'unknown-mood', 10, 10)",
        )
        val entry = repository.entries.first().single()
        assertNull(entry.mood)
    }
}
