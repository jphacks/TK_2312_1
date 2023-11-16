package com.atssystem.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppItemDao {

    @Query("SELECT * FROM app_infos")
    fun loadAllApps(): List<AppItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAppItem(appItemEntity: AppItemEntity)

    @Insert
    fun saveAll(appItems: List<AppItemEntity> )
}