package com.atssystem.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.atssystem.model.Clause

@Entity(tableName = "risky_clause")
data class ClauseEntity(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @Embedded
    val clause: Clause
)