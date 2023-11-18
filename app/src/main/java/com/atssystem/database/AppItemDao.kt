package com.atssystem.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppItemDao {

    @Query("SELECT * FROM app_infos")
    fun loadAllApps(): List<AppItemEntity>

    @Query("SELECT * FROM app_infos where is_installed_lately = 1")
    fun loadLatelyInstalledApps(): List<AppItemEntity>

    @Query("SELECT * FROM app_infos where package_name = :packageName")
    fun loadApp(packageName: String): AppItemEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAppItem(appItemEntity: AppItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(appItems: List<AppItemEntity> )
}