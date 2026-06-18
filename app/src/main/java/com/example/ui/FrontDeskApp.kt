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
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.Montserrat
import com.example.ui.theme.CormorantGaramond
import com.example.ui.theme.BebasNeue
import com.example.ui.theme.EmpireGold
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.SandPlated
import com.example.ui.theme.ObsidianMutedText
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

    val appContent = @Composable {
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
                        icon = { Icon(Icons.Default.Star, contentDescription = "T&F HyperDeck") },
                        label = { Text("T&F HyperDeck", maxLines = 1, overflow = TextOverflow.Ellipsis) }
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

    if (viewModel.isLuxuryThemeActive) {
        TFAugmentedTheme {
            appContent()
        }
    } else {
        appContent()
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

        // Luxurious $12,480 Revenue Recovered Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_revenue_banner"),
                colors = CardDefaults.cardColors(
                    containerColor = if (viewModel.isLuxuryThemeActive) ObsidianCard else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.5.dp,
                    if (viewModel.isLuxuryThemeActive) EmpireGold.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "T&F AUTOMATE CO-PILOT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = if (viewModel.isLuxuryThemeActive) Montserrat else FontFamily.Default,
                            color = if (viewModel.isLuxuryThemeActive) EmpireGold else MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (viewModel.isLuxuryThemeActive) EmpireGold.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "LIVE OPERATIONS AUDIT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = if (viewModel.isLuxuryThemeActive) Montserrat else FontFamily.Default,
                                color = if (viewModel.isLuxuryThemeActive) EmpireGold else MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Total Revenue Recovered",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        fontFamily = if (viewModel.isLuxuryThemeActive) CormorantGaramond else FontFamily.Default,
                        color = if (viewModel.isLuxuryThemeActive) SandPlated else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "$12,480",
                            fontSize = 46.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = if (viewModel.isLuxuryThemeActive) BebasNeue else FontFamily.Default,
                            color = if (viewModel.isLuxuryThemeActive) EmpireGold else MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp,
                            lineHeight = 44.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "USD RECOVERED THIS WEEK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = if (viewModel.isLuxuryThemeActive) Montserrat else FontFamily.Default,
                            color = if (viewModel.isLuxuryThemeActive) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Autonomous AI receptionist intercepted dropped inbound sales leads and locked in bookings instantly.",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = if (viewModel.isLuxuryThemeActive) Montserrat else FontFamily.Default,
                        color = if (viewModel.isLuxuryThemeActive) SandPlated.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
    var activeDrawerLog by remember { mutableStateOf<CallLog?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        CallLogDetailCard(
                            log = log,
                            onDelete = { viewModel.deleteCallLog(log.id) },
                            onShowDrawer = { activeDrawerLog = log },
                            isLuxuryThemeActive = viewModel.isLuxuryThemeActive
                        )
                    }
                }
            }
        }

        // Overlaid AI Call Summary Drawer overlay
        if (activeDrawerLog != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(20f)
            ) {
                AICallSummaryDrawer(
                    log = activeDrawerLog!!,
                    isLuxuryMode = viewModel.isLuxuryThemeActive,
                    onDismiss = { activeDrawerLog = null }
                )
            }
        }
    }
}

@Composable
fun CallLogDetailCard(
    log: CallLog,
    onDelete: () -> Unit,
    onShowDrawer: () -> Unit,
    isLuxuryThemeActive: Boolean
) {
    var isExpanded by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    val timeStr = sdf.format(Date(log.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { isExpanded = !isExpanded }
            .testTag("call_log_card_${log.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isLuxuryThemeActive) ObsidianCard else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (isLuxuryThemeActive) EmpireGold.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
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
                        .background(
                            if (isLuxuryThemeActive) EmpireGold.copy(alpha = 0.12f) else iconColor.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isLuxuryThemeActive) EmpireGold else iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.visitorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = if (isLuxuryThemeActive) CormorantGaramond else FontFamily.Default,
                        color = if (isLuxuryThemeActive) EmpireGold else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${log.phoneNumber} • $timeStr",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default,
                        color = if (isLuxuryThemeActive) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant
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
                fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default,
                color = if (isLuxuryThemeActive) SandPlated else MaterialTheme.colorScheme.onSurface
            )

            // Auto Tags
            if (log.tags.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    log.tags.split(",").forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isLuxuryThemeActive) EmpireGold.copy(alpha = 0.08f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                                .border(
                                    1.dp,
                                    if (isLuxuryThemeActive) EmpireGold.copy(alpha = 0.2f) else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag.trim(),
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default,
                                color = if (isLuxuryThemeActive) EmpireGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (isExpanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = if (isLuxuryThemeActive) EmpireGold.copy(alpha = 0.25f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
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
                            fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default,
                            color = if (isLuxuryThemeActive) EmpireGold else CoolBlue
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
                                fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default,
                                color = when (log.sentiment.lowercase()) {
                                    "positive" -> EmeraldGreen
                                    "negative" -> CrimsonRed
                                    else -> if (isLuxuryThemeActive) SandPlated else MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }

                    Text(
                        text = "Duration: ${log.durationSeconds}s",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default,
                        color = if (isLuxuryThemeActive) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = log.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default,
                        color = if (isLuxuryThemeActive) SandPlated else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Follow up Recommendations:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default,
                        color = if (isLuxuryThemeActive) EmpireGold else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = log.followUpText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default,
                        color = if (isLuxuryThemeActive) SandPlated.copy(alpha = 0.82f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // BEAUTIFUL ULTRA DRAWER TOGGLE BUTTON
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onShowDrawer,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLuxuryThemeActive) EmpireGold else CoolBlue,
                            contentColor = if (isLuxuryThemeActive) ObsidianBlack else Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_log_drawer_btn_${log.id}"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Search details",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "✨ VIEW AI AUDIO ANALYSIS & TRANSCRIPT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap to show AI Summaries & Insights...",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isLuxuryThemeActive) EmpireGold else CoolBlue,
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

    var selectedTab by remember { mutableStateOf(0) } // 0: KPIs & Auditing, 1: AI Evolution Blueprint

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shared Screen Header
        Column {
            Text(
                text = "Business Intelligence & Strategy",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Performance statistics and strategic roadmap for scaling front-desk operations.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Custom M3 Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = CoolBlue,
            divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)) },
            modifier = Modifier.fillMaxWidth().testTag("analytics_tab_row")
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("KPI Metrics & Audit", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("analytics_tab_metrics")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("AI Evolution Blueprint", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("analytics_tab_evolution")
            )
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
        } else {
            // TF LUXURY PRESENTATION SEQUENCE PLAYER DECK
            TFHyperDeckScreen(viewModel)
        }
    }
}

@Composable
fun EvolutionDeckView(
    callLogsCount: Int,
    meetingsCount: Int,
    bookingsCount: Int,
    modifier: Modifier = Modifier
) {
    var currentSlideIndex by remember { mutableStateOf(0) }
    val totalSlides = 5

    val slides = listOf(
        EvolutionSlide(
            phase = "PHASE 1",
            title = "The Traditional Front Desk",
            subtitle = "The Operational Bottleneck",
            tagline = "Single-threaded physical presence, high abandonment, and administrative strain.",
            concept = "Relying 100% on a human receptionist at a desk means single-threaded operations. Missed calls, endless queues, and off-hours voice-mail drops act as heavy business bottlenecks.",
            bullets = listOf(
                "High Abandonment Rate: 1 in 3 inbound calls missed during peak lobby hours.",
                "Off-Hours Invisibility: 0% support after standard business hours (8 PM - 6 AM).",
                "Administrative Tax: Repeating Wi-Fi codes, parking validation, and floor directions takes 70% of physical capacity."
            ),
            impactColor = CrimsonRed,
            icon = Icons.Default.Warning,
            metricLabel = "Projected Missed Calls",
            metricValue = "${(callLogsCount * 0.45).toInt() + 14} missed/day"
        ),
        EvolutionSlide(
            phase = "PHASE 2",
            title = "The Co-Pilot Assist Screen",
            subtitle = "The Assisted Operator",
            tagline = "Digital forms and basic routing support, speed up reception tasks.",
            concept = "Digital check-in sheets and basic chatbot forms are added, but humans are still manually responsible for triaging information, notifying staff, and booking calendar reservations.",
            bullets = listOf(
                "Triage Efficiency: Shared digital spreadsheets simplify tracking.",
                "Notification Drag: Receptionists still manually search and ping hosts via Slack or phone.",
                "High Friction: Callers must still wait for a physical desk callback to confirm scheduling details."
            ),
            impactColor = AmberOrange,
            icon = Icons.Default.Build,
            metricLabel = "Avg Check-In Speed",
            metricValue = "2.8 minutes"
        ),
        EvolutionSlide(
            phase = "PHASE 3",
            title = "The Generative AI Intercept",
            subtitle = "The Multi-Channel Co-Worker",
            tagline = "Voice & Chat AI answering calls, auto-mapping JSON logs, and answering FAQs.",
            concept = "Introduction of GenAI voice/chat agents that act as first intercept layers. Phone calls are answered instantly, details are extracted into structured logs, and FAQs are resolved instantly.",
            bullets = listOf(
                "0% Missed Calls: Infinite telephone trunk-lines mean every single call gets answered in 1.2 seconds.",
                "Structured Conversational Parsing: Extracts caller details, sentiment analysis, and urgent tags instantly.",
                "Autonomous FAQ Clearance: Instant 24/7 resolution of guest Wi-Fi, location, pass, and parking queries."
            ),
            impactColor = CoolBlue,
            icon = Icons.Default.Call,
            metricLabel = "Resolved Inquiries",
            metricValue = "${callLogsCount + 82} answered/day"
        ),
        EvolutionSlide(
            phase = "PHASE 4",
            title = "Autonomous Live Workflows",
            subtitle = "The Integrated Ecosystem",
            tagline = "Automated SMS alerts, self-updating DB registries, and predictive audits.",
            concept = "AI does more than talk—it automates administrative actions. Calls trigger auto-booking. Visitor check-in triggers host SMS notification. Real-time logging enables predictive scheduling analytics.",
            bullets = listOf(
                "Connected Automation: High-velocity data pipelines linking phone logs, room bookings, and calendar feeds.",
                "Zero-Entry Overhead: Database registers visitor and schedules meeting room in Room DB automatically.",
                "Host Alerts & check-ins: Instant host alert check-ins with clear status updates, minimizing lobby crowd control."
            ),
            impactColor = EmeraldGreen,
            icon = Icons.Default.CheckCircle,
            metricLabel = "Auto-Processed Records",
            metricValue = "${meetingsCount + bookingsCount} events/day"
        ),
        EvolutionSlide(
            phase = "PHASE 5",
            title = "Zero-Touch Hospitality",
            subtitle = "Infinite Scale Hospitality",
            tagline = "Focusing 100% of human energy on premium guest experiences.",
            concept = "The ultimate operational endpoint. Routine administrative workflows run entirely autonomously at infinite scale. Physical hosts are empowered to focus entirely on human-to-human hospitality.",
            bullets = listOf(
                "High Value Hospitality: Receptionists become Experience Directors, greeting executive guests and handling complex VIP escalations.",
                "Infinite Operational Bandwidth: Simultaneously handles 100+ callers or registrants during high-growth seasons.",
                "Predictive Intelligence: AI system actively audits front-desk operations and recommends peak capacity scheduling parameters."
            ),
            impactColor = EmeraldGreen,
            icon = Icons.Default.Star,
            metricLabel = "A.I. Operator Efficiency",
            metricValue = "98.4%"
        )
    )

    val currentSlide = slides[currentSlideIndex]

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .testTag("evolution_deck_container")
    ) {
        // Slide Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Evolution Roadmap Deck",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Slide ${currentSlideIndex + 1} of $totalSlides",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress Pill indicator row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until totalSlides) {
                val isCurrent = i == currentSlideIndex
                val widthWeight = if (isCurrent) 2f else 1f
                val color = if (isCurrent) CoolBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                Box(
                    modifier = Modifier
                        .weight(widthWeight)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // The Sliding Card Body
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, currentSlide.impactColor.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header (Badge Indicator + Main Icon)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(currentSlide.impactColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentSlide.phase,
                            color = currentSlide.impactColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(currentSlide.impactColor.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentSlide.icon,
                            contentDescription = null,
                            tint = currentSlide.impactColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title & Subtitle block
                Text(
                    text = currentSlide.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currentSlide.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = currentSlide.impactColor.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                // Dramatic Tagline Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "“${currentSlide.tagline}”",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Concept explanation Text
                Text(
                    text = currentSlide.concept,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Core Bullets
                Text(
                    text = "Key Metrics & Characteristics:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                currentSlide.bullets.forEach { bullet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "⚡",
                            color = currentSlide.impactColor,
                            modifier = Modifier.padding(end = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = bullet,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Dynamic System Mapping Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(currentSlide.impactColor.copy(alpha = 0.08f))
                        .border(1.dp, currentSlide.impactColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Live Workspace Metric",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currentSlide.metricLabel,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(currentSlide.impactColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentSlide.metricValue,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Slide navigation controller bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { if (currentSlideIndex > 0) currentSlideIndex-- },
                enabled = currentSlideIndex > 0,
                modifier = Modifier
                    .weight(1f)
                    .testTag("evolution_prev_slide_btn"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (currentSlideIndex > 0) CoolBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                )
                Text("Previous Slide")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { if (currentSlideIndex < totalSlides - 1) currentSlideIndex++ },
                enabled = currentSlideIndex < totalSlides - 1,
                modifier = Modifier
                    .weight(1f)
                    .testTag("evolution_next_slide_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoolBlue)
            ) {
                Text("Next Slide")
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).padding(start = 4.dp)
                )
            }
        }
    }
}

data class EvolutionSlide(
    val phase: String,
    val title: String,
    val subtitle: String,
    val tagline: String,
    val concept: String,
    val bullets: List<String>,
    val impactColor: Color,
    val icon: ImageVector,
    val metricLabel: String,
    val metricValue: String
)

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

// ==========================================
// 6. T&F LUXURY PRESENTATION HANDLER & DRAWER
// ==========================================
@Composable
fun AICallSummaryDrawer(
    log: CallLog,
    isLuxuryMode: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .testTag("luxury_ai_drawer"),
            colors = CardDefaults.cardColors(
                containerColor = if (isLuxuryMode) ObsidianBlack else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            border = BorderStroke(
                1.5.dp,
                if (isLuxuryMode) EmpireGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI INTELLIGENT DEEPLINK SCREEN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = if (isLuxuryMode) Montserrat else FontFamily.Default,
                            color = if (isLuxuryMode) EmpireGold else CoolBlue,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = log.visitorName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = if (isLuxuryMode) CormorantGaramond else FontFamily.Default,
                            color = if (isLuxuryMode) EmpireGold else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isLuxuryMode) EmpireGold.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close drawer",
                            tint = if (isLuxuryMode) EmpireGold else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = if (isLuxuryMode) EmpireGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outlineVariant
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // KPI Parameters Row 1
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "COGNITIVE SEMANTIC CLASSIFICATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLuxuryMode) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                previewSubCard(
                                    modifier = Modifier.weight(1f),
                                    label = "Intent Class",
                                    value = "INBOUND SPACE LEASE",
                                    isLuxury = isLuxuryMode
                                )
                                previewSubCard(
                                    modifier = Modifier.weight(1f),
                                    label = "Urgency Status",
                                    value = "CRITICAL ⚡",
                                    isLuxury = isLuxuryMode
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                previewSubCard(
                                    modifier = Modifier.weight(1f),
                                    label = "Detected Sentiment",
                                    value = "POSITIVE (98.2% confidence)",
                                    isLuxury = isLuxuryMode
                                )
                                previewSubCard(
                                    modifier = Modifier.weight(1f),
                                    label = "Verification Seal",
                                    value = "T&F AUTOMATE VERIFIED ✔",
                                    isLuxury = isLuxuryMode
                                )
                            }
                        }
                    }

                    // Conversation Summary
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLuxuryMode) ObsidianCard else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isLuxuryMode) EmpireGold.copy(alpha = 0.2f) else Color.Transparent
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "EXECUTIVE BRIEF",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLuxuryMode) EmpireGold else MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Sarah Lee states interest in leasing the dual-suite workspace workspace area. Conversation lasted 145 seconds with perfect intent parsing and zero fallback events. Automated response completed booking lock and sent SMS validation link successfully.",
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = if (isLuxuryMode) Montserrat else FontFamily.Default,
                                    color = if (isLuxuryMode) SandPlated else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Dialog audio transcript!
                    item {
                        Text(
                            text = "AUDIO REAL-TIME SYSTEM TRANSCRIPT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLuxuryMode) EmpireGold else MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TranscriptBubble(
                                speaker = "Sarah Lee (Visitor)",
                                time = "11:05:01 AM",
                                text = "Hi, I'm calling from Summit Properties. We are looking to book a dual commercial workspace desk suite for the investor conference at conference Room A next Wednesday. Is that something we can secure with you today?",
                                isVisitor = true,
                                isLuxury = isLuxuryMode
                            )

                            TranscriptBubble(
                                speaker = "T&F Front-Desk AI",
                                time = "11:05:14 AM",
                                text = "Absolutey, Ms. Lee! I see conference Room A has open reservation availability next Wednesday. I can lock in that investor suite booking for your team under the executive summit pricing structure ($12,480 total booking value). Shall I secure this list slot now?",
                                isVisitor = false,
                                isLuxury = isLuxuryMode
                            )

                            TranscriptBubble(
                                speaker = "Sarah Lee (Visitor)",
                                time = "11:05:28 AM",
                                text = "Oh perfect, that matches our conference outline. Go ahead and confirm that. I will authorize Summit Properties to process the credit invoice directly. Thank you!",
                                isVisitor = true,
                                isLuxury = isLuxuryMode
                            )

                            TranscriptBubble(
                                speaker = "T&F Front-Desk AI",
                                time = "11:05:40 AM",
                                text = "Secure slot complete! Summit Properties dual executive suite booking is locked and verified. I have transmitted the instant SMS payment and calendar reminder confirmation directly to your phone. We look forward to welcome you next Wednesday!",
                                isVisitor = false,
                                isLuxury = isLuxuryMode
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun previewSubCard(modifier: Modifier, label: String, value: String, isLuxury: Boolean) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            1.dp,
            if (isLuxury) EmpireGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TranscriptBubble(speaker: String, time: String, text: String, isVisitor: Boolean, isLuxury: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isVisitor) Alignment.Start else Alignment.End
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = speaker,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default
            )
            Text(
                text = time,
                fontSize = 9.sp,
                color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isVisitor) 2.dp else 12.dp,
                        bottomEnd = if (isVisitor) 12.dp else 2.dp
                    )
                )
                .background(
                    if (isLuxury) {
                        if (isVisitor) ObsidianCard else EmpireGold.copy(alpha = 0.08f)
                    } else {
                        if (isVisitor) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                    }
                )
                .border(
                    1.dp,
                    if (isLuxury) {
                        if (isVisitor) EmpireGold.copy(alpha = 0.15f) else EmpireGold.copy(alpha = 0.40f)
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isVisitor) 2.dp else 12.dp,
                        bottomEnd = if (isVisitor) 12.dp else 2.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ExecutiveKpiStrip(isLuxury: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("executive_kpi_card"),
        colors = CardDefaults.cardColors(
            containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.5.dp,
            if (isLuxury) EmpireGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "EXECUTIVE KPI AUDITING REPORT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiTile(
                    modifier = Modifier.weight(1f),
                    label = "Automation Rate",
                    value = "87%",
                    isLuxury = isLuxury,
                    accentColor = EmeraldGreen
                )
                KpiTile(
                    modifier = Modifier.weight(1f),
                    label = "Bookings Formed",
                    value = "613",
                    isLuxury = isLuxury,
                    accentColor = CoolBlue
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiTile(
                    modifier = Modifier.weight(1f),
                    label = "Inbound Calls Saved",
                    value = "1,285",
                    isLuxury = isLuxury,
                    accentColor = AmberOrange
                )
                KpiTile(
                    modifier = Modifier.weight(1f),
                    label = "Estimated Recovered Value",
                    value = "$84,200",
                    isLuxury = isLuxury,
                    accentColor = EmpireGold
                )
            }
        }
    }
}

@Composable
fun KpiTile(modifier: Modifier, label: String, value: String, isLuxury: Boolean, accentColor: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (isLuxury) EmpireGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = if (isLuxury) BebasNeue else FontFamily.Default,
                color = if (isLuxury) accentColor else MaterialTheme.colorScheme.onSurface,
                lineHeight = 26.sp
            )
        }
    }
}

@Composable
fun FinalBrandFrame(isLuxury: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("brand_frame_card"),
        colors = CardDefaults.cardColors(
            containerColor = if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            2.dp,
            if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "T&F AUTOMATE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = if (isLuxury) CormorantGaramond else FontFamily.Default,
                color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(1.dp)
                    .background(if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Powered by T&F Automate",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "The Autonomous Intelligent Front Desk of Tomorrow",
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TFHyperDeckScreen(viewModel: FrontDeskViewModel) {
    var hyperSlideIndex by remember { mutableStateOf(0) }
    val totalSlides = 4
    val isLuxury = viewModel.isLuxuryThemeActive

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (hyperSlideIndex) {
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "SEQUENCE FRAME 1: DASHBOARD HERO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.5.dp, if (isLuxury) EmpireGold else Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "Total Revenue Recovered",
                                    fontSize = 11.sp,
                                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                    color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "$12,480",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = if (isLuxury) BebasNeue else FontFamily.Default,
                                    color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Autonomous receptionist prevented missed leads during off-hours.",
                                    fontSize = 10.sp,
                                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                    color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = if (isLuxury) EmpireGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outlineVariant)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Calls Logged", fontSize = 9.sp, color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("1,285 Today", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.onSurface)
                                    }
                                    Column {
                                        Text("Check-In Rate", fontSize = 9.sp, color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("87% Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.onSurface)
                                    }
                                    Column {
                                        Text("Upcoming", fontSize = 9.sp, color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("613 Booked", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "SEQUENCE FRAME 2: AI CALL SUMMARY DRAWER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.5.dp, if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("EXECUTIVE ANALYST VIEW", fontSize = 8.sp, color = if (isLuxury) EmpireGold else CoolBlue)
                                        Text("Sarah Lee (Summit Properties)", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = if (isLuxury) CormorantGaramond else FontFamily.Default, color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.onSurface)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(EmeraldGreen.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Positive Sentiment", fontSize = 9.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Inbound Call Summary Drawer Details:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurface)
                                Text("• Intent Class: Booking Desk Lease\n• Urgency: High ⚡ (98% Parsing Confidence)\n• Follow up advice: Sent confirmation calendar link via SMS successfully.", fontSize = 11.sp, color = if (isLuxury) SandPlated.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)

                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isLuxury) EmpireGold else CoolBlue),
                                    modifier = Modifier.fillMaxWidth().height(32.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("✨ LAUNCH DRAWER FROM CALLS LOG PAGE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) ObsidianBlack else Color.White)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "SEQUENCE FRAME 3: EXECUTIVE KPI STRIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        ExecutiveKpiStrip(isLuxury = isLuxury)
                    }
                }
                3 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "SEQUENCE FRAME 4: FINAL BRAND FRAME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        FinalBrandFrame(isLuxury = isLuxury)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (hyperSlideIndex > 0) hyperSlideIndex-- },
                enabled = hyperSlideIndex > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLuxury) EmpireGold else CoolBlue,
                    contentColor = if (isLuxury) ObsidianBlack else Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Previous Frame", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (0 until totalSlides).forEach { idx ->
                    Box(
                        modifier = Modifier
                            .size(if (idx == hyperSlideIndex) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (idx == hyperSlideIndex) {
                                    if (isLuxury) EmpireGold else CoolBlue
                                } else {
                                    if (isLuxury) EmpireGold.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant
                                }
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = { if (hyperSlideIndex < totalSlides - 1) hyperSlideIndex++ },
                enabled = hyperSlideIndex < totalSlides - 1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLuxury) EmpireGold else CoolBlue,
                    contentColor = if (isLuxury) ObsidianBlack else Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Next Frame", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}
