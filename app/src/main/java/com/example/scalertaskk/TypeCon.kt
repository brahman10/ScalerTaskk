package com.example.scalertaskk

import androidx.room.TypeConverter

class TypeCon {
    @TypeConverter
    fun storedStringToLanguages(value: String): Email {
        val langs = value.split("\\s*,\\s*")
        return Email(langs)
    }

    @TypeConverter
    fun languagesToStoredString(cl: Email): String {
        var value = ""
        for (lang in cl.emails) value += "$lang,"
        return value
    }
}