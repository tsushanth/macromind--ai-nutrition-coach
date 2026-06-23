package com.factory.macromindainutritioncoach.`data`.local.dao

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.factory.macromindainutritioncoach.`data`.local.entity.CommonFood
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
public class CommonFoodDao_Impl(
  __db: RoomDatabase,
) : CommonFoodDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfCommonFood: EntityInsertionAdapter<CommonFood>
  init {
    this.__db = __db
    this.__insertionAdapterOfCommonFood = object : EntityInsertionAdapter<CommonFood>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `common_foods` (`id`,`name`,`calories_per_100g`,`protein_per_100g`,`carbs_per_100g`,`fat_per_100g`,`default_serving_size`,`default_serving_unit`,`category`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: CommonFood) {
        statement.bindLong(1, entity.id)
        statement.bindString(2, entity.name)
        statement.bindLong(3, entity.caloriesPer100g.toLong())
        statement.bindDouble(4, entity.proteinPer100g.toDouble())
        statement.bindDouble(5, entity.carbsPer100g.toDouble())
        statement.bindDouble(6, entity.fatPer100g.toDouble())
        statement.bindDouble(7, entity.defaultServingSize.toDouble())
        statement.bindString(8, entity.defaultServingUnit)
        statement.bindString(9, entity.category)
      }
    }
  }

  public override suspend fun insertAll(vararg foods: CommonFood): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfCommonFood.insert(foods)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override fun searchFoods(query: String): Flow<List<CommonFood>> {
    val _sql: String =
        "SELECT * FROM common_foods WHERE name LIKE '%' || ? || '%' ORDER BY name ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, query)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("common_foods"), object :
        Callable<List<CommonFood>> {
      public override fun call(): List<CommonFood> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfCaloriesPer100g: Int = getColumnIndexOrThrow(_cursor,
              "calories_per_100g")
          val _cursorIndexOfProteinPer100g: Int = getColumnIndexOrThrow(_cursor, "protein_per_100g")
          val _cursorIndexOfCarbsPer100g: Int = getColumnIndexOrThrow(_cursor, "carbs_per_100g")
          val _cursorIndexOfFatPer100g: Int = getColumnIndexOrThrow(_cursor, "fat_per_100g")
          val _cursorIndexOfDefaultServingSize: Int = getColumnIndexOrThrow(_cursor,
              "default_serving_size")
          val _cursorIndexOfDefaultServingUnit: Int = getColumnIndexOrThrow(_cursor,
              "default_serving_unit")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _result: MutableList<CommonFood> = ArrayList<CommonFood>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CommonFood
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpCaloriesPer100g: Int
            _tmpCaloriesPer100g = _cursor.getInt(_cursorIndexOfCaloriesPer100g)
            val _tmpProteinPer100g: Float
            _tmpProteinPer100g = _cursor.getFloat(_cursorIndexOfProteinPer100g)
            val _tmpCarbsPer100g: Float
            _tmpCarbsPer100g = _cursor.getFloat(_cursorIndexOfCarbsPer100g)
            val _tmpFatPer100g: Float
            _tmpFatPer100g = _cursor.getFloat(_cursorIndexOfFatPer100g)
            val _tmpDefaultServingSize: Float
            _tmpDefaultServingSize = _cursor.getFloat(_cursorIndexOfDefaultServingSize)
            val _tmpDefaultServingUnit: String
            _tmpDefaultServingUnit = _cursor.getString(_cursorIndexOfDefaultServingUnit)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            _item =
                CommonFood(_tmpId,_tmpName,_tmpCaloriesPer100g,_tmpProteinPer100g,_tmpCarbsPer100g,_tmpFatPer100g,_tmpDefaultServingSize,_tmpDefaultServingUnit,_tmpCategory)
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

  public override fun getAllFoods(): Flow<List<CommonFood>> {
    val _sql: String = "SELECT * FROM common_foods ORDER BY name ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("common_foods"), object :
        Callable<List<CommonFood>> {
      public override fun call(): List<CommonFood> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfCaloriesPer100g: Int = getColumnIndexOrThrow(_cursor,
              "calories_per_100g")
          val _cursorIndexOfProteinPer100g: Int = getColumnIndexOrThrow(_cursor, "protein_per_100g")
          val _cursorIndexOfCarbsPer100g: Int = getColumnIndexOrThrow(_cursor, "carbs_per_100g")
          val _cursorIndexOfFatPer100g: Int = getColumnIndexOrThrow(_cursor, "fat_per_100g")
          val _cursorIndexOfDefaultServingSize: Int = getColumnIndexOrThrow(_cursor,
              "default_serving_size")
          val _cursorIndexOfDefaultServingUnit: Int = getColumnIndexOrThrow(_cursor,
              "default_serving_unit")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _result: MutableList<CommonFood> = ArrayList<CommonFood>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CommonFood
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpCaloriesPer100g: Int
            _tmpCaloriesPer100g = _cursor.getInt(_cursorIndexOfCaloriesPer100g)
            val _tmpProteinPer100g: Float
            _tmpProteinPer100g = _cursor.getFloat(_cursorIndexOfProteinPer100g)
            val _tmpCarbsPer100g: Float
            _tmpCarbsPer100g = _cursor.getFloat(_cursorIndexOfCarbsPer100g)
            val _tmpFatPer100g: Float
            _tmpFatPer100g = _cursor.getFloat(_cursorIndexOfFatPer100g)
            val _tmpDefaultServingSize: Float
            _tmpDefaultServingSize = _cursor.getFloat(_cursorIndexOfDefaultServingSize)
            val _tmpDefaultServingUnit: String
            _tmpDefaultServingUnit = _cursor.getString(_cursorIndexOfDefaultServingUnit)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            _item =
                CommonFood(_tmpId,_tmpName,_tmpCaloriesPer100g,_tmpProteinPer100g,_tmpCarbsPer100g,_tmpFatPer100g,_tmpDefaultServingSize,_tmpDefaultServingUnit,_tmpCategory)
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

  public override fun getFoodsByCategory(category: String): Flow<List<CommonFood>> {
    val _sql: String = "SELECT * FROM common_foods WHERE category = ? ORDER BY name ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, category)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("common_foods"), object :
        Callable<List<CommonFood>> {
      public override fun call(): List<CommonFood> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfCaloriesPer100g: Int = getColumnIndexOrThrow(_cursor,
              "calories_per_100g")
          val _cursorIndexOfProteinPer100g: Int = getColumnIndexOrThrow(_cursor, "protein_per_100g")
          val _cursorIndexOfCarbsPer100g: Int = getColumnIndexOrThrow(_cursor, "carbs_per_100g")
          val _cursorIndexOfFatPer100g: Int = getColumnIndexOrThrow(_cursor, "fat_per_100g")
          val _cursorIndexOfDefaultServingSize: Int = getColumnIndexOrThrow(_cursor,
              "default_serving_size")
          val _cursorIndexOfDefaultServingUnit: Int = getColumnIndexOrThrow(_cursor,
              "default_serving_unit")
          val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_cursor, "category")
          val _result: MutableList<CommonFood> = ArrayList<CommonFood>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CommonFood
            val _tmpId: Long
            _tmpId = _cursor.getLong(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpCaloriesPer100g: Int
            _tmpCaloriesPer100g = _cursor.getInt(_cursorIndexOfCaloriesPer100g)
            val _tmpProteinPer100g: Float
            _tmpProteinPer100g = _cursor.getFloat(_cursorIndexOfProteinPer100g)
            val _tmpCarbsPer100g: Float
            _tmpCarbsPer100g = _cursor.getFloat(_cursorIndexOfCarbsPer100g)
            val _tmpFatPer100g: Float
            _tmpFatPer100g = _cursor.getFloat(_cursorIndexOfFatPer100g)
            val _tmpDefaultServingSize: Float
            _tmpDefaultServingSize = _cursor.getFloat(_cursorIndexOfDefaultServingSize)
            val _tmpDefaultServingUnit: String
            _tmpDefaultServingUnit = _cursor.getString(_cursorIndexOfDefaultServingUnit)
            val _tmpCategory: String
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory)
            _item =
                CommonFood(_tmpId,_tmpName,_tmpCaloriesPer100g,_tmpProteinPer100g,_tmpCarbsPer100g,_tmpFatPer100g,_tmpDefaultServingSize,_tmpDefaultServingUnit,_tmpCategory)
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

  public override suspend fun getCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM common_foods"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Int> {
      public override fun call(): Int {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Int
          if (_cursor.moveToFirst()) {
            val _tmp: Int
            _tmp = _cursor.getInt(0)
            _result = _tmp
          } else {
            _result = 0
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
