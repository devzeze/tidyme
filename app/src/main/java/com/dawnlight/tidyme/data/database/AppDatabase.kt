package com.dawnlight.tidyme.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dawnlight.tidyme.data.database.dao.EventDao
import com.dawnlight.tidyme.data.database.entity.Event

@Database(
    entities = [Event::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE events ADD COLUMN category TEXT NOT NULL DEFAULT 'SELF'")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE events ADD COLUMN nextExecutionTimestamp INTEGER")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create new table without category column
                db.execSQL("""
                    CREATE TABLE events_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        eventType TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        occurrenceType TEXT NOT NULL,
                        repeatType TEXT,
                        repeatFrequency INTEGER,
                        lastExecutionTimestamp INTEGER,
                        nextExecutionTimestamp INTEGER,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())

                // Copy data from old table to new table
                db.execSQL("""
                    INSERT INTO events_new (id, eventType, title, description, occurrenceType,
                        repeatType, repeatFrequency, lastExecutionTimestamp, nextExecutionTimestamp, createdAt)
                    SELECT id, eventType, title, description, occurrenceType,
                        repeatType, repeatFrequency, lastExecutionTimestamp, nextExecutionTimestamp, createdAt
                    FROM events
                """.trimIndent())

                // Drop old table
                db.execSQL("DROP TABLE events")

                // Rename new table to original name
                db.execSQL("ALTER TABLE events_new RENAME TO events")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tidyme_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}