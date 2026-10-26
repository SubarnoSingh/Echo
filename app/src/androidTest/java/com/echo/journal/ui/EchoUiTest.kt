package com.echo.journal.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.ParcelFileDescriptor
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.echo.journal.MainActivity
import com.echo.journal.data.local.EchoDatabase
import com.echo.journal.data.local.JournalEntryEntity
import com.echo.journal.data.local.MoodCheckInEntity
import com.echo.journal.domain.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class EchoUiTest {

    @get:Rule(order = 0)
    val reducedMotionRule: TestRule = object : TestRule {
        override fun apply(base: Statement, description: Description): Statement =
            object : Statement() {
                override fun evaluate() {
                    setAnimatorScale("0")
                    try {
                        base.evaluate()
                    } finally {
                        setAnimatorScale("1")
                    }
                }
            }
    }

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun moodAndNavigationExposeSelectionState() {
        val calm = composeRule
            .onAllNodes(hasContentDescription("Calm mood") and hasClickAction())
            .onFirst()
        calm.performClick()
        calm.assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))

        composeRule.onNodeWithText("Journal").performClick()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("Your journal").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Journal")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
    }

    @Test
    fun capturePrimaryScreensWithRecordedData() {
        seedData()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("One moment noticed today.").fetchSemanticsNodes().isNotEmpty()
        }
        capture("home")

        composeRule.onNodeWithText("Journal").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("A quieter morning").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("3 ECHOES").assertExists()
        capture("journal")

        composeRule.onNodeWithText("A quieter morning").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Edit this echo").fetchSemanticsNodes().isNotEmpty()
        }
        capture("reader")

        composeRule.onNodeWithContentDescription("Edit echo").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Editing echo").fetchSemanticsNodes().isNotEmpty()
        }
        capture("editor")

        composeRule.onNodeWithContentDescription("Close editor").performClick()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("Edit this echo").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("Your journal").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Insights").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Your rhythm").fetchSemanticsNodes().isNotEmpty()
        }
        capture("insights")

        composeRule.onAllNodes(hasScrollAction()).onFirst()
            .performScrollToNode(hasText("Recent check-ins"))
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("Recent check-ins").fetchSemanticsNodes().isNotEmpty()
        }
        capture("insights-bottom")

        val directory = screenshotDirectory()
        val names = directory.list()?.toSet().orEmpty()
        assertTrue(
            names.containsAll(
                setOf(
                    "home.png",
                    "journal.png",
                    "reader.png",
                    "editor.png",
                    "insights.png",
                    "insights-bottom.png",
                ),
            ),
        )
    }

    private fun seedData() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dao = EchoDatabase.get(context).echoDao()
        val zone = ZoneId.systemDefault()
        fun at(daysAgo: Long, hour: Int = 10): Long = LocalDate.now()
            .minusDays(daysAgo)
            .atTime(LocalTime.of(hour, 0))
            .atZone(zone)
            .toInstant()
            .toEpochMilli()

        runBlocking(Dispatchers.IO) {
            EchoDatabase.get(context).clearAllTables()

            dao.insertEntry(
                JournalEntryEntity(
                    title = "A quieter morning",
                    body = "The light came in slowly today. I made tea, opened the window, and let the room stay quiet for a while.",
                    moodKey = Mood.CALM.key,
                    createdAt = at(0, 8),
                    updatedAt = at(0, 8),
                ),
            )
            dao.insertEntry(
                JournalEntryEntity(
                    title = "Small courage",
                    body = "I said the thing I had been holding back. My hands shook, but the words landed, and the day felt lighter afterward.",
                    moodKey = Mood.GRATEFUL.key,
                    createdAt = at(1, 19),
                    updatedAt = at(1, 19),
                ),
            )
            dao.insertEntry(
                JournalEntryEntity(
                    title = "Too much noise",
                    body = "The afternoon was crowded with messages and small demands. I stepped outside for ten minutes and remembered how to breathe.",
                    moodKey = Mood.TIRED.key,
                    createdAt = at(3, 16),
                    updatedAt = at(3, 16),
                ),
            )

            dao.insertCheckIn(
                MoodCheckInEntity(
                    moodKey = Mood.CALM.key,
                    note = "A slower start, and that was enough.",
                    createdAt = at(0, 9),
                ),
            )
            dao.insertCheckIn(
                MoodCheckInEntity(
                    moodKey = Mood.HAPPY.key,
                    note = "",
                    createdAt = at(1, 12),
                ),
            )
            dao.insertCheckIn(
                MoodCheckInEntity(
                    moodKey = Mood.GRATEFUL.key,
                    note = "",
                    createdAt = at(2, 18),
                ),
            )
            dao.insertCheckIn(
                MoodCheckInEntity(
                    moodKey = Mood.TIRED.key,
                    note = "",
                    createdAt = at(4, 21),
                ),
            )
            dao.insertCheckIn(
                MoodCheckInEntity(
                    moodKey = Mood.NEUTRAL.key,
                    note = "",
                    createdAt = at(12, 14),
                ),
            )
        }
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("One moment noticed today.").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun capture(name: String) {
        composeRule.waitForIdle()
        val activity = composeRule.activity
        val view = activity.window.decorView
        var bitmap: Bitmap? = null
        val rendered = CountDownLatch(1)
        activity.runOnUiThread {
            val frame = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(frame))
            bitmap = frame
            rendered.countDown()
        }
        rendered.await()
        val file = File(screenshotDirectory(), "$name.png")
        FileOutputStream(file).use { output ->
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        assertTrue("Screenshot was not written: ${file.absolutePath}", file.length() > 0)
    }

    private fun screenshotDirectory(): File =
        File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, "screenshots")
            .apply { mkdirs() }

    private fun setAnimatorScale(value: String) {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("settings put global animator_duration_scale $value")
        ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
    }
}
