package com.factory.macromindainutritioncoach.`data`.local.dao

import android.database.Cursor
import androidx.room.CoroutinesRoom
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.factory.macromindainutritioncoach.`data`.local.entity.FoodEntry
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class FoodEntryDao_Impl(
  __db: RoomDatabase,
) : FoodEntryDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfFoodEntry: EntityInsertionAdapter<FoodEntry>

  private val __deletionAdapterOfFoodEntry: EntityDeletionOrUpdateAdapter<FoodEntry>
  init {
    this.__db = __db
    this.__insertionAdapterOfFoodEntry = object : EntityInsertionAdapter<FoodEntry>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `food_entries` (`id`,`name`,`calories`,`protein`,`carbs`,`fat`,`serving_size`,`serving_unit`,`meal_type`,`date_string`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: FoodEntry) {
        statement.bindLong(1, entity.id)
        statement.bindString(2, entity.name)
        statement.bindLong(3, entity.calories.toLong())
        statement.bindDouble(4, entity.protein.toDouble())
        statement.bindDouble(5, entity.carbs.toDouble())
        statement.bindDouble(6, entity.fat.toDouble())
        statement.bindDouble(7, entity.servingSize.toDouble())
        statement.bindString(8, entity.servingUnit)
        statement.bindString(9, entity.mealType)
        statement.bindString(10, entity.dateString)
        statement.bindLong(11, entity.timestamp)
      }
    }
    this.__deletionAdapterOfFoodEntry = object : EntityDeletionOrUpdateAdapter<FoodEntry>(__db) {
      protected override fun createQuery(): String = "DELETE FROM `food_entries` WHERE `id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: FoodEntry) {
        statement.bindLong(1, entity.id)
      }
    }
  }

  public override suspend fun insertFoodEntry(foodEntry: FoodEntry): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfFoodEntry.insert(foodEntry)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun deleteFoodEntry(foodEntry: FoodEntry): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __deletionAdapterOfFoodEntry.handle(foodEntry)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override fun getFoodEntriesForDate(date: String): Flow<List<FoodEntry>> {
    val _sql: String = "SELECT * FROM food_entries WHERE date_string = ? ORDER BY timestamp ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, date)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("food_entries"), object :
        Callable<List<FoodEntry>> {
      public override fun call(): List<FoodEntry> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfCalories: Int = getColumnIndexOrThrow(_cursor, "calories")
          val _cursorIndexOfProtein: Int = getColumnIndexOrThrow(_cursor, "protein")
          val _cursorIndexOfCarbs: Int = getColumnIndexOrThrow(_cursor, "carbs")
          val _cursorIndexOfFat: Int = getColumnIndexOrThrow(_cursor, "fat")
          val _cursorIndexOfServingSize: Int = getColumnIndexOrThrow(_cursor, "serving_size")
          val _cursorIndexOfServingUnit: Int = getColumnIndexOrThrow(_cursor, "serving_unit")
          val _cursorIndexOfMealType: Int = getColumnIndexOrThrow(_cursor, "meal_type")
          val _cursorIndexOfDateString: Int = getColumnIndexOrThrow(_cursor, "date_string")
          val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_cursor, "timestamp")
          val _result: MutableList<FoodEntry> = ArrayList<FoodEntry>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: FoodEntry
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpCalories: Int
            _tmpCalories = _cursor.getInt(_cursorIndexOfCalories)
            val _tmpProtein: Float
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein)
            val _tmpCarbs: Float
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs)
            val _tmpFat: Float
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat)
            val _tmpServingSize: Float
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize)
            val _tmpServingUnit: String
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit)
            val _tmpMealType: String
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType)
            val _tmpDateString: String
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString)
            val _tmpTimestamp: Long
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp)
            _item =
                FoodEntry(_tmpId,_tmpName,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpServingSize,_tmpServingUnit,_tmpMealType,_tmpDateString,_tmpTimestamp)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun getAllFoodEntries(): Flow<List<FoodEntry>> {
    val _sql: String = "SELECT * FROM food_entries ORDER BY timestamp DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("food_entries"), object :
        Callable<List<FoodEntry>> {
      public override fun call(): List<FoodEntry> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfCalories: Int = getColumnIndexOrThrow(_cursor, "calories")
          val _cursorIndexOfProtein: Int = getColumnIndexOrThrow(_cursor, "protein")
          val _cursorIndexOfCarbs: Int = getColumnIndexOrThrow(_cursor, "carbs")
          val _cursorIndexOfFat: Int = getColumnIndexOrThrow(_cursor, "fat")
          val _cursorIndexOfServingSize: Int = getColumnIndexOrThrow(_cursor, "serving_size")
          val _cursorIndexOfServingUnit: Int = getColumnIndexOrThrow(_cursor, "serving_unit")
          val _cursorIndexOfMealType: Int = getColumnIndexOrThrow(_cursor, "meal_type")
          val _cursorIndexOfDateString: Int = getColumnIndexOrThrow(_cursor, "date_string")
          val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_cursor, "timestamp")
          val _result: MutableList<FoodEntry> = ArrayList<FoodEntry>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: FoodEntry
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpCalories: Int
            _tmpCalories = _cursor.getInt(_cursorIndexOfCalories)
            val _tmpProtein: Float
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein)
            val _tmpCarbs: Float
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs)
            val _tmpFat: Float
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat)
            val _tmpServingSize: Float
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize)
            val _tmpServingUnit: String
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit)
            val _tmpMealType: String
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType)
            val _tmpDateString: String
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString)
            val _tmpTimestamp: Long
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp)
            _item =
                FoodEntry(_tmpId,_tmpName,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpServingSize,_tmpServingUnit,_tmpMealType,_tmpDateString,_tmpTimestamp)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun getFoodEntriesForDateRange(startDate: String, endDate: String):
      Flow<List<FoodEntry>> {
    val _sql: String =
        "SELECT * FROM food_entries WHERE date_string >= ? AND date_string <= ? ORDER BY date_string ASC, timestamp ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, startDate)
    _argIndex = 2
    _statement.bindString(_argIndex, endDate)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("food_entries"), object :
        Callable<List<FoodEntry>> {
      public override fun call(): List<FoodEntry> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfCalories: Int = getColumnIndexOrThrow(_cursor, "calories")
          val _cursorIndexOfProtein: Int = getColumnIndexOrThrow(_cursor, "protein")
          val _cursorIndexOfCarbs: Int = getColumnIndexOrThrow(_cursor, "carbs")
          val _cursorIndexOfFat: Int = getColumnIndexOrThrow(_cursor, "fat")
          val _cursorIndexOfServingSize: Int = getColumnIndexOrThrow(_cursor, "serving_size")
          val _cursorIndexOfServingUnit: Int = getColumnIndexOrThrow(_cursor, "serving_unit")
          val _cursorIndexOfMealType: Int = getColumnIndexOrThrow(_cursor, "meal_type")
          val _cursorIndexOfDateString: Int = getColumnIndexOrThrow(_cursor, "date_string")
          val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_cursor, "timestamp")
          val _result: MutableList<FoodEntry> = ArrayList<FoodEntry>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: FoodEntry
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpCalories: Int
            _tmpCalories = _cursor.getInt(_cursorIndexOfCalories)
            val _tmpProtein: Float
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein)
            val _tmpCarbs: Float
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs)
            val _tmpFat: Float
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat)
            val _tmpServingSize: Float
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize)
            val _tmpServingUnit: String
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit)
            val _tmpMealType: String
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType)
            val _tmpDateString: String
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString)
            val _tmpTimestamp: Long
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp)
            _item =
                FoodEntry(_tmpId,_tmpName,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpServingSize,_tmpServingUnit,_tmpMealType,_tmpDateString,_tmpTimestamp)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun getTotalCaloriesForDate(date: String): Flow<Int?> {
    val _sql: String = "SELECT SUM(calories) FROM food_entries WHERE date_string = ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, date)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("food_entries"), object : Callable<Int?> {
      public override fun call(): Int? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Int?
          if (_cursor.moveToFirst()) {
            val _tmp: Int?
            if (_cursor.isNull(0)) {
              _tmp = null
            } else {
              _tmp = _cursor.getInt(0)
            }
            _result = _tmp
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
