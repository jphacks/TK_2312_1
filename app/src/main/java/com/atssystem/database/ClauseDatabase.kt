package com.atssystem.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ClauseEntity::class], version = 2)
abstract class ClauseDatabase: RoomDatabase() {
    abstract fun clauseDao(): ClauseDao

    companion object {
        private const val Database_NAME = "risky_clause"

        /**
         * As we need only one instance of db in our app will use to store
         * This is to avoid memory leaks in android when there exist multiple instances of db
         */
        @Volatile
        private var INSTANCE: ClauseDatabase? = null

        fun getInstance(context: Context): ClauseDatabase {
            /**
             *  Context should be application context
             */
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ClauseDatabase::class.java,
                        Database_NAME
                    ).build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}