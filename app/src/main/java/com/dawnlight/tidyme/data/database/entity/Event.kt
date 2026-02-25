package com.dawnlight.tidyme.data.database.entity

import java.util.Calendar

data class Event(
    val id: String = "",
    val eventType: EventType,
    val title: String,
    val description: String,
    val occurrenceType: OccurrenceType,
    val repeatType: RepeatType?,
    val repeatFrequency: Int?,
    val lastExecutionTimestamp: Long?,
    val nextExecutionTimestamp: Long?,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "eventType" to eventType.name,
            "title" to title,
            "description" to description,
            "occurrenceType" to occurrenceType.name,
            "repeatType" to repeatType?.name,
            "repeatFrequency" to repeatFrequency,
            "lastExecutionTimestamp" to lastExecutionTimestamp,
            "nextExecutionTimestamp" to nextExecutionTimestamp,
            "createdAt" to createdAt
        )
    }

    fun getNextExecutionTime(): Long? {
        if (occurrenceType == OccurrenceType.ONCE) {
            return if (lastExecutionTimestamp == null) createdAt else null
        }

        if (lastExecutionTimestamp == null) {
            return createdAt
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = lastExecutionTimestamp
        }

        when (repeatType) {
            RepeatType.DAYS -> calendar.add(Calendar.DAY_OF_YEAR, repeatFrequency ?: 1)
            RepeatType.WEEKS -> calendar.add(Calendar.WEEK_OF_YEAR, repeatFrequency ?: 1)
            RepeatType.MONTHS -> calendar.add(Calendar.MONTH, repeatFrequency ?: 1)
            null -> return null
        }
        return calendar.timeInMillis
    }
}

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