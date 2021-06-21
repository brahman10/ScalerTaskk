package com.example.scalertaskk

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [InterviewModel::class], version = 2,exportSchema = false)
@TypeConverters(TypeCon::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun interviewDao(): InterviewDao
}