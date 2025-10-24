package com.example.foodtracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Database(
    entities = [User::class, Food::class, Entry::class, UserProfile::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun foodDao(): FoodDao
    abstract fun entryDao(): EntryDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "food.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Copy food data from asset to the newly created database
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                // Attach the asset database and copy food table data
                                val assetDbPath = copyAssetDatabase(context)
                                db.execSQL("ATTACH DATABASE '$assetDbPath' AS asset_db")
                                db.execSQL("INSERT INTO food SELECT * FROM asset_db.food")
                                db.execSQL("DETACH DATABASE asset_db")
                                // Clean up temporary file
                                File(assetDbPath).delete()
                            } catch (e: Exception) {
                                android.util.Log.e("AppDatabase", "Error copying food data", e)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
        }

        private fun copyAssetDatabase(context: Context): String {
            val tempFile = File(context.cacheDir, "temp_food_catalog.db")
            context.assets.open("databases/food_catalog.db").use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            return tempFile.absolutePath
        }
    }
}
