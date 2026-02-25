package com.dawnlight.tidyme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.data.repository.FirestoreEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val repository = FirestoreEventRepository()
    val allEvents: Flow<List<Event>> = repository.getAllEvents()
    val allEventsOrdered: Flow<List<Event>> = repository.getAllEventsOrdered()

    fun insertEvent(event: Event) = viewModelScope.launch {
        repository.insertEvent(event)
    }

    fun updateEvent(event: Event) = viewModelScope.launch {
        repository.updateEvent(event)
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        repository.deleteEvent(event)
    }

    fun updateLastExecutionTimestamp(id: String, timestamp: Long) = viewModelScope.launch {
        repository.updateLastExecutionTimestamp(id, timestamp)
    }

    fun getEventsByType(eventType: EventType): Flow<List<Event>> =
        repository.getEventsByType(eventType)
}