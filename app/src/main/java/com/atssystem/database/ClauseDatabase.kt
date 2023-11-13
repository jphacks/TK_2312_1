package com.atssystem.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ClauseEntity::class], version = 1)
abstract class ClauseDatabase: RoomDatabase() {
    abstract fun clauseDao(): ClauseDao
}