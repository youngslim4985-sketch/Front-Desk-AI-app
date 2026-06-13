package com.example.data

import android.content.Context
import androidx.room.*
import com.example.data.api.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FrontDeskRepository(private val database: AppDatabase) {

    private val callLogDao = database.callLogDao()
    private val meetingDao = database.meetingDao()
    private val bookingDao = database.bookingDao()
    private val faqDao = database.faqDao()

    val allCallLogs: Flow<List<CallLog>> = callLogDao.getAllCallLogs()
    val allMeetings: Flow<List<Meeting>> = meetingDao.getAllMeetings()
    val allBookings: Flow<List<Booking>> = bookingDao.getAllBookings()
    val allFaqs: Flow<List<Faq>> = faqDao.getAllFaqs()

    // Ensure initial baseline dataset is loaded
    suspend fun checkAndLoadMockData() = withContext(Dispatchers.IO) {
        val currentLogs = callLogDao.getAllCallLogs().first()
        if (currentLogs.isEmpty()) {
            loadInitialMockData()
        }
    }

    private suspend fun loadInitialMockData() {
        // 1. Load Initial Call Logs
        val mockCalls = listOf(
            CallLog(
                visitorName = "John Smith",
                phoneNumber = "201-555-0143",
                reason = "Meeting with CEO to negotiate new office space lease.",
                status = "Answered",
                timestamp = System.currentTimeMillis() - 3600 * 1000, // 1 hour ago
                durationSeconds = 145,
                summary = "Negotiated office floor lease configurations and base pricing. John requested detailed floor plans of the fourth floor suites.",
                sentiment = "Positive",
                followUpText = "Email 4th Floor plans and standard pricing matrix by Monday.",
                tags = "Leasing, Client"
            ),
            CallLog(
                visitorName = "Sarah Lee",
                phoneNumber = "312-555-0291",
                reason = "Courier package delivery for Terrance Franklin.",
                status = "Missed",
                timestamp = System.currentTimeMillis() - 2100 * 1000, // 35 min ago
                durationSeconds = 0,
                summary = "Courier arrived looking for Terrance Franklin. Front-desk receptionist was temporarily away. Package was logged but call was missed.",
                sentiment = "Neutral",
                followUpText = "Secure and hand over the physical parcel in the reception lockbox.",
                tags = "Delivery, Urgent"
            ),
            CallLog(
                visitorName = "James Cooper",
                phoneNumber = "415-555-0374",
                reason = "Inquiry regarding building visitor parking validation.",
                status = "Answered",
                timestamp = System.currentTimeMillis() - 10800 * 1000, // 3 hours ago
                durationSeconds = 62,
                summary = "Inquired if customer parking is fully validated. Informed visitor that we provide 2 hours of complimentary validation.",
                sentiment = "Positive",
                followUpText = "None",
                tags = "Inquiry"
            ),
            CallLog(
                visitorName = "Emily Wood",
                phoneNumber = "617-555-0453",
                reason = "Seed round interest and corporate overview request.",
                status = "Answered",
                timestamp = System.currentTimeMillis() - 18000 * 1000, // 5 hours ago
                durationSeconds = 310,
                summary = "Investor from Wood Capital inquired about revenue models, and booked a slot. Highly productive chat.",
                sentiment = "Positive",
                followUpText = "Prepare Investor Deck and financial projections model.",
                tags = "Investor, Funding"
            ),
            CallLog(
                visitorName = "Unknown Telemarketer",
                phoneNumber = "800-555-0100",
                reason = "Solar panels automated sales blast.",
                status = "Missed",
                timestamp = System.currentTimeMillis() - 36000 * 1000, // 10 hours ago
                durationSeconds = 0,
                summary = "Spam automated solar panels marketing recording. Automatically flagged and blocked.",
                sentiment = "Negative",
                followUpText = "No Action Needed",
                tags = "Spam, Blocked"
            )
        )
        callLogDao.insertAll(mockCalls)

        // 2. Load Initial Meetings
        val mockMeetings = listOf(
            Meeting(
                title = "Investor Pitch Meeting",
                guestName = "Terrance Franklin",
                scheduledTime = "09:00 AM",
                hostName = "Marcus Brody",
                room = "Conference Room A",
                status = "Confirmed",
                summary = "Reviewing the Series A seed round milestones and commercial growth strategies."
            ),
            Meeting(
                title = "Office Space Consultation",
                guestName = "Sarah Peterson",
                scheduledTime = "11:30 AM",
                hostName = "Emma Watson",
                room = "Executive Boardroom",
                status = "Pending",
                summary = "Touring empty executive suites on fourth floor and reviewing initial lease terms."
            ),
            Meeting(
                title = "Prospective Tenant Walkthrough",
                guestName = "Robert Vance",
                scheduledTime = "02:00 PM",
                hostName = "Terrance Franklin",
                room = "Lobby Reception",
                status = "Confirmed",
                summary = "Walkthrough of co-working hot desk spaces and amenity lounge parameters."
            ),
            Meeting(
                title = "Commercial Lease Review",
                guestName = "Alexander Hamilton",
                scheduledTime = "04:30 PM",
                hostName = "Marcus Brody",
                room = "Room 102",
                status = "No-Show",
                summary = "Finalizing statutory lease agreements and safety compliance documentation."
            )
        )
        meetingDao.insertAll(mockMeetings)

        // 3. Load Initial Bookings
        val mockBookings = listOf(
            Booking(
                visitorName = "Daniel Craig",
                companyName = "MI6 Productions",
                purpose = "Client Consultation",
                dateTime = "June 15, 11:00 AM",
                hostName = "Marcus Brody",
                status = "Approved"
            ),
            Booking(
                visitorName = "Jessica Alba",
                companyName = "The Honest Co",
                purpose = "Vendor Pitch Presentation",
                dateTime = "June 16, 02:30 PM",
                hostName = "Terrance Franklin",
                status = "Pending"
            ),
            Booking(
                visitorName = "Bruce Wayne",
                companyName = "Wayne Enterprises",
                purpose = "High Priority Facility Tour",
                dateTime = "June 18, 04:00 PM",
                hostName = "Emma Watson",
                status = "Approved"
            )
        )
        bookingDao.insertAll(mockBookings)

        // 4. Load Initial FAQs
        val mockFaqs = listOf(
            Faq(
                category = "Directory",
                question = "Where is Marcus Brody's office located?",
                answer = "Marcus Brody (CEO) is in Suite 402 on the 4th Floor. Access via the main lobby elevators, taking a left upon exit."
            ),
            Faq(
                category = "Office Hours",
                question = "What are the building's operating hours?",
                answer = "The main desk reception operates Monday to Friday from 8:00 AM to 6:00 PM. Badge access for valid tenants is active 24/7."
            ),
            Faq(
                category = "Parking",
                question = "Does the facility offer parking validation?",
                answer = "Yes! Underground parking on levels P1 and P2 is free for the first 2 hours. Validate your parking ticket at the front desk."
            ),
            Faq(
                category = "Wi-Fi",
                question = "How do visitors connect to the guest Wi-Fi?",
                answer = "Connect to network 'FrontDesk_Guest' with password 'smartreceptionist2026'. No browser sign-in is required."
            ),
            Faq(
                category = "Emergency",
                question = "What is the emergency safety protocol?",
                answer = "In case of fire evacuation, proceed out of primary emergency exits towards the Oak Street Assembly Area. Main emergency desk is at ext 9111."
            )
        )
        faqDao.insertAll(mockFaqs)
    }

    // --- Write Helpers ---
    suspend fun insertCallLog(callLog: CallLog) = callLogDao.insertCallLog(callLog)
    suspend fun deleteCallLog(id: Int) = callLogDao.deleteCallLog(id)

    suspend fun insertMeeting(meeting: Meeting) = meetingDao.insertMeeting(meeting)
    suspend fun deleteMeeting(id: Int) = meetingDao.deleteMeeting(id)

    suspend fun insertBooking(booking: Booking) = bookingDao.insertBooking(booking)
    suspend fun deleteBooking(id: Int) = bookingDao.deleteBooking(id)

    suspend fun insertFaq(faq: Faq) = faqDao.insertFaq(faq)

    // --- AI/Simulation Business Logic Features ---

    /**
     * Simulates a new phone call using the Gemini API.
     * Generates a call, parses summary, sentiment, tags, and writes to database.
     */
    suspend fun simulateAICall(callerName: String, businessCategory: String): CallLog = withContext(Dispatchers.IO) {
        val prompt = """
            Create a realistic telephone call log between a client and an AI receptionist for a '$businessCategory' office.
            The caller's name is '$callerName'.
            Return your response in strict valid JSON format, with exactly these fields:
            {
              "phoneNumber": "XXX-XXX-XXXX format",
              "reason": "Brief summary of why they called under 15 words",
              "status": "Either 'Answered' or 'Missed'",
              "durationSeconds": Number representing length of conversation (0 if Missed),
              "summary": "Detailed summary of conversation under 30 words",
              "sentiment": "Either 'Positive', 'Neutral', or 'Negative'",
              "followUpText": "Call follow up directive like email or schedule under 15 words",
              "tags": "Two comma separated keywords e.g. 'Leasing, Rent'"
            }
            Do not include any Markdown tags like ```json or prefix text. Just raw JSON.
        """.trimIndent()

        val rawResponse = GeminiClient.generate(prompt, isJson = true)
        
        // Parse the response
        try {
            // Strip any accidental markdown formatting if present
            val cleaned = rawResponse.substringAfter("```json")
                .substringBefore("```")
                .trim()
            val json = JSONObject(cleaned)
            
            val log = CallLog(
                visitorName = callerName,
                phoneNumber = json.optString("phoneNumber", "555-0199"),
                reason = json.optString("reason", "Inquiry about lease pricing"),
                status = json.optString("status", "Answered"),
                timestamp = System.currentTimeMillis(),
                durationSeconds = json.optInt("durationSeconds", 120),
                summary = json.optString("summary", "Inquired about general spacing. AI resolved questions."),
                sentiment = json.optString("sentiment", "Positive"),
                followUpText = json.optString("followUpText", "No Action Required"),
                tags = json.optString("tags", "General, Inquiry")
            )
            
            callLogDao.insertCallLog(log)
            log
        } catch (e: Exception) {
            // Fallback mock call if API fails or lacks internet
            val isAnswered = java.util.Random().nextBoolean()
            val log = CallLog(
                visitorName = callerName,
                phoneNumber = "555-${(100..999).random()}-${(1000..9999).random()}",
                reason = "Simulated voicemail regarding $businessCategory",
                status = if (isAnswered) "Answered" else "Missed",
                timestamp = System.currentTimeMillis(),
                durationSeconds = if (isAnswered) (30..180).random() else 0,
                summary = "High-fidelity simulated caller $callerName who checked for details about office suite hours and parking availability.",
                sentiment = listOf("Positive", "Neutral").random(),
                followUpText = "Return call to $callerName ASAP.",
                tags = "SIM, Auto"
            )
            callLogDao.insertCallLog(log)
            log
        }
    }

    /**
     * Converse with Gemini as an AI receptionist to help schedule/book a visitor.
     * Takes in current conversation history, and can optionally auto-book details.
     */
    suspend fun converseAndBookVisitor(conversation: String, businessCategory: String): Pair<String, Booking?> = withContext(Dispatchers.IO) {
        val systemInstruction = """
            You are 'Front-Desk AI', the virtual front-desk receptionist for a '$businessCategory' workspace office. 
            Help the visitor schedule an appointment or booking. 
            Be concise, warm, and highly professional. Limit responses to 2 sentences.
            
            If the user has successfully provided their Name, Company, Purpose of Visit, Date/Time, and Host Name:
            At the VERY END of your response, add a strict trigger line formatted exactly like this to auto-book them:
            [AUTO_BOOK: {"name": "NAME", "company": "COMPANY", "purpose": "PURPOSE", "dateTime": "DATE_TIME", "host": "HOST"}]
            
            Otherwise, politely ask for whichever information is missing. Keep guiding them friendly!
        """.trimIndent()

        val chatbotPrompt = """
            Conversation history:
            $conversation
            
            Generate your next friendly virtual assistant response:
        """.trimIndent()

        val response = GeminiClient.generate(chatbotPrompt, systemInstruction = systemInstruction)
        
        // Parse if booking has been triggered
        var booking: Booking? = null
        if (response.contains("[AUTO_BOOK:")) {
            try {
                val jsonPart = response.substringAfter("[AUTO_BOOK:").substringBefore("]")
                val json = JSONObject(jsonPart)
                booking = Booking(
                    visitorName = json.optString("name", "Unknown"),
                    companyName = json.optString("company", "Individual"),
                    purpose = json.optString("purpose", "Visitor Pre-Registration"),
                    dateTime = json.optString("dateTime", "Soon"),
                    hostName = json.optString("host", "Terrance Franklin"),
                    status = "Approved"
                )
                bookingDao.insertBooking(booking)
            } catch (e: Exception) {
                // Ignore parse failures
            }
        }
        
        // Return cleaned message (without the bracket trigger) and booking if any
        val cleanMsg = response.substringBefore("[AUTO_BOOK:").trim()
        Pair(cleanMsg, booking)
    }

    /**
     * Direct query assistant to answer questions about office faqs using Gemini context
     */
    suspend fun consultFAQ(question: String): String = withContext(Dispatchers.IO) {
        val faqs = faqDao.getAllFaqs().first()
        val faqContext = faqs.joinToString("\n") { "Category: ${it.category} | Q: ${it.question} | A: ${it.answer}" }

        val system = """
            You are the Front-Desk building AI. You answer visitor questions using only the listed building instructions if relevant. 
            If the answer is not in the directory context, state it politely and provide a general helpful reception guess. Keep it under 2 sentences.
        """.trimIndent()

        val prompt = """
            Building Directory Context:
            $faqContext
            
            Visitor's Question:
            $question
            
            Your friendly response:
        """.trimIndent()

        GeminiClient.generate(prompt, systemInstruction = system)
    }

    /**
     * Returns predictive AI recommendations and business insights based on database statistics
     */
    suspend fun getPredictiveAIRecommendations(businessCategory: String): String = withContext(Dispatchers.IO) {
        val logs = callLogDao.getAllCallLogs().first()
        val meetings = meetingDao.getAllMeetings().first()
        
        val logsSummary = logs.take(10).joinToString("\n") { "${it.visitorName} - Status: ${it.status} - Reason: ${it.reason}" }
        val meetingsSummary = meetings.joinToString("\n") { "${it.title} with ${it.guestName} at ${it.scheduledTime} - Status: ${it.status}" }

        val prompt = """
            You are a senior Operations Analyzer of a office in the category '$businessCategory'. 
            Based on the actual activities below, write 2 concise AI actionable bullet recommendations.
            Each bullet must start with a '👉' and be direct (e.g. 'Add call receptionists from 10am-1pm due to high Missed calls of delivery couriers').
            Keep the content ultra-compact and professional.

            Recent Phone Calls:
            $logsSummary

            Today's Meetings:
            $meetingsSummary
        """.trimIndent()

        val response = GeminiClient.generate(prompt)
        if (response.startsWith("ERROR") || response.length < 5) {
            // High fidelity default fallback
            """
                👉 Add receptionist coverage from 10 AM–1 PM due to peak commercial lease inquiries.
                👉 Proactively send automated Wi-Fi and parking validation reminders to scheduled guests.
            """.trimIndent()
        } else {
            response
        }
    }
}
