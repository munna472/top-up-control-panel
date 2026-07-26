package com.telegramautotopup.gateway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telegramautotopup.gateway.ui.theme.TelegramAutoTopUpGatewayTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: GatewayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelegramAutoTopUpGatewayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF020617) // Slate 950
                ) {
                    GatewayApp(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GatewayApp(viewModel: GatewayViewModel) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Gradient Ambience Background Simulation
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x150891B2), Color.Transparent),
                            radius = 1200f
                        )
                    )
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header / Toolbar
            HeaderView()

            // Info Cards row
            InfoCardsRow()

            // Scheduler Panel
            SchedulerPanelView(viewModel)

            // Step 1: UID Verification View
            VerificationPanelView(viewModel)

            // Step 2: Package Selection view
            PackageSelectionView(viewModel)

            // Command Hub (Tab layout)
            CommandHubView(viewModel)

            // Dispatch Summary & Operations View
            DispatchSummaryView(viewModel)

            // Manual Raw Input Terminal
            ManualConsoleTransmitterView(viewModel)

            // Gateway Stats
            GatewayStatsView()

            // Collapsible Admin Settings Settings
            AdminSettingsView(viewModel)

            // Bottom Footer
            FooterView()
        }

        // Toasts Notifications Queue overlay (Floating right top corner)
        ToastNotificationQueueView(
            toasts = viewModel.toasts,
            onDismiss = { viewModel.removeToast(it) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .widthIn(max = 350.dp)
        )
    }
}

@Composable
fun HeaderView() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF0F172A), RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF06B6D4), Color(0xFF10B981))
                        )
                    )
                    .padding(1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color(0xFF020617)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Zap icon",
                        tint = Color(0xFF06B6D4),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column {
                Text(
                    text = "Telegram Auto Top-Up Gateway",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "INSTANT AUTOMATIC PROCESSING NODE",
                    color = Color(0xFF10B981),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Active State Badge
        Row(
            modifier = Modifier
                .background(Color(0xFF020617), RoundedCornerShape(50))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
            )
            Text(
                text = "Active",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun InfoCardsRow() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val cards = listOf(
            Triple(Icons.Default.Security, "SECURE VERIFICATION", "Direct synchronization ensures you check players live in-game database before triggering credit dispatches."),
            Triple(Icons.Default.HourglassEmpty, "INSTANT EXECUTION", "Automated trigger relays directly message your dedicated Telegram bot queue within millisecond cycles."),
            Triple(Icons.Default.Grid3x3, "INTERACTIVE COMMAND PANEL", "Quick actions for balance updates, UC codes, Garena shell vouchers, and live terminal calculation sheets.")
        )
        for (card in cards) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = card.first,
                    contentDescription = null,
                    tint = if (card.second.startsWith("SECURE")) Color(0xFF06B6D4) else if (card.second.startsWith("INSTANT")) Color(0xFF10B981) else Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp).padding(top = 2.dp)
                )
                Column {
                    Text(
                        text = card.second,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = card.third,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SchedulerPanelView(viewModel: GatewayViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x1506B6D4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Schedule",
                            tint = Color(0xFF06B6D4),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "অটোমেটিক রেট চেকার সিডিউলার",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "প্রতিদিন নির্দিষ্ট সময়ে স্বয়ংক্রিয়ভাবে Telegram-এ কমান্ড পাঠায়",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Status Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF020617).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "সিডিউল সার্ভিস অবস্থা",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (viewModel.schedulerEnabled) "সক্রিয় রয়েছে এবং ব্যাকগ্রাউন্ডে চলছে" else "সাময়িকভাবে বন্ধ করা রয়েছে",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = viewModel.schedulerEnabled,
                            onCheckedChange = { viewModel.schedulerEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF10B981),
                                checkedTrackColor = Color(0xFF064E3B)
                            )
                        )
                    }

                    // Configuration fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "সময় (HH:MM)",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = viewModel.schedulerTime,
                                onValueChange = { viewModel.schedulerTime = it },
                                placeholder = { Text("e.g., 07:00", color = Color(0xFF475569)) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF06B6D4),
                                    unfocusedBorderColor = Color(0xFF1E293B),
                                    focusedContainerColor = Color(0xFF020617),
                                    unfocusedContainerColor = Color(0xFF020617)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(
                                text = "টাইমজোন",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Simple toggle selector for timezone in this mobile form factor
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(Color(0xFF020617), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.schedulerTimezone = if (viewModel.schedulerTimezone == "Asia/Dhaka") "UTC" else "Asia/Dhaka"
                                    }
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = viewModel.schedulerTimezone,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Change",
                                    color = Color(0xFF06B6D4),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Command text field
                    Column {
                        Text(
                            text = "Telegram কমান্ড",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = viewModel.schedulerCommand,
                            onValueChange = { viewModel.schedulerCommand = it },
                            placeholder = { Text("e.g., Adiamond", color = Color(0xFF475569)) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06B6D4),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedContainerColor = Color(0xFF020617),
                                unfocusedContainerColor = Color(0xFF020617)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Save and test buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.saveSchedulerConfig() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x2006B6D4)),
                            border = BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("সেটিংস সংরক্ষণ করুন", color = Color(0xFF06B6D4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.triggerSchedulerTestRun() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                            border = BorderStroke(1.dp, Color(0xFF1E293B)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(0.7f)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("টেস্ট রান", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Log history title
                    Text(
                        text = "🕒 সিডিউল এক্সিকিউশন লগ হিস্টোরি (সর্বশেষ ১৫টি)",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    // Logs list box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                            .background(Color(0xFF020617), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        val logs = viewModel.schedulerLogs
                        if (logs.isEmpty()) {
                            Text(
                                text = "এখনও কোনো স্বয়ংক্রিয় লগ রেকর্ড তৈরি হয়নি।",
                                color = Color(0xFF475569),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center).fillMaxWidth()
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(logs) { log ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF0F172A).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = if (log.status == "success") Icons.Default.CheckCircle else Icons.Default.Error,
                                            contentDescription = null,
                                            tint = if (log.status == "success") Color(0xFF10B981) else Color(0xFFF43F5E),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "কমান্ড: \"${log.command}\"",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            if (log.status == "success") Color(0x1510B981) else Color(0x15F43F5E),
                                                            RoundedCornerShape(4.dp)
                                                        )
                                                        .border(
                                                            1.dp,
                                                            if (log.status == "success") Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFF43F5E).copy(alpha = 0.4f),
                                                            RoundedCornerShape(4.dp)
                                                        )
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = log.status.uppercase(),
                                                        color = if (log.status == "success") Color(0xFF10B981) else Color(0xFFF43F5E),
                                                        fontSize = 8.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = log.timestamp,
                                                    color = Color(0xFF475569),
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                if (log.details.isNotEmpty()) {
                                                    Text(
                                                        text = log.details,
                                                        color = Color(0xFF64748B),
                                                        fontSize = 9.sp,
                                                        overflow = TextOverflow.Ellipsis,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Next running timing helper
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x1006B6D4), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(14.dp))
                            Text("পরবর্তী কমান্ড সময়:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Text("${viewModel.schedulerTime} (${viewModel.schedulerTimezone})", color = Color(0xFF06B6D4), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationPanelView(viewModel: GatewayViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header row with toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(6.dp, 12.dp).clip(RoundedCornerShape(1.dp)).background(Color(0xFF06B6D4)))
                    Text(
                        text = "Step 1: Player UID",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .background(Color(0xFF020617).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Live Check:",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                if (viewModel.isVerificationEnabled) Color(0x1506B6D4) else Color(0x1564748B),
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                if (viewModel.isVerificationEnabled) Color(0xFF06B6D4).copy(alpha = 0.4f) else Color(0xFF64748B).copy(alpha = 0.4f),
                                RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                viewModel.onVerificationModeChanged(!viewModel.isVerificationEnabled)
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (viewModel.isVerificationEnabled) "Active (Recommended)" else "Optional (Skipped)",
                            color = if (viewModel.isVerificationEnabled) Color(0xFF06B6D4) else Color(0xFF64748B),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Input and Submit button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.uid,
                    onValueChange = { viewModel.onUidChanged(it) },
                    placeholder = { Text("Enter Player UID (e.g. 2777402998)", color = Color(0xFF475569), fontSize = 11.sp) },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF06B6D4),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF020617),
                        unfocusedContainerColor = Color(0xFF020617)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { if (viewModel.isVerificationEnabled) viewModel.verifyUid() }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                if (viewModel.isVerificationEnabled) {
                    Button(
                        onClick = { viewModel.verifyUid() },
                        enabled = !viewModel.isCheckingUid && viewModel.uid.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0891B2),
                            disabledContainerColor = Color(0xFF0891B2).copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        if (viewModel.isCheckingUid) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verifying...", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verify UID", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Results alerts
            if (!viewModel.isVerificationEnabled && viewModel.uid.trim().isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF020617).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF06B6D4)))
                    Text(
                        text = "Direct routing enabled. Verified player profile lookup is skipped.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            if (viewModel.isCheckingUid) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF020617).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = Color(0xFF06B6D4), modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp)
                    Text(
                        text = "Contacting FreeFire UID Gateway system...",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            viewModel.verifiedName?.let { name ->
                if (viewModel.isVerificationEnabled && name != "Skipped (Optional Mode)") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x1510B981), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                        Column {
                            Text(
                                text = "Database Identity Match",
                                color = Color(0xFF10B981),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Player Name: $name",
                                color = Color(0xFF10B981),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            viewModel.uidError?.let { err ->
                if (viewModel.isVerificationEnabled) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x15F43F5E), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFF43F5E).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(18.dp))
                            Text(text = err, color = Color(0xFFF43F5E), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Text(
                            text = "If you are sure the UID is correct, you can still select a package below and submit the transaction.",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageSelectionView(viewModel: GatewayViewModel) {
    var searchTerm by remember { mutableStateOf("") }
    val filteredPackages = viewModel.packages.filter {
        it.name.contains(searchTerm, ignoreCase = true) || it.commandValue.contains(searchTerm, ignoreCase = true)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header Row with search
            Column {
                Text(
                    text = "Step 2: Select Top-Up Bundle",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pick your desired premium game package or voucher code",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            // Search Box
            OutlinedTextField(
                value = searchTerm,
                onValueChange = { searchTerm = it },
                placeholder = { Text("Search packages...", color = Color(0xFF475569), fontSize = 11.sp) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                ),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(14.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF06B6D4),
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedContainerColor = Color(0xFF020617),
                    unfocusedContainerColor = Color(0xFF020617)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Packages list container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .background(Color(0xFF020617).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                if (filteredPackages.isEmpty()) {
                    Text(
                        text = "No active bundles matching search filters found.",
                        color = Color(0xFF475569),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredPackages) { pkg ->
                            val isSelected = viewModel.selectedPackage?.id == pkg.id
                            val isDiamond = pkg.name.contains("diamond", ignoreCase = true)
                            val isMember = pkg.name.contains("membership", ignoreCase = true)
                            val isPass = pkg.name.contains("pass", ignoreCase = true) || pkg.name.contains("lite", ignoreCase = true) || pkg.name.contains("evo", ignoreCase = true)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) Color(0x2006B6D4) else Color(0xFF0F172A)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF06B6D4) else Color(0xFF1E293B),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.selectedPackage = pkg }
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = pkg.name,
                                            color = if (isSelected) Color(0xFF06B6D4) else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Selection Status Dot Indicator
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .border(1.dp, if (isSelected) Color(0xFF06B6D4) else Color(0xFF475569))
                                                .padding(2.dp)
                                        ) {
                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF06B6D4))
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Command",
                                            color = Color(0xFF475569),
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )

                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isSelected) Color(0xFF083344) else Color(0xFF020617),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) Color(0xFF06B6D4).copy(alpha = 0.5f) else Color(0xFF1E293B),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = pkg.commandValue,
                                                color = if (isSelected) Color(0xFF22D3EE) else Color(0xFF94A3B8),
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommandHubView(viewModel: GatewayViewModel) {
    var activeTab by remember { mutableStateOf("account") }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                "account" to "👤 Account & Billing",
                "vouchers" to "🎮 Vouchers",
                "rates" to "📊 Rate Sheets",
                "utilities" to "🛠️ Tools"
            )
            for (tab in tabs) {
                val isTabSelected = activeTab == tab.first
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isTabSelected) Color(0xFF0891B2) else Color.Transparent
                        )
                        .clickable { activeTab = tab.first }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.second.split(" ").last(), // Icon/Name condensed for mobile
                        color = if (isTabSelected) Color.White else Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Tab content
        when (activeTab) {
            "account" -> AccountTabContent(viewModel)
            "vouchers" -> VouchersTabContent(viewModel)
            "rates" -> RatesTabContent(viewModel)
            "utilities" -> UtilitiesTabContent(viewModel)
        }
    }
}

@Composable
fun AccountTabContent(viewModel: GatewayViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Quick commands
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Client Account Actions",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Click any profile button below to retrieve live statistics directly from your synced account backend.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                // 2x2 Grid of Quick triggers
                val actions = listOf(
                    Triple("Aprofile", "View Profile Info", Color(0xFF22D3EE)),
                    Triple("Abalance", "Check Balance", Color(0xFF34D399)),
                    Triple("Adue", "Outstanding Due", Color(0xFFF87171)),
                    Triple("Amyinfo", "Telegram Details", Color(0xFFC084FC))
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 0..1) {
                        val action = actions[i]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF020617), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                .clickable { viewModel.dispatchCommandToTelegram(action.first, action.second) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Icon(
                                    imageVector = if (action.first == "Aprofile") Icons.Default.AccountBox else Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = action.third,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(action.first, color = Color(0xFF64748B), fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Text(action.second, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 2..3) {
                        val action = actions[i]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF020617), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                .clickable { viewModel.dispatchCommandToTelegram(action.first, action.second) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Icon(
                                    imageVector = if (action.first == "Adue") Icons.Default.Schedule else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = action.third,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(action.first, color = Color(0xFF64748B), fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                Text(action.second, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Reset outstanding due CTA
                Button(
                    onClick = { viewModel.dispatchCommandToTelegram("Aresetbaki", "Reset/Clear Due baki") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x15F43F5E)),
                    border = BorderStroke(1.dp, Color(0xFFF43F5E).copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔄 Aresetbaki (Clear Outstanding Due)", color = Color(0xFFF43F5E), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Verification & Cash In
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Add Cash & verify Payment",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Check the operator's active number to cash-in, and then input your TrxID below for dynamic activation.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                // Merchant numbers button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF020617), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .clickable { viewModel.dispatchCommandToTelegram("Anumber", "Check Merchant Numbers") }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0x1506B6D4), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(16.dp))
                            }
                            Column {
                                Text("📱 View Merchant Numbers", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Trigger standard cash-out / send money list commands", color = Color(0xFF64748B), fontSize = 9.sp)
                            }
                        }
                        Text("Anumber →", color = Color(0xFF06B6D4), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                // Verify Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF020617).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Verify with TrxID (Averify)", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = viewModel.trxId,
                            onValueChange = { viewModel.trxId = it },
                            placeholder = { Text("Enter TrxID (e.g. BG6JS8JD)", color = Color(0xFF475569), fontSize = 11.sp) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06B6D4),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedContainerColor = Color(0xFF020617),
                                unfocusedContainerColor = Color(0xFF020617)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = { viewModel.handleVerifyTrx() },
                            enabled = viewModel.trxId.trim().isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Verify TrxID", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "Sends the live string: `Averify [trxID]`. E.g., `Averify BG6JS8JD`",
                        color = Color(0xFF475569),
                        fontSize = 9.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun VouchersTabContent(viewModel: GatewayViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Unipin UC card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🎮 UniPin UC Purchase Hub",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Select or type specific UC parameters options to dispatch commands. E.g. `Auc 161 4`",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                // Presets
                Text("Select UC Preset Value", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf("161", "320", "610")
                    for (preset in presets) {
                        val isPresetSelected = viewModel.ucValue == preset && !viewModel.useCustomUc
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isPresetSelected) Color(0x2006B6D4) else Color(0xFF020617)
                                )
                                .border(
                                    1.dp,
                                    if (isPresetSelected) Color(0xFF06B6D4) else Color(0xFF1E293B),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    viewModel.ucValue = preset
                                    viewModel.useCustomUc = false
                                }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$preset UC", color = if (isPresetSelected) Color(0xFF06B6D4) else Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Custom box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF020617).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Custom UC Value", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Checkbox(
                                checked = viewModel.useCustomUc,
                                onCheckedChange = { viewModel.useCustomUc = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF06B6D4))
                            )
                            Text("Use custom value", color = Color.White, fontSize = 11.sp)
                        }
                    }

                    if (viewModel.useCustomUc) {
                        OutlinedTextField(
                            value = viewModel.customUc,
                            onValueChange = { viewModel.customUc = it },
                            placeholder = { Text("Enter custom UC code value", color = Color(0xFF475569), fontSize = 11.sp) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06B6D4),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedContainerColor = Color(0xFF020617),
                                unfocusedContainerColor = Color(0xFF020617)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("PACK QUANTITY", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("${viewModel.ucQty} Pack(s)", color = Color(0xFF06B6D4), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = viewModel.ucQty.toFloat(),
                        onValueChange = { viewModel.ucQty = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF06B6D4),
                            activeTrackColor = Color(0xFF06B6D4)
                        )
                    )
                    Text("⚠️ Max 5 Top-Ups simultaneously", color = Color(0xFF475569), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                }

                // Submit button
                Button(
                    onClick = { viewModel.handleUcSubmit() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    val selectedVal = if (viewModel.useCustomUc) viewModel.customUc else viewModel.ucValue
                    Text("Dispatch UC Order: ${if (selectedVal.isEmpty()) "?" else selectedVal} UC", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Garena Shell store card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🎯 Garena Shell Store",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pick amount and quantity for instant Garena Shell activations, formatting `Ashell [amount] [qty]`.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                // Presets (50, 100, 330, 500)
                Text("Select Shell Amount", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf("50", "100", "330", "500")
                    for (preset in presets) {
                        val isPresetSelected = viewModel.shellAmount == preset
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isPresetSelected) Color(0x2006B6D4) else Color(0xFF020617)
                                )
                                .border(
                                    1.dp,
                                    if (isPresetSelected) Color(0xFF06B6D4) else Color(0xFF1E293B),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.shellAmount = preset }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$preset Shells", color = if (isPresetSelected) Color(0xFF06B6D4) else Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("PACK QUANTITY", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("${viewModel.shellQty} Pack(s)", color = Color(0xFF06B6D4), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = viewModel.shellQty.toFloat(),
                        onValueChange = { viewModel.shellQty = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF06B6D4),
                            activeTrackColor = Color(0xFF06B6D4)
                        )
                    )
                    Text("⚠️ Max 5 Top-Ups simultaneously", color = Color(0xFF475569), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                }

                // Command outbed preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF020617).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Command payload outbed: Ashell ${viewModel.shellAmount} ${if (viewModel.shellQty > 1) viewModel.shellQty else ""}",
                        color = Color(0xFF06B6D4),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Dispatch button
                Button(
                    onClick = { viewModel.handleShellSubmit() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dispatch Garena shell order", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RatesTabContent(viewModel: GatewayViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "📊 Rate Sheets Enquiry",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap on any check preset below to query live rate databases via Telegram bot actions directly.",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )

            val checkCards = listOf(
                Triple("Arate", "General Product Rates", "Get overall retail and reseller rate tables."),
                Triple("Apacks", "Diamond Packs Rates", "Query precise commands to use for direct diamonds."),
                Triple("Adiamond", "BD & Indo Diamonds", "Compare regional FreeFire server diamond pricing."),
                Triple("Alist", "UC vs Diamond Values", "View comprehensive unit comparison listing.")
            )

            for (card in checkCards) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF020617), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .clickable { viewModel.dispatchCommandToTelegram(card.first, card.second) }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Inquiry Command • ${card.first}", color = Color(0xFF64748B), fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Text(card.second, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(card.third, color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun UtilitiesTabContent(viewModel: GatewayViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Calculator
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🧮 Interactive Terminal Calculator",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Type equations directly (e.g., 100+100, 100-90, 100*6, 100/10). Triggers results instantly and logs math command packets.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = viewModel.calcExpr,
                    onValueChange = {
                        viewModel.calcExpr = it
                        viewModel.calcResult = null
                    },
                    placeholder = { Text("e.g. 100+100-90", color = Color(0xFF475569), fontSize = 11.sp) },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF06B6D4),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF020617),
                        unfocusedContainerColor = Color(0xFF020617)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                viewModel.calcResult?.let { res ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x1510B981), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Result:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text(res.toString(), color = Color(0xFF10B981), fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }

                Button(
                    onClick = { viewModel.handleCalculatorEvaluate() },
                    enabled = viewModel.calcExpr.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Evaluate & Dispatch Command", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Direct search
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "🔍 Direct UID Details Search",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Trigger plain ID validation details from server queue. Writing simply raw digits is passed directly.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = viewModel.queryUid,
                    onValueChange = { viewModel.queryUid = it },
                    placeholder = { Text("e.g. 2232962333", color = Color(0xFF475569), fontSize = 11.sp) },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF06B6D4),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF020617),
                        unfocusedContainerColor = Color(0xFF020617)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.handleQueryUidSubmit() },
                        enabled = viewModel.queryUid.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Request Details", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    if (viewModel.queryUid.trim().isNotEmpty()) {
                        Button(
                            onClick = {
                                viewModel.onUidChanged(viewModel.queryUid.trim())
                                viewModel.queryUid = ""
                                viewModel.verifyUid()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                            border = BorderStroke(1.dp, Color(0xFF1E293B)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Load", tint = Color(0xFF06B6D4), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DispatchSummaryView(viewModel: GatewayViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.8f)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dispatch Summary",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFF020617), RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Pending Sign",
                        color = Color(0xFF94A3B8),
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Summary Info Lists
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF020617).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val items = listOf(
                    "Target UID" to Pair(viewModel.uid.trim(), viewModel.uid.trim().isNotEmpty()),
                    "Verified Name" to Pair(viewModel.verifiedName ?: "", viewModel.verifiedName != null),
                    "Selected Product" to Pair(viewModel.selectedPackage?.name ?: "", viewModel.selectedPackage != null),
                    "Payload Cmd" to Pair(viewModel.selectedPackage?.commandValue ?: "", viewModel.selectedPackage != null)
                )

                for (item in items) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.first, color = Color(0xFF64748B), fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                        if (item.second.second) {
                            Text(
                                text = item.second.first,
                                color = if (item.first == "Verified Name") Color(0xFF10B981) else if (item.first == "Selected Product") Color(0xFF06B6D4) else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = if (item.first == "Verified Name") "Not verified" else if (item.first == "Selected Product") "None Highlighted" else "Unspecified",
                                color = Color(0xFFF59E0B),
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    if (item != items.last()) {
                        Divider(color = Color(0xFF020617), thickness = 1.dp)
                    }
                }
            }

            // Quantity selector if package is selected
            viewModel.selectedPackage?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF020617).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOPUP QUANTITY", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("${viewModel.topUpQty} Times", color = Color(0xFF06B6D4), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = viewModel.topUpQty.toFloat(),
                        onValueChange = { viewModel.topUpQty = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF06B6D4), activeTrackColor = Color(0xFF06B6D4))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1 Unit (Standard)", color = Color(0xFF475569), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text("Max 5 Units simultaneously", color = Color(0xFF475569), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                // Payload preview box
                if (viewModel.uid.trim().isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Relay Packet Preview", color = Color(0xFF475569), fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF020617), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF06B6D4).copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val textPreview = if (viewModel.topUpQty > 1) {
                                    "Atp ${viewModel.uid.trim()} ${it.commandValue} ${viewModel.topUpQty}"
                                } else {
                                    "Atp ${viewModel.uid.trim()} ${it.commandValue}"
                                }

                                Text(
                                    text = textPreview,
                                    color = Color(0xFF22D3EE),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f)
                                )

                                Box(
                                    modifier = Modifier
                                        .background(Color(0x1506B6D4), RoundedCornerShape(4.dp))
                                        .border(1.dp, Color(0xFF06B6D4).copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("SYS_CMD", color = Color(0xFF06B6D4), fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Warnings
            if (viewModel.uid.trim().isNotEmpty() && viewModel.verifiedName == null && viewModel.isVerificationEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x15F59E0B), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp).padding(top = 1.dp))
                    Text(
                        text = "Proceeding without verifying UID might dispatch credits to an accidental account.",
                        color = Color(0xFFF59E0B),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            // Submission CTA Button
            val isEnabled = viewModel.uid.trim().isNotEmpty() && viewModel.selectedPackage != null && viewModel.cooldown == 0 && !viewModel.isSubmitting
            Button(
                onClick = { viewModel.handleSubmitTopUp() },
                enabled = isEnabled,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0891B2),
                    disabledContainerColor = Color(0xFF1E293B)
                ),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Broadcasting...", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else if (viewModel.cooldown > 0) {
                    Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Locked (Cooldown: ${viewModel.cooldown}s)", color = Color(0xFF64748B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null, tint = if (isEnabled) Color.White else Color(0xFF475569), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Top-Up Queue", color = if (isEnabled) Color.White else Color(0xFF475569), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (viewModel.cooldown > 0) {
                Text(
                    text = "Telegram anti-flood protection active.",
                    color = Color(0xFF475569),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun ManualConsoleTransmitterView(viewModel: GatewayViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.8f)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF083344), RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFF06B6D4), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("RAW", color = Color(0xFF22D3EE), fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
                Text("Console Packet Transmitter", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Need to fire a specific customized command string? Enter your text below to trigger immediately.",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )

            OutlinedTextField(
                value = viewModel.rawTerminalCommand,
                onValueChange = { viewModel.rawTerminalCommand = it },
                placeholder = { Text("e.g., Atp 2232962333 lite 2", color = Color(0xFF475569), fontSize = 11.sp) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF06B6D4),
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedContainerColor = Color(0xFF020617),
                    unfocusedContainerColor = Color(0xFF020617)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.handleManualConsoleSubmit() },
                enabled = viewModel.rawTerminalCommand.trim().isNotEmpty() && viewModel.cooldown == 0 && !viewModel.isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), disabledContainerColor = Color(0xFF1E293B).copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Transmit Raw Packet", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun GatewayStatsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Gateway Statistics", color = Color(0xFF64748B), fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Stat 1
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFF020617).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text("QUEUE STATUS", color = Color(0xFF475569), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        Text("Operational", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // Stat 2
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFF020617).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text("RESPONSE LATENCY", color = Color(0xFF475569), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("~120 ms", color = Color(0xFF06B6D4), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun AdminSettingsView(viewModel: GatewayViewModel) {
    var expanded by remember { mutableStateOf(false) }

    // Forms
    var newName by remember { mutableStateOf("") }
    var newCmd by remember { mutableStateOf("") }

    // Editing State
    var editingId by remember { mutableStateOf<String?>(null) }
    var editingName by remember { mutableStateOf("") }
    var editingCmd by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.8f)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Toggle row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x1506B6D4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Admin Gateway Settings", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF083344), RoundedCornerShape(50))
                                    .border(1.dp, Color(0xFF06B6D4), RoundedCornerShape(50))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("${viewModel.packages.size} active", color = Color(0xFF22D3EE), fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("Configure top-up packages, credentials, and custom commands", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
                Icon(imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = Color(0xFF94A3B8))
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Create Form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF020617).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Create Custom Package", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newName,
                                onValueChange = { newName = it },
                                placeholder = { Text("Package Name", color = Color(0xFF475569), fontSize = 11.sp) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 12.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF06B6D4),
                                    unfocusedBorderColor = Color(0xFF1E293B),
                                    focusedContainerColor = Color(0xFF020617),
                                    unfocusedContainerColor = Color(0xFF020617)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = newCmd,
                                onValueChange = { newCmd = it },
                                placeholder = { Text("Command Code", color = Color(0xFF475569), fontSize = 11.sp) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF06B6D4),
                                    unfocusedBorderColor = Color(0xFF1E293B),
                                    focusedContainerColor = Color(0xFF020617),
                                    unfocusedContainerColor = Color(0xFF020617)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.addPackage(newName, newCmd)
                                newName = ""
                                newCmd = ""
                            },
                            enabled = newName.trim().isNotEmpty() && newCmd.trim().isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Package", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Database management header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Manage Package Database", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { viewModel.resetPackagesToDefault() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x15F43F5E)),
                            border = BorderStroke(1.dp, Color(0xFFF43F5E).copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset to Defaults", color = Color(0xFFF43F5E), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Package list
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .background(Color(0xFF020617).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(viewModel.packages) { pkg ->
                                val isEditing = editingId == pkg.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F172A).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (isEditing) {
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            OutlinedTextField(
                                                value = editingName,
                                                onValueChange = { editingName = it },
                                                placeholder = { Text("Name", fontSize = 11.sp) },
                                                singleLine = true,
                                                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 11.sp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF06B6D4),
                                                    unfocusedBorderColor = Color(0xFF1E293B),
                                                    focusedContainerColor = Color(0xFF020617)
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            OutlinedTextField(
                                                value = editingCmd,
                                                onValueChange = { editingCmd = it },
                                                placeholder = { Text("Command", fontSize = 11.sp) },
                                                singleLine = true,
                                                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF06B6D4),
                                                    unfocusedBorderColor = Color(0xFF1E293B),
                                                    focusedContainerColor = Color(0xFF020617)
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.editPackage(pkg.id, editingName, editingCmd)
                                                    editingId = null
                                                },
                                                modifier = Modifier.size(28.dp).background(Color(0x1510B981), RoundedCornerShape(4.dp))
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                            }

                                            IconButton(
                                                onClick = { editingId = null },
                                                modifier = Modifier.size(28.dp).background(Color(0xFF020617), RoundedCornerShape(4.dp))
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    } else {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(pkg.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("Cmd: ${pkg.commandValue}", color = Color(0xFF64748B), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = {
                                                    editingId = pkg.id
                                                    editingName = pkg.name
                                                    editingCmd = pkg.commandValue
                                                },
                                                modifier = Modifier.size(28.dp).background(Color(0x1006B6D4), RoundedCornerShape(4.dp))
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(12.dp))
                                            }

                                            IconButton(
                                                onClick = { viewModel.deletePackage(pkg.id) },
                                                modifier = Modifier.size(28.dp).background(Color(0x10F43F5E), RoundedCornerShape(4.dp))
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(12.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Credentials card & Custom Credentials Configuration Form
                    var tokenInput by remember { mutableStateOf(viewModel.botToken) }
                    var chatIdInput by remember { mutableStateOf(viewModel.chatId) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF020617).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Telegram Channel Node Configuration", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = tokenInput,
                            onValueChange = { tokenInput = it },
                            placeholder = { Text("Bot Token", fontSize = 11.sp) },
                            label = { Text("Telegram Bot Token", fontSize = 10.sp, color = Color(0xFF94A3B8)) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06B6D4),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedContainerColor = Color(0xFF020617),
                                unfocusedContainerColor = Color(0xFF020617)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = chatIdInput,
                            onValueChange = { chatIdInput = it },
                            placeholder = { Text("Chat ID", fontSize = 11.sp) },
                            label = { Text("Target Chat ID", fontSize = 10.sp, color = Color(0xFF94A3B8)) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06B6D4),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedContainerColor = Color(0xFF020617),
                                unfocusedContainerColor = Color(0xFF020617)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                viewModel.saveTelegramCredentials(tokenInput, chatIdInput)
                            },
                            enabled = tokenInput.trim().isNotEmpty() && chatIdInput.trim().isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Bot Credentials", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Divider(color = Color(0xFF1E293B), thickness = 1.dp)

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Active Token: ${viewModel.botToken.take(15)}...", color = Color(0xFF06B6D4), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Text("Active Chat ID: ${viewModel.chatId}", color = Color(0xFF10B981), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FooterView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Divider(color = Color(0xFF0F172A), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "ATG Terminal Node v1.2.0 • End-to-end sandbox pipeline protocol active.",
            color = Color(0xFF475569),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )
        Text(
            text = "For testing and gaming operations dispatching exclusively.",
            color = Color(0xFF475569),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ToastNotificationQueueView(
    toasts: List<ToastMessage>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (toast in toasts) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (toast.type) {
                            ToastType.SUCCESS -> Color(0xFF064E3B).copy(alpha = 0.95f)
                            ToastType.ERROR -> Color(0xFF4C0519).copy(alpha = 0.95f)
                            ToastType.INFO -> Color(0xFF0F172A).copy(alpha = 0.95f)
                        }
                    )
                    .border(
                        1.dp,
                        when (toast.type) {
                            ToastType.SUCCESS -> Color(0xFF10B981).copy(alpha = 0.3f)
                            ToastType.ERROR -> Color(0xFFF43F5E).copy(alpha = 0.3f)
                            ToastType.INFO -> Color(0xFF1E293B)
                        },
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                // Left dynamic strip indicator color
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                        .background(
                            when (toast.type) {
                                ToastType.SUCCESS -> Color(0xFF10B981)
                                ToastType.ERROR -> Color(0xFFF43F5E)
                                ToastType.INFO -> Color(0xFF06B6D4)
                            }
                        )
                        .align(Alignment.CenterStart)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFF06B6D4),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = toast.message,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        toast.description?.let { desc ->
                            Text(
                                text = desc,
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 13.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Toast",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onDismiss(toast.id) }
                    )
                }
            }
        }
    }
}
