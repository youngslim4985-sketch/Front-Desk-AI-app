package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "Visitor" or "Front-Desk AI"
    val content: String,
    val isSystem: Boolean = false
)

class FrontDeskViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = FrontDeskRepository(database)

    // --- State Streams for UI ---
    val callLogs: StateFlow<List<CallLog>> = allItems(repository.allCallLogs)
    val meetings: StateFlow<List<Meeting>> = allItems(repository.allMeetings)
    val bookings: StateFlow<List<Booking>> = allItems(repository.allBookings)
    val faqs: StateFlow<List<Faq>> = allItems(repository.allFaqs)

    // Helper to keep flows active and convert to StateFlow
    private fun <T> allItems(flow: kotlinx.coroutines.flow.Flow<List<T>>): StateFlow<List<T>> {
        return flow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // --- Active General Settings State ---
    var businessCategory by mutableStateOf("Commercial Coworking")
    var receptionistName by mutableStateOf("Terrance Franklin")
    var isSimulatingCall by mutableStateOf(false)
    var aiRecommendations by mutableStateOf("👉 Add receptionist coverage from 10 AM–1 PM due to peak lease inquiries.\n👉 Proactively send parking validation instructions to scheduled guests.")
    var isLoadingRecommendations by mutableStateOf(false)

    // --- Add Operations Dialog/Expanded States ---
    var showAddMeetingDialog by mutableStateOf(false)
    var showAddBookingDialog by mutableStateOf(false)
    var showAddFaqDialog by mutableStateOf(false)

    // Chatbot Messages (Bookings & Info Conversation)
    var bookingChatMessages by mutableStateOf(
        listOf(
            ChatMessage("Front-Desk AI", "Hello! Welcome to our office smart booking platform. Who are you looking to meet, and what is your company name?", isSystem = false)
        )
    )
    var activeChatInput by mutableStateOf("")
    var isChatSending by mutableStateOf(false)

    // FAQ Consultation Active States
    var faqQuestionText by mutableStateOf("")
    var faqAnswerResult by mutableStateOf("")
    var isFaqSearching by mutableStateOf(false)

    init {
        viewModelScope.launch {
            repository.checkAndLoadMockData()
            refreshAIRecommendations()
        }
    }

    // --- Actions & Methods ---

    /**
     * Refreshes AI recommendations in background using Gemini's contextual logic
     */
    fun refreshAIRecommendations() {
        viewModelScope.launch {
            isLoadingRecommendations = true
            try {
                val response = repository.getPredictiveAIRecommendations(businessCategory)
                aiRecommendations = response
            } catch (e: Exception) {
                // Keep default
            } finally {
                isLoadingRecommendations = false
            }
        }
    }

    /**
     * Triggers simulated AI Call log creation
     */
    fun triggerSimulatedCall(callerName: String) {
        viewModelScope.launch {
            isSimulatingCall = true
            try {
                repository.simulateAICall(callerName, businessCategory)
                refreshAIRecommendations()
            } catch (e: Exception) {
                // Handled in repository gracefully
            } finally {
                isSimulatingCall = false
            }
        }
    }

    /**
     * Sends dynamic message to Chatbot for pre-registration in Bookings tab
     */
    fun sendBookingMessage(userText: String) {
        if (userText.trim().isEmpty()) return
        val updatedChat = bookingChatMessages + ChatMessage("Visitor", userText)
        bookingChatMessages = updatedChat
        activeChatInput = ""
        isChatSending = true

        viewModelScope.launch {
            try {
                // Gather full chat string context for Gemini
                val fullConversation = updatedChat.joinToString("\n") { "${it.sender}: ${it.content}" }
                val (aiResponse, autoBooking) = repository.converseAndBookVisitor(fullConversation, businessCategory)
                
                var botMsgList = bookingChatMessages + ChatMessage("Front-Desk AI", aiResponse)
                if (autoBooking != null) {
                    botMsgList = botMsgList + ChatMessage(
                        sender = "System",
                        content = "✅ Visitor Registered! Auto-booked: ${autoBooking.visitorName} from ${autoBooking.companyName} at ${autoBooking.dateTime} to see ${autoBooking.hostName}.",
                        isSystem = true
                    )
                }
                bookingChatMessages = botMsgList
            } catch (e: Exception) {
                bookingChatMessages = bookingChatMessages + ChatMessage("Front-Desk AI", "Sorry, my systems are temporarily busy. Please try typing again!")
            } finally {
                isChatSending = false
            }
        }
    }

    /**
     * Ask FAQ chatbot with smart retrieval mapping
     */
    fun askFaqQuery(question: String) {
        if (question.trim().isEmpty()) return
        faqQuestionText = question
        isFaqSearching = true
        faqAnswerResult = ""

        viewModelScope.launch {
            try {
                val answer = repository.consultFAQ(question)
                faqAnswerResult = answer
            } catch (e: Exception) {
                faqAnswerResult = "Error searching directory details."
            } finally {
                isFaqSearching = false
            }
        }
    }

    /**
     * Interactive DB Operations
     */
    fun deleteCallLog(id: Int) {
        viewModelScope.launch {
            repository.deleteCallLog(id)
            refreshAIRecommendations()
        }
    }

    fun addNewMeeting(title: String, guest: String, time: String, host: String, room: String, summary: String) {
        viewModelScope.launch {
            val meeting = Meeting(
                title = title,
                guestName = guest,
                scheduledTime = time,
                hostName = host,
                room = room,
                status = "Confirmed",
                summary = summary
            )
            repository.insertMeeting(meeting)
            refreshAIRecommendations()
        }
    }

    fun updateMeetingStatus(meeting: Meeting, newStatus: String) {
        viewModelScope.launch {
            val updated = meeting.copy(status = newStatus)
            repository.insertMeeting(updated)
            refreshAIRecommendations()
        }
    }

    fun deleteMeeting(id: Int) {
        viewModelScope.launch {
            repository.deleteMeeting(id)
            refreshAIRecommendations()
        }
    }

    fun addNewBooking(visitor: String, company: String, purpose: String, dateTime: String, host: String) {
        viewModelScope.launch {
            val booking = Booking(
                visitorName = visitor,
                companyName = company,
                purpose = purpose,
                dateTime = dateTime,
                hostName = host,
                status = "Approved"
            )
            repository.insertBooking(booking)
            refreshAIRecommendations()
        }
    }

    fun deleteBooking(id: Int) {
        viewModelScope.launch {
            repository.deleteBooking(id)
        }
    }

    fun addNewFaq(category: String, question: String, answer: String) {
        viewModelScope.launch {
            val faq = Faq(
                category = category,
                question = question,
                answer = answer
            )
            repository.insertFaq(faq)
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            database.callLogDao().clear()
            database.meetingDao().clear()
            database.bookingDao().clear()
            database.faqDao().clear()
            repository.checkAndLoadMockData()
            bookingChatMessages = listOf(
                ChatMessage("Front-Desk AI", "Hello! Welcome to our office smart booking platform. Who are you looking to meet, and what is your company name?", isSystem = false)
            )
            refreshAIRecommendations()
        }
    }
}
