package com.saheli.whu.news.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.saheli.whu.news.api.News
import com.saheli.whu.news.db.dao.FavoriteNewsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [News::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteNewsDao(): FavoriteNewsDao

    companion object {

        private const val DATABASE_NAME = "app_database"
        private var instance: AppDatabase? = null

        private val isDatabaseCreated = MutableLiveData<Boolean>()
        val databaseCreated: LiveData<Boolean> = isDatabaseCreated

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = createDb(context)
                    if (isDatabaseCreated(context, DATABASE_NAME)) {
                        isDatabaseCreated.postValue(true)
                    }
                }
            }
            return instance!!
        }

        private fun createDb(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        GlobalScope.launch(Dispatchers.IO) {
                            val database = getInstance(context)
                            database.withTransaction {
                                database.favoriteNewsDao().insertNews(*DataGenerator.news)
                            }
                        }
                    }
                })
                .build()
        }

        private fun isDatabaseCreated(context: Context, databaseName: String): Boolean =
            context.getDatabasePath(databaseName).exists()
    }

}