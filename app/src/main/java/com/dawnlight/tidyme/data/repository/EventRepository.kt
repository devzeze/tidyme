package com.dawnlight.tidyme.data.repository

import com.dawnlight.tidyme.data.database.dao.EventDao
import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventCategory
import com.dawnlight.tidyme.data.database.entity.EventType
import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {
    fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    fun getAllEventsOrdered(): Flow<List<Event>> = eventDao.getAllEventsOrdered()

    fun getEventsByType(eventType: EventType): Flow<List<Event>> =
        eventDao.getEventsByType(eventType)

    fun getEventsByCategory(category: EventCategory): Flow<List<Event>> =
        eventDao.getEventsByCategory(category)

    suspend fun getEventById(id: Int): Event? = eventDao.getEventById(id)

    suspend fun insertEvent(event: Event): Long = eventDao.insertEvent(event)

    suspend fun updateEvent(event: Event) = eventDao.updateEvent(event)

    suspend fun deleteEvent(event: Event) = eventDao.deleteEvent(event)

    suspend fun updateLastExecutionTimestamp(id: Int, timestamp: Long) =
        eventDao.updateLastExecutionTimestamp(id, timestamp)

    suspend fun deleteAllEvents() = eventDao.deleteAllEvents()
}