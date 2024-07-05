package com.kostuciy.data.core.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kostuciy.data.core.db.entity.MessengerType

class Converter {

    @TypeConverter
    fun jsonFromMessengerType(messengerType: MessengerType): String =
        Gson().toJson(messengerType)

    @TypeConverter
    fun messengerTypeFromJson(json: String): MessengerType =
        Gson().fromJson(
            json,
            object : TypeToken<MessengerType>() {}.type
        )
}