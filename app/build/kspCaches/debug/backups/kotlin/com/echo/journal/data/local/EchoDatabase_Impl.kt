package com.echo.journal.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class EchoDatabase_Impl : EchoDatabase() {
  private val _echoDao: Lazy<EchoDao> = lazy {
    EchoDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "01f8767d07132bf73bb6a4d71743ae0a", "7612937eb73e45d59ad4c6f0d54a6068") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `journal_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `body` TEXT NOT NULL, `moodKey` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_journal_entries_createdAt` ON `journal_entries` (`createdAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_journal_entries_updatedAt` ON `journal_entries` (`updatedAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `mood_check_ins` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `moodKey` TEXT NOT NULL, `note` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_mood_check_ins_createdAt` ON `mood_check_ins` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '01f8767d07132bf73bb6a4d71743ae0a')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `journal_entries`")
        connection.execSQL("DROP TABLE IF EXISTS `mood_check_ins`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsJournalEntries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsJournalEntries.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsJournalEntries.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsJournalEntries.put("body", TableInfo.Column("body", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsJournalEntries.put("moodKey", TableInfo.Column("moodKey", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsJournalEntries.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsJournalEntries.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysJournalEntries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesJournalEntries: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesJournalEntries.add(TableInfo.Index("index_journal_entries_createdAt", false, listOf("createdAt"), listOf("ASC")))
        _indicesJournalEntries.add(TableInfo.Index("index_journal_entries_updatedAt", false, listOf("updatedAt"), listOf("ASC")))
        val _infoJournalEntries: TableInfo = TableInfo("journal_entries", _columnsJournalEntries, _foreignKeysJournalEntries, _indicesJournalEntries)
        val _existingJournalEntries: TableInfo = read(connection, "journal_entries")
        if (!_infoJournalEntries.equals(_existingJournalEntries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |journal_entries(com.echo.journal.data.local.JournalEntryEntity).
              | Expected:
              |""".trimMargin() + _infoJournalEntries + """
              |
              | Found:
              |""".trimMargin() + _existingJournalEntries)
        }
        val _columnsMoodCheckIns: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMoodCheckIns.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMoodCheckIns.put("moodKey", TableInfo.Column("moodKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMoodCheckIns.put("note", TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMoodCheckIns.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMoodCheckIns: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMoodCheckIns: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMoodCheckIns.add(TableInfo.Index("index_mood_check_ins_createdAt", false, listOf("createdAt"), listOf("ASC")))
        val _infoMoodCheckIns: TableInfo = TableInfo("mood_check_ins", _columnsMoodCheckIns, _foreignKeysMoodCheckIns, _indicesMoodCheckIns)
        val _existingMoodCheckIns: TableInfo = read(connection, "mood_check_ins")
        if (!_infoMoodCheckIns.equals(_existingMoodCheckIns)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |mood_check_ins(com.echo.journal.data.local.MoodCheckInEntity).
              | Expected:
              |""".trimMargin() + _infoMoodCheckIns + """
              |
              | Found:
              |""".trimMargin() + _existingMoodCheckIns)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "journal_entries", "mood_check_ins")
  }

  public override fun clearAllTables() {
    super.performClear(false, "journal_entries", "mood_check_ins")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(EchoDao::class, EchoDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun echoDao(): EchoDao = _echoDao.value
}
