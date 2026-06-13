package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Booking
import com.example.data.CallLog
import com.example.data.Faq
import com.example.data.Meeting
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class Screen(val title: String) {
    Dashboard("Dashboard"),
    Calls("Calls"),
    Meetings("Meetings"),
    Bookings("Bookings"),
    Analytics("Analytics"),
    Settings("Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrontDeskApp(viewModel: FrontDeskViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.Dashboard) }

    // Scaffold holding screen navigation
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_scaffold"),
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.Dashboard,
                    onClick = { currentScreen = Screen.Dashboard },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Calls,
                    onClick = { currentScreen = Screen.Calls },
                    icon = { Icon(Icons.Default.Call, contentDescription = "Calls") },
                    label = { Text("Calls", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Meetings,
                    onClick = { currentScreen = Screen.Meetings },
                    icon = { Icon(Icons.Default.List, contentDescription = "Meetings") },
                    label = { Text("Meetings", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Bookings,
                    onClick = { currentScreen = Screen.Bookings },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Bookings") },
                    label = { Text("Bookings", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Analytics,
                    onClick = { currentScreen = Screen.Analytics },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Analytics") },
                    label = { Text("Analytics", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Settings,
                    onClick = { currentScreen = Screen.Settings },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.Dashboard -> DashboardScreen(viewModel)
                Screen.Calls -> CallsScreen(viewModel)
                Screen.Meetings -> MeetingsScreen(viewModel)
                Screen.Bookings -> BookingsScreen(viewModel)
                Screen.Analytics -> AnalyticsScreen(viewModel)
                Screen.Settings -> SettingsScreen(viewModel)
            }
        }
    }
}

// ==========================================
// 1. DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(viewModel: FrontDeskViewModel) {
    val callLogs by viewModel.callLogs.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()

    var simulateNameInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Column {
                Text(
                    text = "Welcome, ${viewModel.receptionistName}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Operator dashboard for ${viewModel.businessCategory}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Metrics Grid (Calls Today, Meetings, Bookings)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Calls Today",
                    value = callLogs.size.toString(),
                    icon = Icons.Default.Call,
                    color = CoolBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Meetings",
                    value = meetings.size.toString(),
                    icon = Icons.Default.List,
                    color = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Bookings",
                    value = bookings.size.toString(),
                    icon = Icons.Default.Edit,
                    color = AmberOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Floating Simulation Control
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Simulate Automated AI Inbound Reception",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = simulateNameInput,
                            onValueChange = { simulateNameInput = it },
                            placeholder = { Text("Caller name (e.g. Brad Pitt)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sim_caller_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CoolBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(onGo = {
                                if (simulateNameInput.isNotBlank()) {
                                    viewModel.triggerSimulatedCall(simulateNameInput)
                                    simulateNameInput = ""
                                    focusManager.clearFocus()
                                }
                            })
                        )
                        Button(
                            onClick = {
                                val name = if (simulateNameInput.isNotBlank()) simulateNameInput else "Random Guest"
                                viewModel.triggerSimulatedCall(name)
                                simulateNameInput = ""
                                focusManager.clearFocus()
                            },
                            modifier = Modifier
                                .testTag("simulate_call_btn")
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CoolBlue),
                            enabled = !viewModel.isSimulatingCall
                        ) {
                            if (viewModel.isSimulatingCall) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Simulate")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Simulate")
                            }
                        }
                    }
                }
            }
        }

        // Today's Scheduled Appointments List Component
        item {
            TodayAppointmentsDisplay(viewModel = viewModel)
        }

        // Recent Calls Header
        item {
            Text(
                text = "Recent Receptionist Calls",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (callLogs.isNotEmpty()) {
            val recents = callLogs.take(3)
            items(recents) { log ->
                CallLogMiniCard(log = log)
            }
        } else {
            item {
                EmptyStateCard(message = "No calls logged yet today.")
            }
        }

        // Analytics Snapshot
        item {
            AnalyticsSnapshotView(viewModel)
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CallLogMiniCard(log: CallLog) {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTime = sdf.format(Date(log.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sentiment Indicator Bubble
            val color = when (log.sentiment.lowercase()) {
                "positive" -> EmeraldGreen
                "negative" -> CrimsonRed
                else -> SlateGray400
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = log.visitorName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = log.reason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AnalyticsSnapshotView(viewModel: FrontDeskViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Analytics & Predictions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lead Conversion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "27%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Peak Phone Hours",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "10 AM - 1 PM",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CoolBlue
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Predicted Leads",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "+18% MoM",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // AI Recommendations view inline
            Text(
                text = "AI Operations Recommendations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (viewModel.isLoadingRecommendations) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CoolBlue)
                }
            } else {
                Text(
                    text = viewModel.aiRecommendations,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ==========================================
// 2. CALL LOGS SCREEN
// ==========================================
@Composable
fun CallsScreen(viewModel: FrontDeskViewModel) {
    val callLogs by viewModel.callLogs.collectAsStateWithLifecycle()
    var inlineSimName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Calls Log & CRM",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "A complete history of incoming call records summarized and tagged by AI.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Quick simulation bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inlineSimName,
                    onValueChange = { inlineSimName = it },
                    placeholder = { Text("Simulate caller Name") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CoolBlue,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
                Button(
                    onClick = {
                        val nom = if (inlineSimName.isNotBlank()) inlineSimName else "Valued Client"
                        viewModel.triggerSimulatedCall(nom)
                        inlineSimName = ""
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.testTag("inline_sim_call_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = CoolBlue),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !viewModel.isSimulatingCall
                ) {
                    if (viewModel.isSimulatingCall) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Simulate")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (callLogs.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                EmptyStateCard(message = "Call list is temporarily empty. Use the simulator above to log calls instantly!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(callLogs, key = { it.id }) { log ->
                    CallLogDetailCard(log = log, onDelete = { viewModel.deleteCallLog(log.id) })
                }
            }
        }
    }
}

@Composable
fun CallLogDetailCard(log: CallLog, onDelete: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    val timeStr = sdf.format(Date(log.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Incoming Icon Status
                val iconColor = if (log.status == "Answered") EmeraldGreen else CrimsonRed
                val icon = Icons.Default.Call

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.visitorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${log.phoneNumber} • $timeStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete call log",
                        tint = CrimsonRed.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Reason Call: ${log.reason}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Auto Tags
            if (log.tags.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    log.tags.split(",").forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag.trim(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (isExpanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                // Detailed AI Insights Pane
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI Conversation Insights",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = CoolBlue
                        )

                        // Sentiment Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (log.sentiment.lowercase()) {
                                        "positive" -> EmeraldGreen.copy(alpha = 0.15f)
                                        "negative" -> CrimsonRed.copy(alpha = 0.15f)
                                        else -> SlateGray700.copy(alpha = 0.3f)
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Sentiment: ${log.sentiment}",
                                style = MaterialTheme.typography.labelSmall,
                                color = when (log.sentiment.lowercase()) {
                                    "positive" -> EmeraldGreen
                                    "negative" -> CrimsonRed
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }

                    Text(
                        text = "Duration: ${log.durationSeconds}s",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = log.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Follow up Recommendations:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = log.followUpText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap to show AI Summaries & Insights...",
                    style = MaterialTheme.typography.bodySmall,
                    color = CoolBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ==========================================
// 3. MEETINGS SCREEN
// ==========================================
@Composable
fun MeetingsScreen(viewModel: FrontDeskViewModel) {
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()

    // Form Dialog input States
    var meetTitle by remember { mutableStateOf("") }
    var meetGuest by remember { mutableStateOf("") }
    var meetTime by remember { mutableStateOf("") }
    var meetHost by remember { mutableStateOf("") }
    var meetRoom by remember { mutableStateOf("") }
    var meetSummary by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Scheduled Office Meetings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Visitor log and appointments coordination panel.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (meetings.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateCard(message = "No calendar meetings are active. Tap '+' below to add one.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(meetings, key = { it.id }) { meeting ->
                        MeetingCardItem(
                            meeting = meeting,
                            onStatusChange = { newStatus -> viewModel.updateMeetingStatus(meeting, newStatus) },
                            onDelete = { viewModel.deleteMeeting(meeting.id) }
                        )
                    }
                }
            }
        }

        // Add Meeting FAB
        FloatingActionButton(
            onClick = { viewModel.showAddMeetingDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_meeting_fab"),
            containerColor = CoolBlue,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Schedule Meeting")
        }

        if (viewModel.showAddMeetingDialog) {
            Dialog(onDismissRequest = { viewModel.showAddMeetingDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Schedule New Visitor",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = meetTitle,
                            onValueChange = { meetTitle = it },
                            label = { Text("Meeting Title (e.g. Office Leasing)") },
                            modifier = Modifier.fillMaxWidth().testTag("meet_title_input")
                        )

                        OutlinedTextField(
                            value = meetGuest,
                            onValueChange = { meetGuest = it },
                            label = { Text("Guest Full Name") },
                            modifier = Modifier.fillMaxWidth().testTag("meet_guest_input")
                        )

                        OutlinedTextField(
                            value = meetTime,
                            onValueChange = { meetTime = it },
                            label = { Text("Time (e.g. 10:30 AM)") },
                            modifier = Modifier.fillMaxWidth().testTag("meet_time_input")
                        )

                        OutlinedTextField(
                            value = meetHost,
                            onValueChange = { meetHost = it },
                            label = { Text("Host Employee Name") },
                            modifier = Modifier.fillMaxWidth().testTag("meet_host_input")
                        )

                        OutlinedTextField(
                            value = meetRoom,
                            onValueChange = { meetRoom = it },
                            label = { Text("Meeting Room / Location") },
                            modifier = Modifier.fillMaxWidth().testTag("meet_room_input")
                        )

                        OutlinedTextField(
                            value = meetSummary,
                            onValueChange = { meetSummary = it },
                            label = { Text("Agenda Summary Detail") },
                            modifier = Modifier.fillMaxWidth().testTag("meet_desc_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { viewModel.showAddMeetingDialog = false }) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (meetTitle.isNotBlank() && meetGuest.isNotBlank() && meetTime.isNotBlank()) {
                                        viewModel.addNewMeeting(
                                            title = meetTitle,
                                            guest = meetGuest,
                                            time = meetTime,
                                            host = if(meetHost.isBlank()) viewModel.receptionistName else meetHost,
                                            room = if(meetRoom.isBlank()) "Reception Lobby" else meetRoom,
                                            summary = meetSummary
                                        )
                                        // Reset
                                        meetTitle = ""
                                        meetGuest = ""
                                        meetTime = ""
                                        meetHost = ""
                                        meetRoom = ""
                                        meetSummary = ""
                                        viewModel.showAddMeetingDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CoolBlue),
                                modifier = Modifier.testTag("submit_add_meeting_dialog_btn")
                            ) {
                                Text("Schedule")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingCardItem(
    meeting: Meeting,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Time
                Text(
                    text = meeting.scheduledTime,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CoolBlue
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(status = meeting.status)
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete appointment", tint = CrimsonRed, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(meeting.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Guest / Organization",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = meeting.guestName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Location Suite",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = meeting.room,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (meeting.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = meeting.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Update Status Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onStatusChange("Checked-In") },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    modifier = Modifier.weight(1.5f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Check In", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { onStatusChange("Pending") },
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Reset", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { onStatusChange("No-Show") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRed),
                    border = BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1.2f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("No-Show", fontSize = 12.sp)
                }
            }
        }
    }
}

// ==========================================
// 4. BOOKINGS & FAQ SCREEN
// ==========================================
@Composable
fun BookingsScreen(viewModel: FrontDeskViewModel) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val faqs by viewModel.faqs.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf(0) } // 0 = Conversational booking registry, 1 = Directory Lookup / FAQ info

    Column(modifier = Modifier.fillMaxSize()) {
        // Upper switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activeSubTab == 0) CoolBlue else Color.Transparent)
                    .clickable { activeSubTab = 0 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Conversational AI Bookings",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (activeSubTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (activeSubTab == 1) CoolBlue else Color.Transparent)
                    .clickable { activeSubTab = 1 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Information & Directory FAQs",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (activeSubTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (activeSubTab == 0) {
            ConversationalBookingView(viewModel, bookings)
        } else {
            DirectoryInformationView(viewModel, faqs)
        }
    }
}

@Composable
fun ConversationalBookingView(viewModel: FrontDeskViewModel, bookings: List<Booking>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Bookings AI Receptionist Chatbot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Message the smart front-desk to pre-register. The AI parses visitor details and registers them automatically.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Chats List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.bookingChatMessages) { msg ->
                        ChatBubbleItem(msg = msg)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.activeChatInput,
                        onValueChange = { viewModel.activeChatInput = it },
                        placeholder = { Text("E.g: My name is Daniel from MI6, I want to book tomorrow 10am to see Brody for consulting.") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("booking_chat_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoolBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (viewModel.activeChatInput.isNotBlank() && !viewModel.isChatSending) {
                                viewModel.sendBookingMessage(viewModel.activeChatInput)
                            }
                        })
                    )
                    IconButton(
                        onClick = {
                            viewModel.sendBookingMessage(viewModel.activeChatInput)
                        },
                        modifier = Modifier
                            .testTag("submit_booking_chat")
                            .size(52.dp)
                            .background(CoolBlue, CircleShape),
                        enabled = !viewModel.isChatSending && viewModel.activeChatInput.isNotBlank()
                    ) {
                        if (viewModel.isChatSending) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        } else {
                            Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Pre-Registered Visitor Logs",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (bookings.isEmpty()) {
            EmptyStateCard(message = "No pre-registered bookings logged yet.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookings, key = { it.id }) { booking ->
                    BookingRegistryItem(
                        booking = booking,
                        onDelete = { viewModel.deleteBooking(booking.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(msg: ChatMessage) {
    if (msg.isSystem) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(EmeraldGreen.copy(alpha = 0.2f))
                    .border(1.dp, EmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = msg.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        return
    }

    val isUser = msg.sender == "Visitor"
    val bubbleColor = if (isUser) CoolBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalAlignment = alignment
    ) {
        Text(
            text = msg.sender,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
        )
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isUser) 12.dp else 0.dp,
                        bottomEnd = if (isUser) 0.dp else 12.dp
                    )
                )
                .background(bubbleColor)
                .padding(10.dp)
        ) {
            Text(text = msg.content, style = MaterialTheme.typography.bodyMedium, color = textColor)
        }
    }
}

@Composable
fun BookingRegistryItem(booking: Booking, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = booking.visitorName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(booking.status, color = EmeraldGreen, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Text(
                    text = "Company: ${booking.companyName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Time: ${booking.dateTime} • Meeting: ${booking.hostName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Reason: ${booking.purpose}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete pre-registration", tint = CrimsonRed)
            }
        }
    }
}

@Composable
fun DirectoryInformationView(viewModel: FrontDeskViewModel, faqs: List<Faq>) {
    var faqSimQuery by remember { mutableStateOf("") }
    var activeCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Directory", "Office Hours", "Parking", "Wi-Fi", "Emergency")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Upper AI look up bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Direct Building AI Information Center",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ask any building query. The AI answers visitors directly using the validated office directory.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = faqSimQuery,
                        onValueChange = { faqSimQuery = it },
                        placeholder = { Text("E.g. Is there client parking validated?") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("faq_consult_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoolBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            if (faqSimQuery.isNotBlank() && !viewModel.isFaqSearching) {
                                viewModel.askFaqQuery(faqSimQuery)
                            }
                        })
                    )
                    Button(
                        onClick = {
                            viewModel.askFaqQuery(faqSimQuery)
                        },
                        modifier = Modifier
                            .testTag("submit_faq_consult_btn")
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CoolBlue),
                        enabled = !viewModel.isFaqSearching && faqSimQuery.isNotBlank()
                    ) {
                        if (viewModel.isFaqSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                }

                if (viewModel.faqAnswerResult.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CoolBlue.copy(alpha = 0.15f))
                            .border(1.dp, CoolBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Front-Desk AI Response:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CoolBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = viewModel.faqAnswerResult,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Category filter pills row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.take(3).forEach { cat ->
                val selected = cat == activeCategoryFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) CoolBlue else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { activeCategoryFilter = cat }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        cat,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.drop(3).forEach { cat ->
                val selected = cat == activeCategoryFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) CoolBlue else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { activeCategoryFilter = cat }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        cat,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Display Directory Items
        val filtered = if (activeCategoryFilter == "All") faqs else faqs.filter { it.category == activeCategoryFilter }
        if (filtered.isEmpty()) {
            EmptyStateCard(message = "No matching directory items logged.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { item ->
                    FaqDirectoryItem(item = item)
                }
            }
        }
    }
}

@Composable
fun FaqDirectoryItem(item: Faq) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AmberOrange.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = AmberOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.question, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==========================================
// 5. DATA ANALYTICS SCREEN
// ==========================================
@Composable
fun AnalyticsScreen(viewModel: FrontDeskViewModel) {
    val callLogs by viewModel.callLogs.collectAsStateWithLifecycle()
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Business Intelligence & KPIs",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "AI receptionist performance statistics and prospective business operations leads.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Numerical KPIs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text("Total Inbound Calls", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${callLogs.size + 1280}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = CoolBlue)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text("Bookings Formed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${bookings.size + 610}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                }
            }
        }

        // Lead sources custom aesthetic bar chart (CSS/Compose Bar Style)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lead Source Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    LeadSourceBar(label = "Direct Call Inbound", percentage = 45, color = CoolBlue)
                    LeadSourceBar(label = "Virtual AI Booking Chat", percentage = 30, color = EmeraldGreen)
                    LeadSourceBar(label = "Walk-ins Welcome", percentage = 15, color = AmberOrange)
                    LeadSourceBar(label = "Strategic Referrals", percentage = 10, color = SlateGray700)
                }
            }
        }

        // Intent breakdown card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Visitor Intent Projections", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    IntentProgressRow(label = "Sales & Space Inquiries", count = 582, total = 1000, color = EmeraldGreen)
                    IntentProgressRow(label = "Courier Deliveries", count = 302, total = 1000, color = CoolBlue)
                    IntentProgressRow(label = "Investor Consultations", count = 91, total = 1000, color = AmberOrange)
                    IntentProgressRow(label = "General Customer Support", count = 25, total = 1000, color = CrimsonRed)
                }
            }
        }

        // Predictive Insights Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 Predictive Business Insights",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberOrange.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("A.I. Enabled", color = AmberOrange, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Most Active Day: Tuesday\n• Peak Traffic Hours: 10:00 AM – 1:00 PM\n• High Conversion Focus Areas:\n  1. Mid-Size Commercial Real Estate\n  2. Multi-Practitioner Medical Offices\n  3. Shared Workspace Franchises",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Active AI Operative Recommendation refresh trigger
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dynamic Live Operations Audit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { viewModel.refreshAIRecommendations() },
                            enabled = !viewModel.isLoadingRecommendations
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = CoolBlue
                            )
                        }
                    }
                    Text(
                        text = "Generates actionable operations auditing advice using direct workspace logging patterns.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (viewModel.isLoadingRecommendations) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CoolBlue)
                        }
                    } else {
                        Text(
                            text = viewModel.aiRecommendations,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeadSourceBar(label: String, percentage: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text("$percentage%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Visual custom slider bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun IntentProgressRow(label: String, count: Int, total: Int, color: Color) {
    val progress = count.toFloat() / total.toFloat()
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$count leads", style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    }
}

// ==========================================
// 6. SETTINGS SCREEN
// ==========================================
@Composable
fun SettingsScreen(viewModel: FrontDeskViewModel) {
    var recNameInput by remember { mutableStateOf(viewModel.receptionistName) }
    var catNameInput by remember { mutableStateOf(viewModel.businessCategory) }

    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Dashboard Configuration",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tune the virtual receptionist settings, receptionist name parameters, and simulation variables.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Host Operator Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Receptionist Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = recNameInput,
                        onValueChange = {
                            recNameInput = it
                            viewModel.receptionistName = it
                        },
                        label = { Text("Lead Receptionist Name") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("settings_operator_input")
                    )

                    OutlinedTextField(
                        value = catNameInput,
                        onValueChange = {
                            catNameInput = it
                            viewModel.businessCategory = it
                        },
                        label = { Text("Business Office Category") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("settings_office_input"),
                        placeholder = { Text("E.g. Medical Clinic, Coworking Franchise") }
                    )

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.refreshAIRecommendations()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_settings_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CoolBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save & Update AI Context")
                    }
                }
            }
        }

        // Sandbox System Diagnostics & Database Flush
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Sandbox Maintenance & CRM Diagnostics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonRed
                    )
                    Text(
                        text = "Clears and resets the local SQLite database to retrieve the polished standard business demo logs immediately.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { viewModel.resetDemoData() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reset_demo_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reset & Reload Standard Demo Database")
                    }
                }
            }
        }

        // App details
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Front-Desk Receptionist AI v1.0.0",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Enterprise-Grade SaaS Front-Desk CRM System",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ==========================================
// SHARED UTILITY COMPOSABLES
// ==========================================
@Composable
fun StatusChip(status: String) {
    val (backColor, textColor) = when (status.lowercase()) {
        "confirmed", "approved", "checked-in" -> Pair(EmeraldGreen.copy(alpha = 0.15f), EmeraldGreen)
        "pending" -> Pair(AmberOrange.copy(alpha = 0.15f), AmberOrange)
        "no-show", "denied" -> Pair(CrimsonRed.copy(alpha = 0.15f), CrimsonRed)
        else -> Pair(SlateGray800, SlateGray400)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// TODAY'S APPOINTMENTS DISPLAY COMPONENT
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayAppointmentsDisplay(
    viewModel: FrontDeskViewModel,
    modifier: Modifier = Modifier
) {
    val meetings by viewModel.meetings.collectAsStateWithLifecycle()
    var selectedStatusFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Confirmed", "Checked-In", "Pending", "No-Show")

    val filteredMeetings = remember(meetings, selectedStatusFilter) {
        if (selectedStatusFilter == "All") {
            meetings
        } else {
            meetings.filter { it.status.equals(selectedStatusFilter, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("today_appointments_display_component"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Topic Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Today's Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${meetings.size} active appointments for the current day",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Add a mini add button
            IconButton(
                onClick = { viewModel.showAddMeetingDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(CoolBlue.copy(alpha = 0.15f), CircleShape)
                    .testTag("app_add_meeting_shortcut"),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Add Appointment",
                    tint = CoolBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Horizontal status filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            filters.forEach { filter ->
                val isSelected = selectedStatusFilter == filter
                val count = if (filter == "All") {
                    meetings.size
                } else {
                    meetings.count { it.status.equals(filter, ignoreCase = true) }
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { selectedStatusFilter = filter },
                    label = { 
                        Text(
                            text = if (count > 0 && filter != "All") "$filter ($count)" else filter,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ) 
                    },
                    modifier = Modifier.testTag("appointment_filter_$filter"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CoolBlue.copy(alpha = 0.15f),
                        selectedLabelColor = CoolBlue,
                        selectedLeadingIconColor = CoolBlue
                    )
                )
            }
        }

        // List container
        if (filteredMeetings.isEmpty()) {
            EmptyStateCard(message = "No $selectedStatusFilter appointments found for today.")
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredMeetings.forEach { meeting ->
                    TodayAppointmentListItem(
                        meeting = meeting,
                        onStatusChange = { newStatus ->
                            viewModel.updateMeetingStatus(meeting, newStatus)
                        },
                        onDelete = {
                            viewModel.deleteMeeting(meeting.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TodayAppointmentListItem(
    meeting: Meeting,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine leading indicator color based on status
    val statusColor = when (meeting.status.lowercase()) {
        "confirmed" -> CoolBlue
        "checked-in" -> EmeraldGreen
        "pending" -> AmberOrange
        "no-show" -> CrimsonRed
        else -> SlateGray400
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("appointment_item_${meeting.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
            // Asymmetric Left vertical Accent bar with active Status Color
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(statusColor)
            )

            // Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                // Header Row (Time, Status Badge, Mute actions block)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Scheduled Time Icon",
                            tint = CoolBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = meeting.scheduledTime,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CoolBlue
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StatusChip(status = meeting.status)
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("delete_appointment_shortcut_${meeting.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete appointment record",
                                tint = CrimsonRed.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                // Title
                Text(
                    text = meeting.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))
                // Guest & Host Grid details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Guest / Account",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = meeting.guestName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Location Suite / Room",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = meeting.room,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Inner Action Buttons for workflow streamlining
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Workflow:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    // "Check-In" workflow action button is key for Front Desks
                    if (meeting.status.lowercase() != "checked-in") {
                        Button(
                            onClick = { onStatusChange("Checked-In") },
                            modifier = Modifier
                                .testTag("check_in_btn_${meeting.id}")
                                .height(32.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Check In", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Toggles to set status back or pending
                    if (meeting.status.lowercase() == "checked-in" || meeting.status.lowercase() == "no-show") {
                        OutlinedButton(
                            onClick = { onStatusChange("Confirmed") },
                            modifier = Modifier
                                .testTag("reconfirm_btn_${meeting.id}")
                                .height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CoolBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CoolBlue)
                        ) {
                            Text("Reset Confirmed", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Mark No-Show button quick toggle
                        OutlinedButton(
                            onClick = { onStatusChange("No-Show") },
                            modifier = Modifier
                                .testTag("no_show_btn_${meeting.id}")
                                .height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRed)
                        ) {
                            Text("No Show", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
