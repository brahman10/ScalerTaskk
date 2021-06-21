package com.example.scalertaskk

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class InterviewModel(
    @ColumnInfo(name = "email") var emails: Email,
    @ColumnInfo(name = "start_time") var startTime: String?,
    @ColumnInfo(name = "end_time") var endTime: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "resume") var resume: String?
) :Serializable{
    @PrimaryKey(autoGenerate = true) var id: Int=0
}

class Email(var emails: List<String>):Serializable

