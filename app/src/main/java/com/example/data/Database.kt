package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- 1. Call Log Entity ---
@Entity(tableName = "call_logs")
data class CallLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val visitorName: String,
    val phoneNumber: String,
    val reason: String,
    val status: String, // "Answered", "Missed"
    val timestamp: Long,
    val durationSeconds: Int,
    val summary: String,
    val sentiment: String, // "Positive", "Neutral", "Negative"
    val followUpText: String,
    val tags: String // Comma-separated strings
)

// --- 2. Meeting Entity ---
@Entity(tableName = "meetings")
data class Meeting(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val guestName: String,
    val scheduledTime: String, // e.g. "09:00 AM"
    val hostName: String,
    val room: String, // e.g. "Conference Room A"
    val status: String, // "Confirmed", "Pending", "Checked-In", "No-Show"
    val summary: String
)

// --- 3. Visitor Booking Entity ---
@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val visitorName: String,
    val companyName: String,
    val purpose: String,
    val dateTime: String, // e.g. "June 13, 10:00 AM"
    val hostName: String,
    val status: String // "Pending", "Approved", "Denied"
)

// --- 4. FAQ / Directory Entity ---
@Entity(tableName = "faqs")
data class Faq(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // "Directory", "Office Hours", "Parking", "Wi-Fi", "Emergency"
    val question: String,
    val answer: String
)

// --- DAOs ---

@Dao
interface CallLogDao {
    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callLog: CallLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<CallLog>)

    @Query("DELETE FROM call_logs WHERE id = :id")
    suspend fun deleteCallLog(id: Int)

    @Query("DELETE FROM call_logs")
    suspend fun clear()
}

@Dao
interface MeetingDao {
    @Query("SELECT * FROM meetings ORDER BY scheduledTime ASC")
    fun getAllMeetings(): Flow<List<Meeting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: Meeting)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meetings: List<Meeting>)

    @Query("DELETE FROM meetings WHERE id = :id")
    suspend fun deleteMeeting(id: Int)

    @Query("DELETE FROM meetings")
    suspend fun clear()
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookings: List<Booking>)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBooking(id: Int)

    @Query("DELETE FROM bookings")
    suspend fun clear()
}

@Dao
interface FaqDao {
    @Query("SELECT * FROM faqs ORDER BY category ASC")
    fun getAllFaqs(): Flow<List<Faq>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaq(faq: Faq)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(faqs: List<Faq>)

    @Query("DELETE FROM faqs")
    suspend fun clear()
}

// --- App Database ---

@Database(entities = [CallLog::class, Meeting::class, Booking::class, Faq::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callLogDao(): CallLogDao
    abstract fun meetingDao(): MeetingDao
    abstract fun bookingDao(): BookingDao
    abstract fun faqDao(): FaqDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "front_desk_ai_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
