package com.echo.journal.`data`.local

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class EchoDao_Impl(
  __db: RoomDatabase,
) : EchoDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfJournalEntryEntity: EntityInsertAdapter<JournalEntryEntity>

  private val __insertAdapterOfMoodCheckInEntity: EntityInsertAdapter<MoodCheckInEntity>

  private val __deleteAdapterOfJournalEntryEntity: EntityDeleteOrUpdateAdapter<JournalEntryEntity>

  private val __deleteAdapterOfMoodCheckInEntity: EntityDeleteOrUpdateAdapter<MoodCheckInEntity>

  private val __updateAdapterOfJournalEntryEntity: EntityDeleteOrUpdateAdapter<JournalEntryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfJournalEntryEntity = object : EntityInsertAdapter<JournalEntryEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `journal_entries` (`id`,`title`,`body`,`moodKey`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: JournalEntryEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.body)
        val _tmpMoodKey: String? = entity.moodKey
        if (_tmpMoodKey == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpMoodKey)
        }
        statement.bindLong(5, entity.createdAt)
        statement.bindLong(6, entity.updatedAt)
      }
    }
    this.__insertAdapterOfMoodCheckInEntity = object : EntityInsertAdapter<MoodCheckInEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `mood_check_ins` (`id`,`moodKey`,`note`,`createdAt`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MoodCheckInEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.moodKey)
        statement.bindText(3, entity.note)
        statement.bindLong(4, entity.createdAt)
      }
    }
    this.__deleteAdapterOfJournalEntryEntity = object : EntityDeleteOrUpdateAdapter<JournalEntryEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `journal_entries` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: JournalEntryEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__deleteAdapterOfMoodCheckInEntity = object : EntityDeleteOrUpdateAdapter<MoodCheckInEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `mood_check_ins` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MoodCheckInEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfJournalEntryEntity = object : EntityDeleteOrUpdateAdapter<JournalEntryEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `journal_entries` SET `id` = ?,`title` = ?,`body` = ?,`moodKey` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: JournalEntryEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.body)
        val _tmpMoodKey: String? = entity.moodKey
        if (_tmpMoodKey == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpMoodKey)
        }
        statement.bindLong(5, entity.createdAt)
        statement.bindLong(6, entity.updatedAt)
        statement.bindLong(7, entity.id)
      }
    }
  }

  public override suspend fun insertEntry(entry: JournalEntryEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfJournalEntryEntity.insertAndReturnId(_connection, entry)
    _result
  }

  public override suspend fun insertCheckIn(checkIn: MoodCheckInEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfMoodCheckInEntity.insertAndReturnId(_connection, checkIn)
    _result
  }

  public override suspend fun deleteEntry(entry: JournalEntryEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfJournalEntryEntity.handle(_connection, entry)
  }

  public override suspend fun deleteCheckIn(checkIn: MoodCheckInEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfMoodCheckInEntity.handle(_connection, checkIn)
  }

  public override suspend fun updateEntry(entry: JournalEntryEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfJournalEntryEntity.handle(_connection, entry)
  }

  public override fun observeEntries(): Flow<List<JournalEntryEntity>> {
    val _sql: String = "SELECT * FROM journal_entries ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("journal_entries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfMoodKey: Int = getColumnIndexOrThrow(_stmt, "moodKey")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<JournalEntryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: JournalEntryEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpMoodKey: String?
          if (_stmt.isNull(_columnIndexOfMoodKey)) {
            _tmpMoodKey = null
          } else {
            _tmpMoodKey = _stmt.getText(_columnIndexOfMoodKey)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = JournalEntryEntity(_tmpId,_tmpTitle,_tmpBody,_tmpMoodKey,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeEntry(id: Long): Flow<JournalEntryEntity?> {
    val _sql: String = "SELECT * FROM journal_entries WHERE id = ?"
    return createFlow(__db, false, arrayOf("journal_entries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfMoodKey: Int = getColumnIndexOrThrow(_stmt, "moodKey")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: JournalEntryEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpMoodKey: String?
          if (_stmt.isNull(_columnIndexOfMoodKey)) {
            _tmpMoodKey = null
          } else {
            _tmpMoodKey = _stmt.getText(_columnIndexOfMoodKey)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = JournalEntryEntity(_tmpId,_tmpTitle,_tmpBody,_tmpMoodKey,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findEntry(id: Long): JournalEntryEntity? {
    val _sql: String = "SELECT * FROM journal_entries WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfMoodKey: Int = getColumnIndexOrThrow(_stmt, "moodKey")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: JournalEntryEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpMoodKey: String?
          if (_stmt.isNull(_columnIndexOfMoodKey)) {
            _tmpMoodKey = null
          } else {
            _tmpMoodKey = _stmt.getText(_columnIndexOfMoodKey)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = JournalEntryEntity(_tmpId,_tmpTitle,_tmpBody,_tmpMoodKey,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeCheckIns(): Flow<List<MoodCheckInEntity>> {
    val _sql: String = "SELECT * FROM mood_check_ins ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("mood_check_ins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfMoodKey: Int = getColumnIndexOrThrow(_stmt, "moodKey")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<MoodCheckInEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MoodCheckInEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpMoodKey: String
          _tmpMoodKey = _stmt.getText(_columnIndexOfMoodKey)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = MoodCheckInEntity(_tmpId,_tmpMoodKey,_tmpNote,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteEntryById(id: Long) {
    val _sql: String = "DELETE FROM journal_entries WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCheckInById(id: Long) {
    val _sql: String = "DELETE FROM mood_check_ins WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
