package com.atssystem.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_infos")
data class AppItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "warning_num")
    val warnings: Int,
    @ColumnInfo(name = "app_name")
    val appName: String,
    @ColumnInfo(name = "is_installed_lately")
    val isInstalledLately: Boolean,
    @ColumnInfo(name = "unix_time")
    val time: Long
    /*
    This is an application label.
     */
)
