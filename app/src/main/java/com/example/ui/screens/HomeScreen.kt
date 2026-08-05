package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType
import com.example.data.repository.FinancialHealthMetrics
import com.example.ui.CurrentScreen
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    healthMetrics: FinancialHealthMetrics,
    transactions: List<TransactionEntity>,
    userName: String,
    onNavigate: (CurrentScreen) -> Unit,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = rememberCurrencyFormat()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundFinora)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("home_screen")
    ) {
        // 1. Header & Greeting Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PrimaryFinora,
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Good Morning,",
                            fontSize = 14.sp,
                            color = SecondaryFinora
                        )
                        Text(
                            text = userName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { viewModel.toggleGlobalSearch(true) },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .testTag("home_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { onNavigate(CurrentScreen.BillsReminder) },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .testTag("home_notifications_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Bills",
                                tint = AccentFinora
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Financial Health Card inside Header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp))
                        .clickable { onNavigate(CurrentScreen.FinancialInsights) }
                        .testTag("health_score_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = SuccessFinora.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = healthMetrics.scoreGrade.uppercase(),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessFinora
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Today's Financial Health",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryFinora
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Net Cash Flow: +${currencyFormat.format(healthMetrics.netCashFlow)}/mo",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        FinoraProgressRing(
                            progressPercent = healthMetrics.score / 100f,
                            scoreText = "${healthMetrics.score}",
                            color = AccentFinora
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Quick Actions Grid
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Quick Actions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryFinora
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    QuickActionButton(
                        title = "Add Expense",
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        color = Color(0xFFE53935),
                        onClick = { viewModel.toggleAddTransactionSheet(true) }
                    )
                }
                item {
                    QuickActionButton(
                        title = "Add Income",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        color = Color(0xFF4CAF50),
                        onClick = { viewModel.toggleAddTransactionSheet(true) }
                    )
                }
                item {
                    QuickActionButton(
                        title = "Ask AI",
                        icon = Icons.Default.AutoAwesome,
                        color = AccentFinora,
                        onClick = { onSelectTab(2) } // AI Advisor tab
                    )
                }
                item {
                    QuickActionButton(
                        title = "Insights",
                        icon = Icons.Default.Analytics,
                        color = PrimaryFinora,
                        onClick = { onNavigate(CurrentScreen.FinancialInsights) }
                    )
                }
                item {
                    QuickActionButton(
                        title = "Investments",
                        icon = Icons.Default.ShowChart,
                        color = SecondaryFinora,
                        onClick = { onNavigate(CurrentScreen.InvestmentPlanner) }
                    )
                }
                item {
                    QuickActionButton(
                        title = "Debts",
                        icon = Icons.Default.CreditCard,
                        color = PrimaryFinora,
                        onClick = { onNavigate(CurrentScreen.DebtManager) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Monthly Summary Cards
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Summary",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryFinora
                )
                TextButton(onClick = { onNavigate(CurrentScreen.FinancialInsights) }) {
                    Text(text = "See Details", color = AccentFinora, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FinoraMetricCard(
                    title = "Monthly Income",
                    amount = currencyFormat.format(healthMetrics.totalIncome),
                    subtitle = "+12% vs last month",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    iconBgColor = Color(0xFFE8F5E9),
                    iconTint = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )

                FinoraMetricCard(
                    title = "Monthly Expenses",
                    amount = currencyFormat.format(healthMetrics.totalExpenses),
                    subtitle = "26% of Income",
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    iconBgColor = Color(0xFFFFEBEE),
                    iconTint = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FinoraMetricCard(
                    title = "Net Savings",
                    amount = currencyFormat.format(healthMetrics.totalSavings),
                    subtitle = "Emergency reserve target",
                    icon = Icons.Default.Savings,
                    iconBgColor = SecondaryFinora.copy(alpha = 0.2f),
                    iconTint = PrimaryFinora,
                    modifier = Modifier.weight(1f)
                )

                FinoraMetricCard(
                    title = "Investments",
                    amount = currencyFormat.format(healthMetrics.totalInvestments),
                    subtitle = "+11.4% Return",
                    icon = Icons.Default.PieChart,
                    iconBgColor = Color(0xFFE3F2FD),
                    iconTint = Color(0xFF1565C0),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Recent AI Suggestion Banner
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp))
                    .clickable { onSelectTab(2) }
                    .testTag("recent_ai_suggestion_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryFinora)
            ) {
                Row(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AccentFinora),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val aiMessages by viewModel.aiMessages.collectAsState()
                        val latestAi = aiMessages.lastOrNull()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Finora AI Assistant",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryFinora
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AccentFinora,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (latestAi != null) "RECOMMENDATION" else "READY",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = latestAi?.suggestion ?: latestAi?.summary ?: "Tap to get personalized AI advice or ask questions about your budget.",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Recent Transactions Section
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryFinora
                )
                TextButton(onClick = { onSelectTab(1) }) { // Go to Transactions Tab
                    Text(text = "View All", color = AccentFinora, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (transactions.isEmpty()) {
                FinoraEmptyState(
                    title = "No Recent Transactions",
                    message = "Your income and expenses will appear here. Tap + Add to record a transaction.",
                    buttonText = "+ Add Transaction",
                    onButtonClick = { viewModel.toggleAddTransactionSheet(true) },
                    icon = Icons.Default.ReceiptLong
                )
            } else {
                transactions.take(4).forEach { tx ->
                    TransactionItemRow(
                        transaction = tx,
                        currencyFormat = currencyFormat,
                        onClick = {}
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(100.dp)
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(18.dp))
            .border(1.dp, CardBorderFinora, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryFinora,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TransactionItemRow(
    transaction: TransactionEntity,
    currencyFormat: NumberFormat,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val amountPrefix = if (isIncome) "+" else "-"
    val amountColor = if (isIncome) Color(0xFF2E7D32) else PrimaryFinora

    val icon = when (transaction.type) {
        TransactionType.INCOME -> Icons.AutoMirrored.Filled.TrendingUp
        TransactionType.EXPENSE -> Icons.AutoMirrored.Filled.TrendingDown
        TransactionType.INVESTMENT -> Icons.Default.PieChart
        TransactionType.LOAN -> Icons.Default.CreditCard
        TransactionType.TRANSFER -> Icons.Default.SwapHoriz
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderFinora, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isIncome) Color(0xFFE8F5E9) else SecondaryFinora.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = transaction.title,
                        tint = if (isIncome) Color(0xFF2E7D32) else PrimaryFinora,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryFinora,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FinoraCategoryBadge(category = transaction.category)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = transaction.paymentMethod,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$amountPrefix${currencyFormat.format(transaction.amount)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = amountColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = SuccessFinora
                    )
                }

                if (onDelete != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Transaction",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun rememberCurrencyFormat(): NumberFormat {
    return remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 2
        }
    }
}
