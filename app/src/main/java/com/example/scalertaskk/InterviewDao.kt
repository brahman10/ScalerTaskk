package com.example.scalertaskk

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface InterviewDao {
    @Query("SELECT * FROM interviewmodel")
    fun getAll(): List<InterviewModel>

    @Query("SELECT * FROM interviewmodel WHERE date >= :date")
    fun loadAll(date: String): List<InterviewModel>

    @Query("SELECT email FROM interviewmodel WHERE date LIKE :date AND start_time >= :start Or end_time <=:end")
    fun checkInterview(date: String, start: String , end:String): Email

    @Insert
    fun insertAll(interview: InterviewModel)

    @Delete
    fun delete(interviewModel: InterviewModel)
}