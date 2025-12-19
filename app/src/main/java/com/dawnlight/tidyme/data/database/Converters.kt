package com.dawnlight.tidyme.data.database

import androidx.room.TypeConverter
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.data.database.entity.OccurrenceType
import com.dawnlight.tidyme.data.database.entity.RepeatType

class Converters {
    @TypeConverter
    fun fromEventType(value: EventType): String {
        return value.name
    }

    @TypeConverter
    fun toEventType(value: String): EventType {
        return EventType.valueOf(value)
    }

    @TypeConverter
    fun fromOccurrenceType(value: OccurrenceType): String {
        return value.name
    }

    @TypeConverter
    fun toOccurrenceType(value: String): OccurrenceType {
        return OccurrenceType.valueOf(value)
    }

    @TypeConverter
    fun fromRepeatType(value: RepeatType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toRepeatType(value: String?): RepeatType? {
        return value?.let { RepeatType.valueOf(it) }
    }
}