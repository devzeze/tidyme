package com.dawnlight.tidyme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dawnlight.tidyme.data.database.AppDatabase
import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventRepository
    val allEvents: Flow<List<Event>>
    val allEventsOrdered: Flow<List<Event>>

    init {
        val eventDao = AppDatabase.getDatabase(application).eventDao()
        repository = EventRepository(eventDao)
        allEvents = repository.getAllEvents()
        allEventsOrdered = repository.getAllEventsOrdered()
    }

    fun insertEvent(event: Event) = viewModelScope.launch {
        repository.insertEvent(event)
    }

    fun updateEvent(event: Event) = viewModelScope.launch {
        repository.updateEvent(event)
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        repository.deleteEvent(event)
    }

    fun updateLastExecutionTimestamp(id: Int, timestamp: Long) = viewModelScope.launch {
        repository.updateLastExecutionTimestamp(id, timestamp)
    }

    fun getEventsByType(eventType: EventType): Flow<List<Event>> =
        repository.getEventsByType(eventType)
}