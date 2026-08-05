package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TransactionsScreen(
    viewModel: MainViewModel,
    transactions: List<TransactionEntity>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val currencyFormat = rememberCurrencyFormat()

    // Filtering logic
    val filteredTransactions = remember(transactions, searchQuery, selectedFilter) {
        transactions.filter { tx ->
            val matchesSearch = searchQuery.isBlank() ||
                    tx.title.contains(searchQuery, ignoreCase = true) ||
                    tx.merchant.contains(searchQuery, ignoreCase = true) ||
                    tx.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Income" -> tx.type == TransactionType.INCOME
                "Expense" -> tx.type == TransactionType.EXPENSE
                "Investment" -> tx.type == TransactionType.INVESTMENT
                "Loan" -> tx.type == TransactionType.LOAN
                "Transfer" -> tx.type == TransactionType.TRANSFER
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    // Grouping by time
    val groupedTransactions = remember(filteredTransactions) {
        filteredTransactions.groupBy { tx ->
            val daysAgo = (System.currentTimeMillis() - tx.dateEpochMillis) / (1000 * 60 * 60 * 24)
            when {
                daysAgo <= 0 -> "Today"
                daysAgo == 1L -> "Yesterday"
                daysAgo in 2..7 -> "This Week"
                else -> "This Month"
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("transactions_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundFinora,
        topBar = {
            FinoraHeaderBar(
                title = "Transactions",
                subtitle = "${transactions.size} Records Persisted"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleAddTransactionSheet(true) },
                containerColor = AccentFinora,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_transaction_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction", modifier = Modifier.size(28.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Real-time SMS Reader Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp))
                    .border(1.dp, CardBorderFinora, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AccentFinora.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Sms, contentDescription = "SMS Auto-Reader", tint = AccentFinora, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Real-Time SMS Auto-Reader", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                            }
                            Text("Auto-captures amount, date & merchant details", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.processIncomingSmsText("ALERT: Your A/C XX8902 has been debited by Rs. 3850.00 at Starbucks Coffee on 05-Aug-2026. Avail Bal: Rs. 124500.00")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryFinora.copy(alpha = 0.2f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Test SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_search_input"),
                placeholder = { Text("Search title, merchant, category...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryFinora) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryFinora,
                    unfocusedTextColor = PrimaryFinora,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryFinora,
                    unfocusedBorderColor = CardBorderFinora,
                    cursorColor = AccentFinora
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips Row
            val filters = listOf("All", "Income", "Expense", "Investment", "Loan", "Transfer")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filters) { filter ->
                    FinoraChip(
                        text = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of Grouped Transactions
            if (filteredTransactions.isEmpty()) {
                FinoraEmptyState(
                    title = "No Transactions Found",
                    message = "Try searching for a different keyword or adding a new transaction.",
                    buttonText = "Add Transaction",
                    onButtonClick = { viewModel.toggleAddTransactionSheet(true) },
                    icon = Icons.Default.ReceiptLong
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedTransactions.forEach { (groupTitle, txList) ->
                        item {
                            Column {
                                Text(
                                    text = groupTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryFinora,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    for (tx in txList) {
                                        TransactionItemRow(
                                            transaction = tx,
                                            currencyFormat = currencyFormat,
                                            onClick = {},
                                            onDelete = { viewModel.deleteTransaction(tx.id) }
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
