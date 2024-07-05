package com.kostuciy.data.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kostuciy.data.core.db.converter.Converter
import com.kostuciy.data.core.db.dao.AuthDao
import com.kostuciy.data.core.db.entity.MessengerUserEntity
import com.kostuciy.data.core.db.entity.TokenEntity
import com.kostuciy.data.core.db.entity.UserEntity

@Database(entities = [UserEntity::class, MessengerUserEntity::class, TokenEntity::class], version = 1)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): AuthDao
}