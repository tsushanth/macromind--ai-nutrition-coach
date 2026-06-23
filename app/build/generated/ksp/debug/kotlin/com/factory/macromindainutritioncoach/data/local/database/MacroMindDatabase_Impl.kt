package com.factory.macromindainutritioncoach.`data`.local.database

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.room.RoomOpenHelper
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.factory.macromindainutritioncoach.`data`.local.dao.CommonFoodDao
import com.factory.macromindainutritioncoach.`data`.local.dao.CommonFoodDao_Impl
import com.factory.macromindainutritioncoach.`data`.local.dao.FoodEntryDao
import com.factory.macromindainutritioncoach.`data`.local.dao.FoodEntryDao_Impl
import com.factory.macromindainutritioncoach.`data`.local.dao.UserProfileDao
import com.factory.macromindainutritioncoach.`data`.local.dao.UserProfileDao_Impl
import java.lang.Class
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import javax.`annotation`.processing.Generated
import kotlin.Any
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.Set

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class MacroMindDatabase_Impl : MacroMindDatabase() {
  private val _foodEntryDao: Lazy<FoodEntryDao> = lazy {
    FoodEntryDao_Impl(this)
  }


  private val _userProfileDao: Lazy<UserProfileDao> = lazy {
    UserProfileDao_Impl(this)
  }


  private val _commonFoodDao: Lazy<CommonFoodDao> = lazy {
    CommonFoodDao_Impl(this)
  }


  protected override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
    val _openCallback: SupportSQLiteOpenHelper.Callback = RoomOpenHelper(config, object :
        RoomOpenHelper.Delegate(1) {
      public override fun createAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `food_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `calories` INTEGER NOT NULL, `protein` REAL NOT NULL, `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `serving_size` REAL NOT NULL, `serving_unit` TEXT NOT NULL, `meal_type` TEXT NOT NULL, `date_string` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_profile` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `age` INTEGER NOT NULL, `weight_kg` REAL NOT NULL, `height_cm` REAL NOT NULL, `gender` TEXT NOT NULL, `activity_level` TEXT NOT NULL, `goal` TEXT NOT NULL, `target_calories` INTEGER NOT NULL, `target_protein` INTEGER NOT NULL, `target_carbs` INTEGER NOT NULL, `target_fat` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `common_foods` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `calories_per_100g` INTEGER NOT NULL, `protein_per_100g` REAL NOT NULL, `carbs_per_100g` REAL NOT NULL, `fat_per_100g` REAL NOT NULL, `default_serving_size` REAL NOT NULL, `default_serving_unit` TEXT NOT NULL, `category` TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'b65d82a40caed57c564e4663fd4f4f0c')")
      }

      public override fun dropAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `food_entries`")
        db.execSQL("DROP TABLE IF EXISTS `user_profile`")
        db.execSQL("DROP TABLE IF EXISTS `common_foods`")
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onDestructiveMigration(db)
          }
        }
      }

      public override fun onCreate(db: SupportSQLiteDatabase) {
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onCreate(db)
          }
        }
      }

      public override fun onOpen(db: SupportSQLiteDatabase) {
        mDatabase = db
        internalInitInvalidationTracker(db)
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onOpen(db)
          }
        }
      }

      public override fun onPreMigrate(db: SupportSQLiteDatabase) {
        dropFtsSyncTriggers(db)
      }

      public override fun onPostMigrate(db: SupportSQLiteDatabase) {
      }

      public override fun onValidateSchema(db: SupportSQLiteDatabase):
          RoomOpenHelper.ValidationResult {
        val _columnsFoodEntries: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(11)
        _columnsFoodEntries.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodEntries.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodEntries.put("calories", TableInfo.Column("calories", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodEntries.put("protein", TableInfo.Column("protein", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodEntries.put("carbs", TableInfo.Column("carbs", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodEntries.put("fat", TableInfo.Column("fat", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodEntries.put("serving_size", TableInfo.Column("serving_size", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodEntries.put("serving_unit", TableInfo.Column("serving_unit", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodEntries.put("meal_type", TableInfo.Column("meal_type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodEntries.put("date_string", TableInfo.Column("date_string", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFoodEntries.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFoodEntries: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesFoodEntries: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoFoodEntries: TableInfo = TableInfo("food_entries", _columnsFoodEntries,
            _foreignKeysFoodEntries, _indicesFoodEntries)
        val _existingFoodEntries: TableInfo = read(db, "food_entries")
        if (!_infoFoodEntries.equals(_existingFoodEntries)) {
          return RoomOpenHelper.ValidationResult(false, """
              |food_entries(com.factory.macromindainutritioncoach.data.local.entity.FoodEntry).
              | Expected:
              |""".trimMargin() + _infoFoodEntries + """
              |
              | Found:
              |""".trimMargin() + _existingFoodEntries)
        }
        val _columnsUserProfile: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(12)
        _columnsUserProfile.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("age", TableInfo.Column("age", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("weight_kg", TableInfo.Column("weight_kg", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("height_cm", TableInfo.Column("height_cm", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("gender", TableInfo.Column("gender", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("activity_level", TableInfo.Column("activity_level", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("goal", TableInfo.Column("goal", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("target_calories", TableInfo.Column("target_calories", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("target_protein", TableInfo.Column("target_protein", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("target_carbs", TableInfo.Column("target_carbs", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProfile.put("target_fat", TableInfo.Column("target_fat", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUserProfile: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesUserProfile: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoUserProfile: TableInfo = TableInfo("user_profile", _columnsUserProfile,
            _foreignKeysUserProfile, _indicesUserProfile)
        val _existingUserProfile: TableInfo = read(db, "user_profile")
        if (!_infoUserProfile.equals(_existingUserProfile)) {
          return RoomOpenHelper.ValidationResult(false, """
              |user_profile(com.factory.macromindainutritioncoach.data.local.entity.UserProfile).
              | Expected:
              |""".trimMargin() + _infoUserProfile + """
              |
              | Found:
              |""".trimMargin() + _existingUserProfile)
        }
        val _columnsCommonFoods: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(9)
        _columnsCommonFoods.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommonFoods.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommonFoods.put("calories_per_100g", TableInfo.Column("calories_per_100g",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommonFoods.put("protein_per_100g", TableInfo.Column("protein_per_100g", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommonFoods.put("carbs_per_100g", TableInfo.Column("carbs_per_100g", "REAL", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommonFoods.put("fat_per_100g", TableInfo.Column("fat_per_100g", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommonFoods.put("default_serving_size", TableInfo.Column("default_serving_size",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommonFoods.put("default_serving_unit", TableInfo.Column("default_serving_unit",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommonFoods.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCommonFoods: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesCommonFoods: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoCommonFoods: TableInfo = TableInfo("common_foods", _columnsCommonFoods,
            _foreignKeysCommonFoods, _indicesCommonFoods)
        val _existingCommonFoods: TableInfo = read(db, "common_foods")
        if (!_infoCommonFoods.equals(_existingCommonFoods)) {
          return RoomOpenHelper.ValidationResult(false, """
              |common_foods(com.factory.macromindainutritioncoach.data.local.entity.CommonFood).
              | Expected:
              |""".trimMargin() + _infoCommonFoods + """
              |
              | Found:
              |""".trimMargin() + _existingCommonFoods)
        }
        return RoomOpenHelper.ValidationResult(true, null)
      }
    }, "b65d82a40caed57c564e4663fd4f4f0c", "33a38d3271d868bebd68cc54920e3ba1")
    val _sqliteConfig: SupportSQLiteOpenHelper.Configuration =
        SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build()
    val _helper: SupportSQLiteOpenHelper = config.sqliteOpenHelperFactory.create(_sqliteConfig)
    return _helper
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: HashMap<String, String> = HashMap<String, String>(0)
    val _viewTables: HashMap<String, Set<String>> = HashMap<String, Set<String>>(0)
    return InvalidationTracker(this, _shadowTablesMap, _viewTables,
        "food_entries","user_profile","common_foods")
  }

  public override fun clearAllTables() {
    super.assertNotMainThread()
    val _db: SupportSQLiteDatabase = super.openHelper.writableDatabase
    try {
      super.beginTransaction()
      _db.execSQL("DELETE FROM `food_entries`")
      _db.execSQL("DELETE FROM `user_profile`")
      _db.execSQL("DELETE FROM `common_foods`")
      super.setTransactionSuccessful()
    } finally {
      super.endTransaction()
      _db.query("PRAGMA wal_checkpoint(FULL)").close()
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM")
      }
    }
  }

  protected override fun getRequiredTypeConverters(): Map<Class<out Any>, List<Class<out Any>>> {
    val _typeConvertersMap: HashMap<Class<out Any>, List<Class<out Any>>> =
        HashMap<Class<out Any>, List<Class<out Any>>>()
    _typeConvertersMap.put(FoodEntryDao::class.java, FoodEntryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(UserProfileDao::class.java, UserProfileDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CommonFoodDao::class.java, CommonFoodDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecs(): Set<Class<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: HashSet<Class<out AutoMigrationSpec>> =
        HashSet<Class<out AutoMigrationSpec>>()
    return _autoMigrationSpecsSet
  }

  public override
      fun getAutoMigrations(autoMigrationSpecs: Map<Class<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = ArrayList<Migration>()
    return _autoMigrations
  }

  public override fun foodEntryDao(): FoodEntryDao = _foodEntryDao.value

  public override fun userProfileDao(): UserProfileDao = _userProfileDao.value

  public override fun commonFoodDao(): CommonFoodDao = _commonFoodDao.value
}
