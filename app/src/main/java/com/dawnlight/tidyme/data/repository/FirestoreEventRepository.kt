package com.dawnlight.tidyme.data.repository

import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.data.firebase.FirebaseManager
import com.dawnlight.tidyme.data.firebase.asEventFlow
import com.dawnlight.tidyme.data.firebase.toEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreEventRepository {
    private val firestore: FirebaseFirestore = FirebaseManager.getFirestore()

    private suspend fun getUserEventsCollection() =
        firestore.collection("events")
            .document(FirebaseManager.ensureAuthenticated())
            .collection("user_events")

    fun getAllEvents(): Flow<List<Event>> {
        return kotlinx.coroutines.flow.flow {
            val collection = getUserEventsCollection()
            collection.orderBy("createdAt", Query.Direction.DESCENDING)
                .asEventFlow()
                .collect { emit(it) }
        }
    }

    fun getAllEventsOrdered(): Flow<List<Event>> {
        return getAllEvents().map { events ->
            events.sortedWith(
                compareBy<Event> { it.lastExecutionTimestamp != null }
                    .thenByDescending { it.lastExecutionTimestamp ?: 0L }
                    .thenByDescending { it.createdAt }
            )
        }
    }

    fun getEventsByType(eventType: EventType): Flow<List<Event>> {
        return kotlinx.coroutines.flow.flow {
            val collection = getUserEventsCollection()
            collection.whereEqualTo("eventType", eventType.name)
                .asEventFlow()
                .collect { emit(it) }
        }
    }

    suspend fun getEventById(id: String): Event? {
        val collection = getUserEventsCollection()
        val document = collection.document(id).get().await()
        return document.toEvent()
    }

    suspend fun insertEvent(event: Event): String {
        val collection = getUserEventsCollection()
        val docRef = collection.document()
        docRef.set(event.toMap()).await()
        return docRef.id
    }

    suspend fun updateEvent(event: Event) {
        val collection = getUserEventsCollection()
        collection.document(event.id).set(event.toMap()).await()
    }

    suspend fun deleteEvent(event: Event) {
        val collection = getUserEventsCollection()
        collection.document(event.id).delete().await()
    }

    suspend fun updateLastExecutionTimestamp(id: String, timestamp: Long) {
        val collection = getUserEventsCollection()
        collection.document(id).update("lastExecutionTimestamp", timestamp).await()
    }

    suspend fun deleteAllEvents() {
        val collection = getUserEventsCollection()
        val documents = collection.get().await()

        // Batch delete all documents
        val batch = firestore.batch()
        documents.documents.forEach { document ->
            batch.delete(document.reference)
        }
        batch.commit().await()
    }
}
