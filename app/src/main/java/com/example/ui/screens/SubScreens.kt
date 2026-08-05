package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.*
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlin.math.pow

// ----------------------------------------------------
// 1. FINANCIAL INSIGHTS SCREEN
// ----------------------------------------------------
@Composable
fun FinancialInsightsScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = rememberCurrencyFormat()
    val healthMetrics by viewModel.healthMetrics.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("financial_insights_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundFinora,
        topBar = {
            FinoraHeaderBar(
                title = "Financial Insights",
                subtitle = "August 2026 Analytics",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Cash Flow Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryFinora)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Monthly Cash Flow", fontSize = 13.sp, color = SecondaryFinora)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("+${currencyFormat.format(healthMetrics.netCashFlow)}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Income: ${currencyFormat.format(healthMetrics.totalIncome)}  |  Expenses: ${currencyFormat.format(healthMetrics.totalExpenses)}", fontSize = 12.sp, color = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Observations Cards
            Text("AI Smart Observations", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
            Spacer(modifier = Modifier.height(10.dp))

            if (healthMetrics.totalIncome == 0.0 && healthMetrics.totalExpenses == 0.0) {
                ObservationCard(
                    title = "No Data Recorded",
                    desc = "Add income and expense transactions to unlock real-time Finora AI cash flow analysis.",
                    icon = Icons.Default.Info,
                    tag = "Get Started"
                )
            } else {
                if (healthMetrics.netCashFlow >= 0) {
                    ObservationCard(
                        title = "Positive Cash Flow",
                        desc = "You have saved ${currencyFormat.format(healthMetrics.netCashFlow)} this month. Consider investing or saving toward goals.",
                        icon = Icons.Default.Shield,
                        tag = "Healthy"
                    )
                } else {
                    ObservationCard(
                        title = "Expenses Exceed Income",
                        desc = "Your expenses exceed income by ${currencyFormat.format(healthMetrics.totalExpenses - healthMetrics.totalIncome)}. Review budget categories.",
                        icon = Icons.Default.Warning,
                        tag = "Warning"
                    )
                }

                if (healthMetrics.totalExpenses > 0) {
                    ObservationCard(
                        title = "Expense Track Active",
                        desc = "Total recorded expenses stand at ${currencyFormat.format(healthMetrics.totalExpenses)}.",
                        icon = Icons.Default.Analytics,
                        tag = "Analysis"
                    )
                }
            }
        }
    }
}

@Composable
fun ObservationCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tag: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderFinora, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SecondaryFinora.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = PrimaryFinora, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(color = AccentFinora.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                        Text(text = tag, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentFinora)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = desc, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

// ----------------------------------------------------
// 2. INVESTMENT PLANNER SCREEN
// ----------------------------------------------------
@Composable
fun InvestmentPlannerScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = rememberCurrencyFormat()
    val investments by viewModel.investments.collectAsState()
    val totalPortfolio = investments.sumOf { it.currentValue }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("investment_planner_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundFinora,
        topBar = { FinoraHeaderBar(title = "Investment Portfolio", subtitle = "Wealth & Growth", onBackClick = onBackClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentFinora,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Investment")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryFinora)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Total Portfolio Value", fontSize = 13.sp, color = SecondaryFinora)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(currencyFormat.format(totalPortfolio), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("+11.4% Overall Un-realized Gain", fontSize = 12.sp, color = SuccessFinora, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Holdings Breakdown", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
            Spacer(modifier = Modifier.height(10.dp))

            if (investments.isEmpty()) {
                FinoraEmptyState(
                    title = "No Investments Added",
                    message = "Track your Stocks, Mutual Funds, Gold, and Crypto holdings.",
                    buttonText = "Add Investment",
                    onButtonClick = { showAddDialog = true },
                    icon = Icons.Default.ShowChart
                )
            } else {
                investments.forEach { inv ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .border(1.dp, CardBorderFinora, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(inv.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                                Text("Risk: ${inv.riskLevel} | Type: ${inv.type.name}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(currencyFormat.format(inv.currentValue), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                                    Text("+${inv.expectedReturnPercent}%", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = { viewModel.deleteInvestment(inv.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Investment", tint = Color.Gray, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddInvestmentDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, type, invested, current, returnPct, risk ->
                    viewModel.addInvestment(name, type, invested, current, returnPct, risk)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddInvestmentDialog(
    onDismiss: () -> Unit,
    onSave: (String, InvestmentType, Double, Double, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var investedText by remember { mutableStateOf("") }
    var currentText by remember { mutableStateOf("") }
    var returnText by remember { mutableStateOf("12.5") }
    var riskLevel by remember { mutableStateOf("Moderate") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Investment", fontWeight = FontWeight.Bold, color = PrimaryFinora) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Holding Name (e.g., Nifty 50 Index)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = investedText,
                    onValueChange = { investedText = it },
                    label = { Text("Invested Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = currentText,
                    onValueChange = { currentText = it },
                    label = { Text("Current Value (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = riskLevel,
                    onValueChange = { riskLevel = it },
                    label = { Text("Risk Level (Low, Moderate, High)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val inv = investedText.toDoubleOrNull() ?: 0.0
                    val cur = currentText.toDoubleOrNull() ?: inv
                    val ret = returnText.toDoubleOrNull() ?: 12.0
                    if (name.isNotBlank() && inv > 0) {
                        onSave(name, InvestmentType.MUTUAL_FUND, inv, cur, ret, riskLevel)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentFinora)
            ) {
                Text("Add Holding", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = Color.White
    )
}

// ----------------------------------------------------
// 3. DEBT MANAGER SCREEN
// ----------------------------------------------------
@Composable
fun DebtManagerScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = rememberCurrencyFormat()
    val debts by viewModel.debts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("debt_manager_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundFinora,
        topBar = { FinoraHeaderBar(title = "Debt & Loan Manager", subtitle = "Payoff Accelerator", onBackClick = onBackClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentFinora,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Loan")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (debts.isEmpty()) {
                FinoraEmptyState(
                    title = "No Active Debts",
                    message = "Track Mortgages, Car Loans, EMI plans, and Credit Card payables.",
                    buttonText = "Add Debt / Loan",
                    onButtonClick = { showAddDialog = true },
                    icon = Icons.Default.CreditCard
                )
            } else {
                debts.forEach { debt ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .shadow(2.dp, RoundedCornerShape(20.dp))
                            .border(1.dp, CardBorderFinora, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(debt.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(color = AccentFinora.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)) {
                                        Text(debt.aiPriority, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentFinora)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(onClick = { viewModel.deleteDebt(debt.id) }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Debt", tint = Color.Gray, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Outstanding: ${currencyFormat.format(debt.outstandingAmount)}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryFinora)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Monthly EMI: ${currencyFormat.format(debt.emiAmount)}  |  ${debt.interestRatePercent}% APR", fontSize = 12.sp, color = Color.Gray)
                                Button(
                                    onClick = { viewModel.payEmi(debt.id, debt.outstandingAmount, debt.emiAmount) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessFinora),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text("Pay EMI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddDebtDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, total, outstanding, rate, emi, months ->
                    viewModel.addDebt(name, LoanType.PERSONAL, total, outstanding, rate, emi, months)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddDebtDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, Double, Double, Double, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var totalText by remember { mutableStateOf("") }
    var rateText by remember { mutableStateOf("8.5") }
    var emiText by remember { mutableStateOf("") }
    var monthsText by remember { mutableStateOf("36") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Loan / Debt", fontWeight = FontWeight.Bold, color = PrimaryFinora) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Loan Title (e.g. Car Loan)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = totalText,
                    onValueChange = { totalText = it },
                    label = { Text("Outstanding Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = it },
                    label = { Text("Interest Rate % APR") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = emiText,
                    onValueChange = { emiText = it },
                    label = { Text("Monthly EMI Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val total = totalText.toDoubleOrNull() ?: 0.0
                    val rate = rateText.toDoubleOrNull() ?: 8.5
                    val emi = emiText.toDoubleOrNull() ?: (total / 36.0)
                    val months = monthsText.toIntOrNull() ?: 36
                    if (name.isNotBlank() && total > 0) {
                        onSave(name, total, total, rate, emi, months)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentFinora)
            ) {
                Text("Add Loan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = Color.White
    )
}

// ----------------------------------------------------
// 4. BUDGET PLANNER SCREEN
// ----------------------------------------------------
@Composable
fun BudgetPlannerScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = rememberCurrencyFormat()
    val budgets by viewModel.budgets.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("budget_planner_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundFinora,
        topBar = { FinoraHeaderBar(title = "Monthly Budget Planner", subtitle = "Limit Management", onBackClick = onBackClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentFinora,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Set Budget")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (budgets.isEmpty()) {
                FinoraEmptyState(
                    title = "No Budget Limits Defined",
                    message = "Set spending caps for Food, Travel, Shopping, and Utilities to stay on track.",
                    buttonText = "Set Category Budget",
                    onButtonClick = { showAddDialog = true },
                    icon = Icons.Default.PieChart
                )
            } else {
                budgets.forEach { b ->
                    val spent = transactions
                        .filter { it.type == TransactionType.EXPENSE && it.category.equals(b.category, ignoreCase = true) }
                        .sumOf { it.amount }
                    val progress = if (b.limitAmount > 0) (spent / b.limitAmount).toFloat().coerceIn(0f, 1f) else 0f

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .border(1.dp, CardBorderFinora, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(b.category, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${currencyFormat.format(spent)} / ${currencyFormat.format(b.limitAmount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (progress >= 0.9f) AccentFinora else PrimaryFinora)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(onClick = { viewModel.deleteBudget(b.id) }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Budget", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = if (progress >= 0.9f) AccentFinora else PrimaryFinora,
                                trackColor = SecondaryFinora.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddBudgetDialog(
                onDismiss = { showAddDialog = false },
                onSave = { category, limit ->
                    viewModel.addBudget(category, limit)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit
) {
    var category by remember { mutableStateOf("Food & Dining") }
    var limitText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Monthly Category Budget", fontWeight = FontWeight.Bold, color = PrimaryFinora) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category Name (e.g. Food & Dining, Travel)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text("Monthly Limit (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limit = limitText.toDoubleOrNull() ?: 0.0
                    if (category.isNotBlank() && limit > 0) {
                        onSave(category, limit)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentFinora)
            ) {
                Text("Set Budget", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = Color.White
    )
}

// ----------------------------------------------------
// 5. SUBSCRIPTION MANAGER SCREEN
// ----------------------------------------------------
@Composable
fun SubscriptionManagerScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = rememberCurrencyFormat()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val totalMonthly = subscriptions.sumOf { it.monthlyCost }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("subscription_manager_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundFinora,
        topBar = { FinoraHeaderBar(title = "Subscriptions", subtitle = "${subscriptions.size} Active Recurring Services", onBackClick = onBackClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentFinora,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subscription")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryFinora)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Total Recurring Subscription Spend", fontSize = 12.sp, color = SecondaryFinora)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${currencyFormat.format(totalMonthly)} / mo", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Yearly projection: ${currencyFormat.format(totalMonthly * 12)}", fontSize = 12.sp, color = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (subscriptions.isEmpty()) {
                FinoraEmptyState(
                    title = "No Subscriptions Active",
                    message = "Track Netflix, Spotify, Prime, Gym, and SaaS recurring charges.",
                    buttonText = "Add Subscription",
                    onButtonClick = { showAddDialog = true },
                    icon = Icons.Default.Subscriptions
                )
            } else {
                subscriptions.forEach { sub ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .border(1.dp, CardBorderFinora, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(sub.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                                Text("Renews on day ${sub.billingDateDay} of month", fontSize = 11.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${currencyFormat.format(sub.monthlyCost)}/mo", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(onClick = { viewModel.deleteSubscription(sub.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Cancel Subscription", tint = Color.Gray, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddSubscriptionDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, cost, category, day ->
                    viewModel.addSubscription(name, cost, category, day)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddSubscriptionDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Entertainment") }
    var dayText by remember { mutableStateOf("15") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Subscription Service", fontWeight = FontWeight.Bold, color = PrimaryFinora) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Service Name (e.g. Netflix)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it },
                    label = { Text("Monthly Cost (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = dayText,
                    onValueChange = { dayText = it },
                    label = { Text("Renewal Day of Month (1-31)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = costText.toDoubleOrNull() ?: 0.0
                    val day = dayText.toIntOrNull() ?: 15
                    if (name.isNotBlank() && cost > 0) {
                        onSave(name, cost, category, day)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentFinora)
            ) {
                Text("Add Service", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = Color.White
    )
}

// ----------------------------------------------------
// 6. BILLS REMINDER SCREEN
// ----------------------------------------------------
@Composable
fun BillsReminderScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = rememberCurrencyFormat()
    val bills by viewModel.bills.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("bills_reminder_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundFinora,
        topBar = { FinoraHeaderBar(title = "Bills & Reminders", subtitle = "Upcoming Obligations", onBackClick = onBackClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentFinora,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Bill")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (bills.isEmpty()) {
                FinoraEmptyState(
                    title = "No Pending Bills",
                    message = "Set reminders for Electricity, Wi-Fi, Water, Rent, and Insurance.",
                    buttonText = "Add Bill Reminder",
                    onButtonClick = { showAddDialog = true },
                    icon = Icons.Default.ReceiptLong
                )
            } else {
                bills.forEach { bill ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .border(1.dp, CardBorderFinora, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(bill.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                                Text(bill.provider, fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Amount: ${currencyFormat.format(bill.amount)}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryFinora)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { viewModel.toggleBillPaid(bill.id, bill.isPaid) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (bill.isPaid) SuccessFinora else AccentFinora),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (bill.isPaid) "PAID" else "PAY NOW", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = { viewModel.deleteBill(bill.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Bill", tint = Color.Gray, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddBillDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, provider, amount, category, dueDays ->
                    viewModel.addBill(title, provider, amount, category, dueDays)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddBillDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Double, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var dueDaysText by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bill Reminder", fontWeight = FontWeight.Bold, color = PrimaryFinora) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Bill Name (e.g. Electricity Bill)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = provider,
                    onValueChange = { provider = it },
                    label = { Text("Provider / Company (e.g. Tata Power)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
                OutlinedTextField(
                    value = dueDaysText,
                    onValueChange = { dueDaysText = it },
                    label = { Text("Due in Days from now") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    val days = dueDaysText.toIntOrNull() ?: 5
                    if (title.isNotBlank() && amt > 0) {
                        onSave(title, provider, amt, "Utilities", days)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentFinora)
            ) {
                Text("Add Bill", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = Color.White
    )
}

// ----------------------------------------------------
// 7. FINANCIAL CALCULATORS SCREEN
// ----------------------------------------------------
@Composable
fun FinancialCalculatorsScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = rememberCurrencyFormat()

    // Calculator 1: Emergency Reserve
    var monthlyExpensesText by remember { mutableStateOf("28400") }
    var coverageMonthsText by remember { mutableStateOf("6") }
    var currentReserveText by remember { mutableStateOf("100000") }

    val monthlyExp = monthlyExpensesText.toDoubleOrNull() ?: 0.0
    val monthsNeeded = coverageMonthsText.toIntOrNull() ?: 6
    val currentRes = currentReserveText.toDoubleOrNull() ?: 0.0
    val targetEmergency = monthlyExp * monthsNeeded
    val gapEmergency = (targetEmergency - currentRes).coerceAtLeast(0.0)

    // Calculator 2: SIP Wealth Accumulator
    var sipMonthlyText by remember { mutableStateOf("5000") }
    var yearsText by remember { mutableStateOf("10") }
    var returnRateText by remember { mutableStateOf("12") }

    val sip = sipMonthlyText.toDoubleOrNull() ?: 0.0
    val years = yearsText.toIntOrNull() ?: 10
    val returnRate = returnRateText.toDoubleOrNull() ?: 12.0

    val totalMonths = years * 12
    val monthlyRate = (returnRate / 100.0) / 12.0
    val totalInvestedSip = sip * totalMonths
    val estimatedWealth = if (monthlyRate > 0) {
        sip * ((1 + monthlyRate).pow(totalMonths) - 1) / monthlyRate * (1 + monthlyRate)
    } else totalInvestedSip
    val wealthGain = (estimatedWealth - totalInvestedSip).coerceAtLeast(0.0)

    var savedMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("calculators_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundFinora,
        topBar = { FinoraHeaderBar(title = "Financial Calculators", subtitle = "Emergency Reserve & SIP Wealth Planner", onBackClick = onBackClick) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (savedMessage != null) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Text(savedMessage!!, modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }

            // Emergency Fund Calculator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderFinora, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = AccentFinora)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Emergency Fund Calculator", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = monthlyExpensesText,
                        onValueChange = { monthlyExpensesText = it },
                        label = { Text("Monthly Living Expenses (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = finoraTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = coverageMonthsText,
                            onValueChange = { coverageMonthsText = it },
                            label = { Text("Target Months") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = finoraTextFieldColors()
                        )
                        OutlinedTextField(
                            value = currentReserveText,
                            onValueChange = { currentReserveText = it },
                            label = { Text("Current Savings (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = finoraTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(color = BackgroundFinora, shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                            Text("Target Required: ${currencyFormat.format(targetEmergency)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                            Text("Shortfall Gap: ${currencyFormat.format(gapEmergency)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (gapEmergency > 0) AccentFinora else SuccessFinora)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.addGoal("Emergency Reserve Fund", GoalCategory.EMERGENCY_FUND, targetEmergency)
                            savedMessage = "Created new Emergency Reserve Goal of ${currencyFormat.format(targetEmergency)} in Room Database!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentFinora),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save as Wealth Goal", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SIP Calculator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderFinora, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = SuccessFinora)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SIP Wealth Accumulator", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = sipMonthlyText,
                        onValueChange = { sipMonthlyText = it },
                        label = { Text("Monthly SIP Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = finoraTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = yearsText,
                            onValueChange = { yearsText = it },
                            label = { Text("Years") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = finoraTextFieldColors()
                        )
                        OutlinedTextField(
                            value = returnRateText,
                            onValueChange = { returnRateText = it },
                            label = { Text("Return Rate %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = finoraTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(color = BackgroundFinora, shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                            Text("Total Invested: ${currencyFormat.format(totalInvestedSip)}", fontSize = 12.sp, color = Color.Gray)
                            Text("Est. Future Wealth: ${currencyFormat.format(estimatedWealth)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = SuccessFinora)
                            Text("Estimated Gain: +${currencyFormat.format(wealthGain)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.addInvestment("Monthly SIP Mutual Fund", InvestmentType.MUTUAL_FUND, totalInvestedSip, estimatedWealth, returnRate, "Moderate")
                            savedMessage = "Added SIP holding of ${currencyFormat.format(estimatedWealth)} to Investment Portfolio!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryFinora),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add to Investment Portfolio", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
