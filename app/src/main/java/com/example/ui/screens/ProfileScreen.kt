package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProfileEntity
import com.example.ui.CurrentScreen
import com.example.ui.MainViewModel
import com.example.ui.components.FinoraHeaderBar
import com.example.ui.components.finoraTextFieldColors
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    profile: ProfileEntity?,
    onNavigate: (CurrentScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var smsReaderEnabled by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val user = profile ?: ProfileEntity()
    val currencyFormat = rememberCurrencyFormat()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FinoraHeaderBar(
                title = "Profile & Settings",
                subtitle = user.occupation,
                actionIcon = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                onActionClick = { viewModel.toggleDarkMode() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 90.dp)
        ) {
            // User Avatar Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp))
                    .border(1.dp, CardBorderFinora, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PrimaryFinora),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(2).uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryFinora
                        )
                        Text(
                            text = user.occupation,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = SecondaryFinora.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = user.riskProfile,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryFinora
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Income: ${currencyFormat.format(user.monthlyIncome)}/mo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SuccessFinora
                            )
                        }
                    }

                    IconButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = PrimaryFinora
                        )
                    }
                }
            }

            if (showEditProfileDialog) {
                EditProfileDialog(
                    user = user,
                    onDismiss = { showEditProfileDialog = false },
                    onSave = { name, occupation, income, risk ->
                        viewModel.updateUserProfile(name, occupation, income, risk)
                        showEditProfileDialog = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sub-modules Shortcuts Grid
            Text(
                text = "Financial Tools & Modules",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryFinora
            )
            Spacer(modifier = Modifier.height(10.dp))

            ProfileMenuItem(
                title = "Financial Analytics & Trends",
                subtitle = "Monthly spending, category distribution & observations",
                icon = Icons.Default.Analytics,
                onClick = { onNavigate(CurrentScreen.FinancialInsights) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuItem(
                title = "Investment Portfolio Planner",
                subtitle = "Stocks, ETF, Gold, Mutual Funds & Risk Meter",
                icon = Icons.Default.ShowChart,
                onClick = { onNavigate(CurrentScreen.InvestmentPlanner) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuItem(
                title = "Debt & Loan Manager",
                subtitle = "Mortgage, Car loans, EMI schedules & AI payoff priority",
                icon = Icons.Default.CreditCard,
                onClick = { onNavigate(CurrentScreen.DebtManager) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuItem(
                title = "Monthly Budget Planner",
                subtitle = "Category limits & automated threshold alerts",
                icon = Icons.Default.PieChart,
                onClick = { onNavigate(CurrentScreen.BudgetPlanner) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuItem(
                title = "Subscription Optimization Manager",
                subtitle = "Track Netflix, Spotify, Prime & unutilized leaks",
                icon = Icons.Default.Subscriptions,
                onClick = { onNavigate(CurrentScreen.SubscriptionManager) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuItem(
                title = "Bills & Reminders",
                subtitle = "Upcoming electricity, internet & rent due dates",
                icon = Icons.Default.ReceiptLong,
                onClick = { onNavigate(CurrentScreen.BillsReminder) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuItem(
                title = "Calculators & Net Worth",
                subtitle = "Emergency fund calculator, retirement & net worth",
                icon = Icons.Default.Calculate,
                onClick = { onNavigate(CurrentScreen.FinancialCalculators) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Settings Section
            Text(
                text = "Preferences & Security",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryFinora
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Dark Mode Toggle",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Dark Theme Mode",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    if (isDarkMode) "Dark theme enabled across all screens" else "Light theme active",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentFinora),
                            modifier = Modifier.testTag("dark_mode_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sms, contentDescription = null, tint = PrimaryFinora)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Real-Time Bank SMS Reader", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Auto-detects debits, credits, amount & store", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = smsReaderEnabled,
                            onCheckedChange = { smsReaderEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentFinora)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorderFinora)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = PrimaryFinora)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Financial Alerts & Notifications", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = PrimaryFinora)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AccentFinora)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorderFinora)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExporting = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = PrimaryFinora)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Export Financial Reports (CSV / PDF)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = PrimaryFinora)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                    }
                }
            }

            if (isExporting) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Report generated successfully! Saved as Finora_Financial_Summary_2026.csv",
                        modifier = Modifier.padding(14.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(1.dp, RoundedCornerShape(18.dp))
            .border(1.dp, CardBorderFinora, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SecondaryFinora.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = PrimaryFinora, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
            }

            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun EditProfileDialog(
    user: ProfileEntity,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, String) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var occupation by remember { mutableStateOf(user.occupation) }
    var incomeText by remember { mutableStateOf(user.monthlyIncome.toString()) }
    var riskProfile by remember { mutableStateOf(user.riskProfile) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit User Profile", fontWeight = FontWeight.Bold, color = PrimaryFinora) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = occupation,
                    onValueChange = { occupation = it },
                    label = { Text("Occupation / Role") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = incomeText,
                    onValueChange = { incomeText = it },
                    label = { Text("Monthly Income (₹)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = riskProfile,
                    onValueChange = { riskProfile = it },
                    label = { Text("Risk Profile (e.g. Conservative, Moderate, Aggressive)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val income = incomeText.toDoubleOrNull() ?: user.monthlyIncome
                    if (name.isNotBlank()) {
                        onSave(name, occupation, income, riskProfile)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentFinora)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color.White
    )
}
