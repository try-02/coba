package com.pos.offline.data

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.pos.offline.data.converter.DatabaseConverters
import com.pos.offline.data.entity.*

@Database(
    entities = [
        PersediaanEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ColumnTypeConverters(DatabaseConverters::class)
abstract class PosDatabase : RoomDatabase() {
    abstract fun persediaanDao(): com.pos.offline.data.dao.PersediaanDao
}