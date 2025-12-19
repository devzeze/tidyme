package com.dawnlight.tidyme.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dawnlight.tidyme.data.database.entity.Event
import com.dawnlight.tidyme.data.database.entity.EventType
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE eventType = :eventType ORDER BY createdAt DESC")
    fun getEventsByType(eventType: EventType): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Int): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("UPDATE events SET lastExecutionTimestamp = :timestamp WHERE id = :id")
    suspend fun updateLastExecutionTimestamp(id: Int, timestamp: Long)

    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()
}