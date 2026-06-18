package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.foundation.horizontalScroll
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
                            color = if (viewModel.isLuxuryThemeActive) SandPlated else MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (viewModel.isLuxuryThemeActive) ObsidianCardAccent else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "LIVE OPERATIONS AUDIT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = if (viewModel.isLuxuryThemeActive) Montserrat else FontFamily.Default,
                                color = if (viewModel.isLuxuryThemeActive) SandPlated.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary,
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
                            if (isLuxuryThemeActive) ObsidianCardAccent else iconColor.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isLuxuryThemeActive) SandPlated else iconColor,
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
                        color = if (isLuxuryThemeActive) Color.White else MaterialTheme.colorScheme.onSurface
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
                                    if (isLuxuryThemeActive) ObsidianCardAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                                .border(
                                    1.dp,
                                    if (isLuxuryThemeActive) ObsidianCardAccent else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag.trim(),
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = if (isLuxuryThemeActive) Montserrat else FontFamily.Default,
                                color = if (isLuxuryThemeActive) SandPlated else MaterialTheme.colorScheme.onSurfaceVariant
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
                            color = if (isLuxuryThemeActive) SandPlated else CoolBlue
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
                        color = if (isLuxuryThemeActive) SandPlated else MaterialTheme.colorScheme.onSurface
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
                    color = if (isLuxuryThemeActive) ObsidianMutedText else CoolBlue,
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
    val isLuxury = viewModel.isLuxuryThemeActive
    val focusManager = LocalFocusManager.current

    val cardBg = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surface
    val cardBorder = if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    val mainText = if (isLuxury) Color.White else MaterialTheme.colorScheme.onSurface
    val subText = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurfaceVariant
    val mutedText = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.outline
    val accentColor = if (isLuxury) EmpireGold else CoolBlue

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP BRANDING BANNER
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "DESK CONFIGURATION CENTER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                    color = accentColor,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Autonomous System Tuning",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = if (isLuxury) CormorantGaramond else FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    color = mainText
                )
                Text(
                    text = "A live administrative console to tune receptionist cognitive settings, trigger dispatch channels, adjust urgent alerts, and link team directories.",
                    fontSize = 12.sp,
                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                    color = subText,
                    lineHeight = 16.sp
                )
            }
        }

        // 1. RECEPTIONIST PROFILE SETTINGS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "RECEPTIONIST PROFILE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                        color = accentColor,
                        letterSpacing = 1.sp
                    )

                    // Lead Agent Name Box
                    OutlinedTextField(
                        value = viewModel.receptionistName,
                        onValueChange = { viewModel.receptionistName = it },
                        label = { Text("Lead Agent Name", fontFamily = if (isLuxury) Montserrat else FontFamily.Default) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("settings_operator_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = mainText,
                            unfocusedTextColor = mainText,
                            focusedLabelColor = accentColor,
                            unfocusedLabelColor = mutedText,
                            focusedIndicatorColor = accentColor,
                            unfocusedIndicatorColor = cardBorder
                        )
                    )

                    // Suggested Nobles name row
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Representative Nobles (Click to Apply)",
                            fontSize = 9.sp,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            color = mutedText,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val templates = listOf("Terrance Franklin", "Amelia Stone", "Arthur Vance", "Vivian Gold")
                            templates.forEach { name ->
                                Surface(
                                    modifier = Modifier.clickable { viewModel.receptionistName = name },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (viewModel.receptionistName == name) {
                                        accentColor.copy(alpha = 0.15f)
                                    } else {
                                        if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        if (viewModel.receptionistName == name) accentColor else cardBorder
                                    )
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                        color = if (viewModel.receptionistName == name) accentColor else subText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Office Category Box
                    OutlinedTextField(
                        value = viewModel.businessCategory,
                        onValueChange = { viewModel.businessCategory = it },
                        label = { Text("Business Office Category", fontFamily = if (isLuxury) Montserrat else FontFamily.Default) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("settings_office_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = mainText,
                            unfocusedTextColor = mainText,
                            focusedLabelColor = accentColor,
                            unfocusedLabelColor = mutedText,
                            focusedIndicatorColor = accentColor,
                            unfocusedIndicatorColor = cardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Receptionist greeting (fully editable)
                    OutlinedTextField(
                        value = viewModel.receptionistGreeting,
                        onValueChange = { viewModel.receptionistGreeting = it },
                        label = { Text("Active Office Intro Greeting", fontFamily = if (isLuxury) Montserrat else FontFamily.Default) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = mainText,
                            unfocusedTextColor = mainText,
                            focusedLabelColor = accentColor,
                            unfocusedLabelColor = mutedText,
                            focusedIndicatorColor = accentColor,
                            unfocusedIndicatorColor = cardBorder
                        )
                    )
                }
            }
        }

        // 2. DISPATCH & AUTOMATION TOGGLES
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "INTELLIGENCE & DISPATCH PIPELINES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                        color = accentColor,
                        letterSpacing = 1.sp
                    )

                    // SMS dispatches item
                    ToggleSettingItem(
                        title = "SMS Pin Dispatches to Hosts",
                        description = "Simulates instant guest notification, Wi-Fi keys, and digital parking codes sent directly to slated host phone lines.",
                        checked = viewModel.smsAlertsEnabled,
                        onCheckedChange = { viewModel.smsAlertsEnabled = it },
                        isLuxury = isLuxury,
                        accentColor = accentColor,
                        mainText = mainText,
                        subText = subText,
                        mutedText = mutedText
                    )

                    HorizontalDivider(color = cardBorder)

                    // Voice activated item
                    ToggleSettingItem(
                        title = "Voice-Activated Reception",
                        description = "Enables dynamic low-latency audio synthesizers to process phone calls and speak directly with callers over lines.",
                        checked = viewModel.voiceReceptionEnabled,
                        onCheckedChange = { viewModel.voiceReceptionEnabled = it },
                        isLuxury = isLuxury,
                        accentColor = accentColor,
                        mainText = mainText,
                        subText = subText,
                        mutedText = mutedText
                    )

                    HorizontalDivider(color = cardBorder)

                    // Live Auditing item
                    ToggleSettingItem(
                        title = "Real-Time System Log Auditing",
                        description = "Instantly updates sentiment indicators, visitor log markers, and administrative records live on the main dashboard.",
                        checked = viewModel.liveAuditingEnabled,
                        onCheckedChange = { viewModel.liveAuditingEnabled = it },
                        isLuxury = isLuxury,
                        accentColor = accentColor,
                        mainText = mainText,
                        subText = subText,
                        mutedText = mutedText
                    )

                    HorizontalDivider(color = cardBorder)

                    // Bypass verification item
                    ToggleSettingItem(
                        title = "Bypass Host Verification",
                        description = "Instantly auto-approves office pre-bookings without demanding host manual approval tokens.",
                        checked = viewModel.bypassVerification,
                        onCheckedChange = { viewModel.bypassVerification = it },
                        isLuxury = isLuxury,
                        accentColor = accentColor,
                        mainText = mainText,
                        subText = subText,
                        mutedText = mutedText
                    )
                }
            }
        }

        // 3. URGENT DISPATCH KEYWATCH REGISTRY
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "URGENT DISPATCH SIGNAL WORDS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                        color = accentColor,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Matching keywords in call transcriptions instantly highlights the logs in Crimson Red and alerts host teams via high-priority lanes. Click a keyword chip to remove it.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                        color = subText
                    )

                    // Keyword tag row (scrollable)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (viewModel.emergencyKeywords.isEmpty()) {
                            Text(
                                "No emergency word signals registered.",
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = mutedText,
                                fontFamily = if (isLuxury) Montserrat else FontFamily.Default
                            )
                        } else {
                            viewModel.emergencyKeywords.forEach { word ->
                                Surface(
                                    modifier = Modifier.clickable { viewModel.removeEmergencyKeyword(word) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isLuxury) CrimsonRed.copy(alpha = 0.15f) else CrimsonRed.copy(alpha = 0.08f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isLuxury) CrimsonRed.copy(alpha = 0.6f) else CrimsonRed.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = word.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = CrimsonRed,
                                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                            letterSpacing = 1.sp
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove keyword",
                                            tint = CrimsonRed,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Add Keyword row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = viewModel.newKeywordInput,
                            onValueChange = { viewModel.newKeywordInput = it },
                            placeholder = { Text("e.g. leak", fontSize = 12.sp, color = mutedText) },
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = mainText,
                                unfocusedTextColor = mainText,
                                focusedLabelColor = accentColor,
                                unfocusedLabelColor = mutedText,
                                focusedIndicatorColor = accentColor,
                                unfocusedIndicatorColor = cardBorder
                            )
                        )
                        Button(
                            onClick = {
                                viewModel.addEmergencyKeyword(viewModel.newKeywordInput)
                                viewModel.newKeywordInput = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.secondaryContainer),
                            border = BorderStroke(1.dp, if (isLuxury) EmpireGold.copy(alpha = 0.4f) else Color.Transparent),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add signal word",
                                    tint = accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    "REGISTER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                    color = accentColor,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. ENTERPRISE PREMIUM EXTENSIONS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ENTERPRISE MULTI-SUITE PREMIUM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            color = accentColor,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "INTEGRATED ENGINE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    Text(
                        text = "Configure multiple active office facilities, manage slated team notifications, adjust automatic call routing schemas, and sync calendar pipeline channels.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                        color = subText
                    )

                    HorizontalDivider(color = cardBorder)

                    // Plaza selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "ACTIVE MULTI-SITE OFFICE PLAZA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = mutedText,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val plazas = listOf("New York Plaza (Main)", "London Savoy Hub", "Tokyo Tech Tower")
                            plazas.forEach { plaza ->
                                val isActive = viewModel.selectedLocation == plaza
                                Surface(
                                    modifier = Modifier.clickable { viewModel.selectedLocation = plaza },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isActive) accentColor.copy(alpha = 0.15f) else if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, if (isActive) accentColor else cardBorder)
                                ) {
                                    Text(
                                        text = plaza,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) accentColor else subText,
                                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = cardBorder)

                    // Routing schema
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "VOICE & DISPATCH CALL ROUTING SCHEMA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = mutedText,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val formats = listOf("Direct to Host", "Sequenced Ringing", "Interactive Voice Menu")
                            formats.forEach { mode ->
                                val isActive = viewModel.callRoutingMode == mode
                                Surface(
                                    modifier = Modifier.clickable { viewModel.callRoutingMode = mode },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isActive) accentColor.copy(alpha = 0.15f) else if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, if (isActive) accentColor else cardBorder)
                                ) {
                                    Text(
                                        text = mode,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) accentColor else subText,
                                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = cardBorder)

                    // Host team invites
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "ACTIVE NOTIFICATION TRUSTED ASSOCIATES",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = mutedText,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            letterSpacing = 0.5.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (viewModel.teamMembers.isEmpty()) {
                                Text(
                                    "No staff registered. Defaulting to Lead Agent.",
                                    fontSize = 11.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = mutedText,
                                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default
                                )
                            } else {
                                viewModel.teamMembers.forEach { associate ->
                                    Surface(
                                        modifier = Modifier.clickable { viewModel.removeTeamMember(associate) },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, cardBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = associate,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = subText,
                                                fontFamily = if (isLuxury) Montserrat else FontFamily.Default
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove associate",
                                                tint = CrimsonRed.copy(alpha = 0.7f),
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Invite form
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = viewModel.newTeamMemberInput,
                                onValueChange = { viewModel.newTeamMemberInput = it },
                                placeholder = { Text("Associate full name", fontSize = 12.sp, color = mutedText) },
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = mainText,
                                    unfocusedTextColor = mainText,
                                    focusedLabelColor = accentColor,
                                    unfocusedLabelColor = mutedText,
                                    focusedIndicatorColor = accentColor,
                                    unfocusedIndicatorColor = cardBorder
                                )
                            )
                            Button(
                                onClick = {
                                    viewModel.addTeamMember(viewModel.newTeamMemberInput)
                                    viewModel.newTeamMemberInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.secondaryContainer),
                                border = BorderStroke(1.dp, if (isLuxury) EmpireGold.copy(alpha = 0.4f) else cardBorder),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    "INVITE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = accentColor,
                                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = cardBorder)

                    // Integration API switches
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "EXTERNAL THIRD-PARTY SYSTEM APIS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = mutedText,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            letterSpacing = 0.5.sp
                        )

                        // Google Calendar
                        IntegrationRowItem(
                            title = "Google Calendar Exchange Synchronizer",
                            checked = viewModel.googleCalendarIntegration,
                            onCheckedChange = { viewModel.googleCalendarIntegration = it },
                            isLuxury = isLuxury,
                            accentColor = accentColor,
                            mainText = mainText,
                            mutedText = mutedText
                        )

                        // Salesforce
                        IntegrationRowItem(
                            title = "Salesforce Enterprise Contacts Pipeline",
                            checked = viewModel.salesforceIntegration,
                            onCheckedChange = { viewModel.salesforceIntegration = it },
                            isLuxury = isLuxury,
                            accentColor = accentColor,
                            mainText = mainText,
                            mutedText = mutedText
                        )

                        // HubSpot
                        IntegrationRowItem(
                            title = "HubSpot CRM Contact Auto-Triage",
                            checked = viewModel.hubspotIntegration,
                            onCheckedChange = { viewModel.hubspotIntegration = it },
                            isLuxury = isLuxury,
                            accentColor = accentColor,
                            mainText = mainText,
                            mutedText = mutedText
                        )

                        // Slack
                        IntegrationRowItem(
                            title = "Slack Channel Webhook Broadcaster",
                            checked = viewModel.slackIntegration,
                            onCheckedChange = { viewModel.slackIntegration = it },
                            isLuxury = isLuxury,
                            accentColor = accentColor,
                            mainText = mainText,
                            mutedText = mutedText
                        )
                    }
                }
            }
        }

        // 5. CORE SAVE BUTTON TRIGGER
        item {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.refreshAIRecommendations()
                    viewModel.showSaveConfirmation = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_settings_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                border = BorderStroke(1.dp, if (isLuxury) EmpireGold.copy(alpha = 0.6f) else Color.Transparent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save details",
                        tint = if (isLuxury) ObsidianBlack else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "SAVE CONFIGURATION & SYNC LEDGER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                        color = if (isLuxury) ObsidianBlack else Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // 6. COGNITIVE MAINTENANCE (Sandbox clear)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SANDBOX COGNITIVE SYSTEM RESET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                        color = CrimsonRed,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Wipes local database indices and re-populates clean executive logs into the Room Database cache.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                        color = subText
                    )

                    Button(
                        onClick = { viewModel.resetDemoData() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reset_demo_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "PURGE COGNITIVE INDEX & FLUSH DB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // FOOTER DETAILS
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "T&F AUTOMATE COGNITIVE v1.3.0",
                    fontSize = 11.sp,
                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                    color = accentColor,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "High-Fidelity Executive Virtual Front Desk Platform",
                    fontSize = 10.sp,
                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                    color = subText
                )
            }
        }
    }

    // High-Fidelity Dialog modal
    if (viewModel.showSaveConfirmation) {
        Dialog(onDismissRequest = { viewModel.showSaveConfirmation = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, if (isLuxury) EmpireGold else CoolBlue)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success tick",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "SYNC ACCOMPLISHED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            color = accentColor,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Configurations Confirmed",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = if (isLuxury) CormorantGaramond else FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            color = mainText,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "T&F Automate local synchronized matrices updated database registries successfully.",
                            fontSize = 11.sp,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            color = subText,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                    }

                    HorizontalDivider(color = cardBorder)

                    // Property checklist overview
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .border(1.dp, cardBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ConfirmPropertyRow(label = "Active Palace", value = viewModel.selectedLocation, isLuxury = isLuxury, accent = accentColor, sub = subText)
                        ConfirmPropertyRow(label = "Lead Assistant", value = viewModel.receptionistName, isLuxury = isLuxury, accent = accentColor, sub = subText)
                        ConfirmPropertyRow(label = "Primary Router", value = viewModel.callRoutingMode, isLuxury = isLuxury, accent = accentColor, sub = subText)
                        ConfirmPropertyRow(label = "Auto-SMS Dispatch", value = if (viewModel.smsAlertsEnabled) "Enabled ✔" else "Disabled ✖", isLuxury = isLuxury, accent = if (viewModel.smsAlertsEnabled) EmeraldGreen else CrimsonRed, sub = subText)
                        ConfirmPropertyRow(label = "Word Keywords", value = "${viewModel.emergencyKeywords.size} Active", isLuxury = isLuxury, accent = accentColor, sub = subText)
                    }

                    Button(
                        onClick = { viewModel.showSaveConfirmation = false },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "DISMISS COGNITIVE TOKEN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            color = if (isLuxury) ObsidianBlack else Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isLuxury: Boolean,
    accentColor: Color,
    mainText: Color,
    subText: Color,
    mutedText: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                color = mainText
            )
            Text(
                text = description,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                color = subText
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.35f),
                uncheckedThumbColor = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun IntegrationRowItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isLuxury: Boolean,
    accentColor: Color,
    mainText: Color,
    mutedText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (checked) EmeraldGreen else CrimsonRed)
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                color = if (checked) mainText else mutedText
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.35f),
                uncheckedThumbColor = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun ConfirmPropertyRow(label: String, value: String, isLuxury: Boolean, accent: Color, sub: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
            color = sub.copy(alpha = 0.7f),
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
            color = accent
        )
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
                            color = if (isLuxuryMode) SandPlated else CoolBlue,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = log.visitorName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = if (isLuxuryMode) CormorantGaramond else FontFamily.Default,
                            color = if (isLuxuryMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isLuxuryMode) ObsidianCardAccent else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close drawer",
                            tint = if (isLuxuryMode) SandPlated else MaterialTheme.colorScheme.onSurface
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
                                    color = if (isLuxuryMode) Color.White else MaterialTheme.colorScheme.primary,
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
                            color = if (isLuxuryMode) SandPlated else MaterialTheme.colorScheme.primary,
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
                color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurface
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
    val totalSlides = 5
    val isLuxury = viewModel.isLuxuryThemeActive

    // 5 sequences mapping both the original mockups & the evolution phases
    val slides = listOf(
        EvolutionSlide(
            phase = "SEQUENCE 01",
            title = "Dashboard Hero Board",
            subtitle = "SaaS Revenue Recovery",
            tagline = "Revenue Recovered $12,480 banner above the live metrics row.",
            concept = "A high-fidelity dashboard terminal that displays call volumes, check-in histories, and financial leakage recovery. It stands as the executive operating center for frontline personnel.",
            bullets = listOf(
                "Revenue Recovery Core: $12,480 recovered this week by preventing dropped calls.",
                "Simultaneous Inbound Trunk line: Manages infinite virtual receptionist slots.",
                "Live Activity Audit: Continuously captures, screens, and maps client inquiries."
            ),
            impactColor = EmeraldGreen,
            icon = Icons.Default.CheckCircle,
            metricLabel = "Recovered Value",
            metricValue = "$12,480"
        ),
        EvolutionSlide(
            phase = "SEQUENCE 02",
            title = "Cognitive AI Deep-Link Drawer",
            subtitle = "Unified Transcript Terminal",
            tagline = "Sarah Lee's call captured with intent, urgency, sentiment, confidence, and transcript.",
            concept = "A slide-out intelligent drawer mockup that records customer identity, conversational sentiment, parsing confidence, and real-time transcripts, with zero-touch delivery lines.",
            bullets = listOf(
                "Sarah Lee (Summit Properties): High-intent transaction query.",
                "Deep Semantic Triage: Classifies intent (workspace lease) with 98.2% confidence.",
                "Automated Follow-up: Transmits SMS validation link and booking details."
            ),
            impactColor = CoolBlue,
            icon = Icons.Default.Call,
            metricLabel = "A.I. Transcription Confidence",
            metricValue = "98.2%"
        ),
        EvolutionSlide(
            phase = "SEQUENCE 03",
            title = "Autonomous Pipeline Synchronizer",
            subtitle = "Active Workspace Workflows",
            tagline = "Self-updating database logs, calendar alignments, and host SMS dispatches.",
            concept = "This stage links conversations directly to the physical facility. The front-desk AI books rooms directly into Sqlite databases and alerts hosts without human operator delay.",
            bullets = listOf(
                "Connected Automation: Integrates phone logs, room bookings, and calendar registries.",
                "Lobby Decoupling: Reduces manual desk work, keeping visitor flow automated.",
                "Host Alerts: Sends SMS validation link to the property host 'Terrence'."
            ),
            impactColor = AmberOrange,
            icon = Icons.Default.Build,
            metricLabel = "Avg Check-In Speed",
            metricValue = "1.2s"
        ),
        EvolutionSlide(
            phase = "SEQUENCE 04",
            title = "Executive Capacity & KPI Auditor",
            subtitle = "Operational KPI Strip Summary",
            tagline = "87% automation, 613 bookings, 1,285 calls saved, and $84,200 recovered value.",
            concept = "Macroscopic operations health tracking for property managers and medical office executives. High-contrast indicators offer simple, executive-grade validation.",
            bullets = listOf(
                "High Automation Rate: 87% of routine FAQs cleared without transferring to agents.",
                "High Volume Support: 1,285 calls saved and 613 room bookings formed.",
                "Projected Recovered Value: $84,200 estimated commercial leakage intercepted."
            ),
            impactColor = EmpireGold,
            icon = Icons.Default.Warning,
            metricLabel = "Macroscopic Recovery",
            metricValue = "$84,200"
        ),
        EvolutionSlide(
            phase = "SEQUENCE 05",
            title = "T&F Automate Brand Seal",
            subtitle = "Product Close",
            tagline = "T&F Wordmark paired with powered by T&F Automate frame.",
            concept = "The final seal of operational excellence. Replaces manual front desk strain with deep automation systems that build credibility and trust with executive clientele.",
            bullets = listOf(
                "Hospitality Repositioning: Transforms desk staff into high-value Experience Directors.",
                "Administrative Tax Relief: Eliminates Wi-Fi, direction, and parking query queues.",
                "Supreme Operating System: Zero response latency, infinite scale, 100% security."
            ),
            impactColor = EmeraldGreen,
            icon = Icons.Default.Star,
            metricLabel = "Autonomous Operational Level",
            metricValue = "Phase 5 Perfect"
        )
    )

    val currentSlide = slides[hyperSlideIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Slide Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = currentSlide.phase,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                    color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = currentSlide.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = if (isLuxury) CormorantGaramond else FontFamily.Default,
                    color = if (isLuxury) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "${hyperSlideIndex + 1} of $totalSlides",
                fontSize = 12.sp,
                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                fontWeight = FontWeight.Bold,
                color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Segmented indicator bar using calmer animation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until totalSlides) {
                val isCurrent = i == hyperSlideIndex
                val weightValue = if (isCurrent) 2f else 1f
                val color = if (isCurrent) {
                    if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary
                } else {
                    if (isLuxury) EmpireGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                }
                Box(
                    modifier = Modifier
                        .weight(weightValue)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        // Main Animated Slide Body
        Crossfade(
            targetState = currentSlide,
            animationSpec = tween(durationMillis = 350, easing = LinearOutSlowInEasing),
            label = "slide_fade"
        ) { slide ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CONCEPT METADATA CARD (Left area contents)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = slide.subtitle.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        
                        Text(
                            text = "“${slide.tagline}”",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )

                        HorizontalDivider(
                            color = if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        )

                        Text(
                            text = slide.concept,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                            color = if (isLuxury) SandPlated.copy(alpha = 0.82f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Key Metrics bullets
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            slide.bullets.forEach { bullet ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(
                                        text = "▪",
                                        color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 8.dp),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = bullet,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                        color = if (isLuxury) SandPlated.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Live Metric Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                                .border(1.dp, if (isLuxury) EmpireGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Live Audited Characteristic",
                                    fontSize = 8.sp,
                                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                    color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = slide.metricLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                    color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                    .border(1.dp, if (isLuxury) EmpireGold.copy(alpha = 0.3f) else Color.Transparent, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = slide.metricValue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                    color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // DESK-FRAMED MOCKUP TERMINAL
                Text(
                    text = "DESK-FRAMED PRESENTATION TERMINAL",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                    color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                DeskFramedMockupContainer(
                    isLuxury = isLuxury,
                    slideIndex = hyperSlideIndex
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Slide Navigation Controllers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (hyperSlideIndex > 0) hyperSlideIndex-- },
                enabled = hyperSlideIndex > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isLuxury) Color.White else MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, if (hyperSlideIndex > 0 && isLuxury) EmpireGold.copy(alpha = 0.4f) else Color.Transparent),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Previous Frame", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { if (hyperSlideIndex < totalSlides - 1) hyperSlideIndex++ },
                enabled = hyperSlideIndex < totalSlides - 1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLuxury) ObsidianCard else CoolBlue,
                    contentColor = if (isLuxury) Color.White else Color.White
                ),
                border = BorderStroke(1.dp, if (hyperSlideIndex < totalSlides - 1 && isLuxury) EmpireGold else Color.Transparent),
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

@Composable
fun DeskFramedMockupContainer(isLuxury: Boolean, slideIndex: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.5.dp, 
            if (isLuxury) EmpireGold.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Desk Tablet Bezel Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                }
                Text(
                    text = "DESK STATION ACTIVE SCREEN",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                    color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isLuxury) EmpireGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outlineVariant)
                )
            }

            HorizontalDivider(
                color = if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 5 frames representing each stage
            when (slideIndex) {
                0 -> {
                    // Frame 1: Dashboard Hero — $12,480 banner + Calls/Meetings/Bookings row
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "DAILY OPERATING PORTAL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldGreen))
                                Text("SYSTEM SECURE", fontSize = 8.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Revenue recovered banner
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, if (isLuxury) EmpireGold.copy(alpha = 0.25f) else Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("REVENUE RECOVERED", fontSize = 8.sp, color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("$12,480", fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = if (isLuxury) BebasNeue else FontFamily.Default, color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("prevented lead leakage", fontSize = 8.sp, color = if (isLuxury) SandPlated.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                                }
                            }
                        }

                        // Metrics row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MiniWallboardMetric(modifier = Modifier.weight(1f), label = "Calls saved", value = "1,285", isLuxury = isLuxury)
                            MiniWallboardMetric(modifier = Modifier.weight(1f), label = "Automation Rate", value = "87%", isLuxury = isLuxury)
                            MiniWallboardMetric(modifier = Modifier.weight(1f), label = "Bookings Formed", value = "613", isLuxury = isLuxury)
                        }
                    }
                }
                1 -> {
                    // Frame 2: AI Call Summary Drawer — Sarah Lee's call
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "INTELLIGENT SUMMARY DRAWER DETAIL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Box(
                                modifier = Modifier
                                    .background(EmeraldGreen.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("INTENT SECURED", fontSize = 7.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, if (isLuxury) ObsidianCardAccent else Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Caller: Sarah Lee (Summit Properties)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) Color.White else MaterialTheme.colorScheme.onSurface)
                                    Text("Confidence: 98% ✔", fontSize = 8.sp, color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Text("• Intent Classification: Commercial Suite Lease\n• Urgency: High (Immediate action completed)\n• Sentiment Detected: Positive / Forward-Looking", fontSize = 9.sp, color = if (isLuxury) SandPlated.copy(alpha = 0.82f) else MaterialTheme.colorScheme.onSurfaceVariant)
                                
                                Divider(color = if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.outlineVariant)
                                
                                Text("REAL-TIME AUDIO TRANSCRIPT PREVIEW:", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant)
                                MessageBubble(speaker = "Visitor", text = "Hi, I'm calling from Summit Properties. We are looking to book a dual commercial workspace suite next Wednesday.", isVisitor = true, isLuxury = isLuxury)
                                MessageBubble(speaker = "receptionist AI", text = "Absolutely room A is open! Calendar locked & confirmation sent. We look forward to welcoming you.", isVisitor = false, isLuxury = isLuxury)
                            }
                        }
                    }
                }
                2 -> {
                    // Frame 3: Autonomous Workspace Workflows
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "INTEGRATED SYSTEM EXECUTION PIPELINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldGreen))
                                Text("EXECUTION SUCCESS", fontSize = 8.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, if (isLuxury) ObsidianCardAccent else Color.Transparent)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Automated Pipeline Registers & Database Synchronization Events:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) Color.White else MaterialTheme.colorScheme.onSurface)
                                LogLine(event = "ROOM RESOURCE LOCK-IN", details = "Conference Room A locked for Sarah Lee - SQLite entry verified.", success = true, isLuxury = isLuxury)
                                LogLine(event = "CALENDAR DISPATCH EXECUTED", details = "Calendar invite link synced autonomously to user's device.", success = true, isLuxury = isLuxury)
                                LogLine(event = "ADMINISTRATIVE SMS BROADCAST", details = "Host Terrence alert sent: 'Lobby guest scheduled in room A.'", success = true, isLuxury = isLuxury)
                            }
                        }
                    }
                }
                3 -> {
                    // Frame 4: Executive KPI Strip — 87% automation, 613 bookings, 1,285 calls saved, $84,200 recovered
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "MACROSCOPIC PERFORMANCE AUDITING STRIP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Box(
                                modifier = Modifier
                                    .background(EmpireGold.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("AUDIT PASSED", fontSize = 8.sp, color = if (isLuxury) EmpireGold else CoolBlue, fontWeight = FontWeight.Bold)
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, if (isLuxury) EmpireGold.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("EXECUTIVE METRICS REVIEW SUMMARY", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    KpiBadgeItem(modifier = Modifier.weight(1f), label = "Automation Rate", value = "87%", color = EmeraldGreen, isLuxury = isLuxury)
                                    KpiBadgeItem(modifier = Modifier.weight(1f), label = "Bookings Formed", value = "613", color = CoolBlue, isLuxury = isLuxury)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    KpiBadgeItem(modifier = Modifier.weight(1f), label = "Calls saved today", value = "1,285", color = AmberOrange, isLuxury = isLuxury)
                                    KpiBadgeItem(modifier = Modifier.weight(1f), label = "Revenue Recovered", value = "$84,200", color = EmpireGold, isLuxury = isLuxury)
                                }
                            }
                        }
                    }
                }
                4 -> {
                    // Frame 5: Final brand frame — wordmark + "Powered by T&F Automate" close
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.5.dp,
                            if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "T&F AUTOMATE",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = if (isLuxury) CormorantGaramond else FontFamily.Default,
                                color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary,
                                letterSpacing = 2.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(1.dp)
                                    .background(if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Powered by T&F Automate",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "The Autonomous Intelligent Front Desk of Tomorrow",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Light,
                                fontFamily = if (isLuxury) Montserrat else FontFamily.Default,
                                color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiBadgeItem(modifier: Modifier, label: String, value: String, color: Color, isLuxury: Boolean) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = if (isLuxury) ObsidianBlack else MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label.uppercase(), fontSize = 6.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = if (isLuxury) BebasNeue else FontFamily.Default, color = if (isLuxury) color else MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun LedgerRow(time: String, name: String, status: String, statusColor: Color, isLuxury: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(time, fontSize = 8.sp, fontFamily = if (isLuxury) Montserrat else FontFamily.Default, color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant)
        Text(name, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurface)
        Text(status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = statusColor)
    }
}

@Composable
fun MessageBubble(speaker: String, text: String, isVisitor: Boolean, isLuxury: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isVisitor) Alignment.Start else Alignment.End
    ) {
        Text(
            speaker.uppercase(), 
            fontSize = 7.sp, 
            fontWeight = FontWeight.Bold, 
            color = if (isLuxury) (if (isVisitor) ObsidianMutedText else EmpireGold) else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isLuxury) {
                        if (isVisitor) ObsidianCardAccent else ObsidianBlack
                    } else {
                        if (isVisitor) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    }
                )
                .border(
                    1.dp,
                    if (isLuxury) {
                        if (isVisitor) ObsidianCardAccent else EmpireGold.copy(alpha = 0.2f)
                    } else Color.Transparent,
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(text, fontSize = 10.sp, lineHeight = 14.sp, color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun LogLine(event: String, details: String, success: Boolean, isLuxury: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (success) "✓" else "✗", 
            color = if (success) EmeraldGreen else CrimsonRed, 
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
        Column {
            Text(event, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary)
            Text(details, fontSize = 9.sp, color = if (isLuxury) SandPlated else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun MiniWallboardMetric(modifier: Modifier, label: String, value: String, isLuxury: Boolean) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = if (isLuxury) ObsidianCard else MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isLuxury) ObsidianCardAccent else MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label.uppercase(), fontSize = 6.sp, fontWeight = FontWeight.SemiBold, color = if (isLuxury) ObsidianMutedText else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = if (isLuxury) BebasNeue else FontFamily.Default, color = if (isLuxury) EmpireGold else MaterialTheme.colorScheme.primary)
        }
    }
}
