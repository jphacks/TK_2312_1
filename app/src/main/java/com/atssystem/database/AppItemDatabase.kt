package com.atssystem.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AppItemEntity::class], version = 1)
abstract class AppItemDatabase: RoomDatabase() {
    abstract fun appItemDao(): AppItemDao

    companion object {
        private const val Database_NAME = "app_infos"

        /**
         * As we need only one instance of db in our app will use to store
         * This is to avoid memory leaks in android when there exist multiple instances of db
         */
        @Volatile
        private var INSTANCE: AppItemDatabase? = null

        fun getInstance(context: Context): AppItemDatabase {
            /**
             * "Context" should be application context
             */
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppItemDatabase::class.java,
                        Database_NAME
                    ).build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
