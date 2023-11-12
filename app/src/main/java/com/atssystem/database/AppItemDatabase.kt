package com.atssystem.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AppItemEntity::class], version = 1)
abstract class AppItemDatabase: RoomDatabase() {
    abstract fun appItemDao(): AppItemDao
}