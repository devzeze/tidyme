package com.dawnlight.tidyme.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventType: EventType,
    val title: String,
    val description: String,
    val occurrenceType: OccurrenceType,
    val repeatType: RepeatType?,
    val repeatFrequency: Int?,
    val lastExecutionTimestamp: Long?,
    val createdAt: Long = System.currentTimeMillis()
)

enum class EventType {
    SINGLE,
    ROUTINE
}

enum class OccurrenceType {
    ONCE,
    REPEAT
}

enum class RepeatType {
    DAYS,
    WEEKS,
    MONTHS
}