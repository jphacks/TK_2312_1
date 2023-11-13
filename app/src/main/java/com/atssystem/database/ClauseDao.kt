package com.atssystem.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.atssystem.model.Clause

@Dao
interface ClauseDao {

    @Query("select * from risky_clause where package_name = :packageName")
    fun getClauses(packageName: String): List<ClauseEntity>

    @Insert
    fun saveNewClauses(list: List<ClauseEntity>)
}