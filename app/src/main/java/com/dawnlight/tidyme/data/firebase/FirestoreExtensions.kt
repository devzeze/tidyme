package com.dawnlight.tidyme.data.firebase

import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventType
import com.dawnlight.tidyme.data.database.entity.OccurrenceType
import com.dawnlight.tidyme.data.database.entity.RepeatType
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Query.asEventFlow(): Flow<List<Event>> = callbackFlow {
    val listener = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close(error)
            return@addSnapshotListener
        }

        if (snapshot != null) {
            val events = snapshot.documents.mapNotNull { it.toEvent() }
            trySend(events)
        }
    }

    awaitClose { listener.remove() }
}

fun DocumentSnapshot.toEvent(): Event? {
    return try {
        Event(
            id = id,
            eventType = EventType.valueOf(getString("eventType") ?: return null),
            title = getString("title") ?: return null,
            description = getString("description") ?: "",
            occurrenceType = OccurrenceType.valueOf(getString("occurrenceType") ?: return null),
            repeatType = getString("repeatType")?.let { RepeatType.valueOf(it) },
            repeatFrequency = getLong("repeatFrequency")?.toInt(),
            lastExecutionTimestamp = getLong("lastExecutionTimestamp"),
            nextExecutionTimestamp = getLong("nextExecutionTimestamp"),
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        null
    }
}
