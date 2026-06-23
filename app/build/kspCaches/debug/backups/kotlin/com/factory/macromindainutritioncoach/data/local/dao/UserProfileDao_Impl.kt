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
import com.factory.macromindainutritioncoach.`data`.local.entity.UserProfile
import java.lang.Class
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class UserProfileDao_Impl(
  __db: RoomDatabase,
) : UserProfileDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfUserProfile: EntityInsertionAdapter<UserProfile>
  init {
    this.__db = __db
    this.__insertionAdapterOfUserProfile = object : EntityInsertionAdapter<UserProfile>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `user_profile` (`id`,`name`,`age`,`weight_kg`,`height_cm`,`gender`,`activity_level`,`goal`,`target_calories`,`target_protein`,`target_carbs`,`target_fat`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: UserProfile) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindString(2, entity.name)
        statement.bindLong(3, entity.age.toLong())
        statement.bindDouble(4, entity.weightKg.toDouble())
        statement.bindDouble(5, entity.heightCm.toDouble())
        statement.bindString(6, entity.gender)
        statement.bindString(7, entity.activityLevel)
        statement.bindString(8, entity.goal)
        statement.bindLong(9, entity.targetCalories.toLong())
        statement.bindLong(10, entity.targetProtein.toLong())
        statement.bindLong(11, entity.targetCarbs.toLong())
        statement.bindLong(12, entity.targetFat.toLong())
      }
    }
  }

  public override suspend fun insertOrUpdate(userProfile: UserProfile): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfUserProfile.insert(userProfile)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override fun getUserProfile(): Flow<UserProfile?> {
    val _sql: String = "SELECT * FROM user_profile WHERE id = 1 LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("user_profile"), object :
        Callable<UserProfile?> {
      public override fun call(): UserProfile? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfAge: Int = getColumnIndexOrThrow(_cursor, "age")
          val _cursorIndexOfWeightKg: Int = getColumnIndexOrThrow(_cursor, "weight_kg")
          val _cursorIndexOfHeightCm: Int = getColumnIndexOrThrow(_cursor, "height_cm")
          val _cursorIndexOfGender: Int = getColumnIndexOrThrow(_cursor, "gender")
          val _cursorIndexOfActivityLevel: Int = getColumnIndexOrThrow(_cursor, "activity_level")
          val _cursorIndexOfGoal: Int = getColumnIndexOrThrow(_cursor, "goal")
          val _cursorIndexOfTargetCalories: Int = getColumnIndexOrThrow(_cursor, "target_calories")
          val _cursorIndexOfTargetProtein: Int = getColumnIndexOrThrow(_cursor, "target_protein")
          val _cursorIndexOfTargetCarbs: Int = getColumnIndexOrThrow(_cursor, "target_carbs")
          val _cursorIndexOfTargetFat: Int = getColumnIndexOrThrow(_cursor, "target_fat")
          val _result: UserProfile?
          if (_cursor.moveToFirst()) {
            val _tmpId: Int
            _tmpId = _cursor.getInt(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpAge: Int
            _tmpAge = _cursor.getInt(_cursorIndexOfAge)
            val _tmpWeightKg: Float
            _tmpWeightKg = _cursor.getFloat(_cursorIndexOfWeightKg)
            val _tmpHeightCm: Float
            _tmpHeightCm = _cursor.getFloat(_cursorIndexOfHeightCm)
            val _tmpGender: String
            _tmpGender = _cursor.getString(_cursorIndexOfGender)
            val _tmpActivityLevel: String
            _tmpActivityLevel = _cursor.getString(_cursorIndexOfActivityLevel)
            val _tmpGoal: String
            _tmpGoal = _cursor.getString(_cursorIndexOfGoal)
            val _tmpTargetCalories: Int
            _tmpTargetCalories = _cursor.getInt(_cursorIndexOfTargetCalories)
            val _tmpTargetProtein: Int
            _tmpTargetProtein = _cursor.getInt(_cursorIndexOfTargetProtein)
            val _tmpTargetCarbs: Int
            _tmpTargetCarbs = _cursor.getInt(_cursorIndexOfTargetCarbs)
            val _tmpTargetFat: Int
            _tmpTargetFat = _cursor.getInt(_cursorIndexOfTargetFat)
            _result =
                UserProfile(_tmpId,_tmpName,_tmpAge,_tmpWeightKg,_tmpHeightCm,_tmpGender,_tmpActivityLevel,_tmpGoal,_tmpTargetCalories,_tmpTargetProtein,_tmpTargetCarbs,_tmpTargetFat)
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

  public override suspend fun getUserProfileOnce(): UserProfile? {
    val _sql: String = "SELECT * FROM user_profile WHERE id = 1 LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<UserProfile?> {
      public override fun call(): UserProfile? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfName: Int = getColumnIndexOrThrow(_cursor, "name")
          val _cursorIndexOfAge: Int = getColumnIndexOrThrow(_cursor, "age")
          val _cursorIndexOfWeightKg: Int = getColumnIndexOrThrow(_cursor, "weight_kg")
          val _cursorIndexOfHeightCm: Int = getColumnIndexOrThrow(_cursor, "height_cm")
          val _cursorIndexOfGender: Int = getColumnIndexOrThrow(_cursor, "gender")
          val _cursorIndexOfActivityLevel: Int = getColumnIndexOrThrow(_cursor, "activity_level")
          val _cursorIndexOfGoal: Int = getColumnIndexOrThrow(_cursor, "goal")
          val _cursorIndexOfTargetCalories: Int = getColumnIndexOrThrow(_cursor, "target_calories")
          val _cursorIndexOfTargetProtein: Int = getColumnIndexOrThrow(_cursor, "target_protein")
          val _cursorIndexOfTargetCarbs: Int = getColumnIndexOrThrow(_cursor, "target_carbs")
          val _cursorIndexOfTargetFat: Int = getColumnIndexOrThrow(_cursor, "target_fat")
          val _result: UserProfile?
          if (_cursor.moveToFirst()) {
            val _tmpId: Int
            _tmpId = _cursor.getInt(_cursorIndexOfId)
            val _tmpName: String
            _tmpName = _cursor.getString(_cursorIndexOfName)
            val _tmpAge: Int
            _tmpAge = _cursor.getInt(_cursorIndexOfAge)
            val _tmpWeightKg: Float
            _tmpWeightKg = _cursor.getFloat(_cursorIndexOfWeightKg)
            val _tmpHeightCm: Float
            _tmpHeightCm = _cursor.getFloat(_cursorIndexOfHeightCm)
            val _tmpGender: String
            _tmpGender = _cursor.getString(_cursorIndexOfGender)
            val _tmpActivityLevel: String
            _tmpActivityLevel = _cursor.getString(_cursorIndexOfActivityLevel)
            val _tmpGoal: String
            _tmpGoal = _cursor.getString(_cursorIndexOfGoal)
            val _tmpTargetCalories: Int
            _tmpTargetCalories = _cursor.getInt(_cursorIndexOfTargetCalories)
            val _tmpTargetProtein: Int
            _tmpTargetProtein = _cursor.getInt(_cursorIndexOfTargetProtein)
            val _tmpTargetCarbs: Int
            _tmpTargetCarbs = _cursor.getInt(_cursorIndexOfTargetCarbs)
            val _tmpTargetFat: Int
            _tmpTargetFat = _cursor.getInt(_cursorIndexOfTargetFat)
            _result =
                UserProfile(_tmpId,_tmpName,_tmpAge,_tmpWeightKg,_tmpHeightCm,_tmpGender,_tmpActivityLevel,_tmpGoal,_tmpTargetCalories,_tmpTargetProtein,_tmpTargetCarbs,_tmpTargetFat)
          } else {
            _result = null
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
