package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.GoalCategory
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType
import com.example.ui.MainViewModel
import com.example.ui.components.FinoraChip
import com.example.ui.components.finoraTextFieldColors
import com.example.ui.components.rememberCurrencyFormat
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food & Dining") }
    var merchant by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Credit Card") }
    var notes by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }

    val categories = listOf("Food & Dining", "Gadgets", "Travel", "Housing", "Salary", "Consulting", "Shopping", "Utilities")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        modifier = Modifier.testTag("add_transaction_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Add New Transaction",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryFinora
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Type Segment
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(TransactionType.EXPENSE, TransactionType.INCOME, TransactionType.INVESTMENT).forEach { type ->
                    val isSelected = selectedType == type
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedType = type },
                        color = if (isSelected) PrimaryFinora else BackgroundFinora,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, CardBorderFinora)
                    ) {
                        Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = type.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else PrimaryFinora
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Transaction Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_tx_title_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryFinora,
                    unfocusedTextColor = PrimaryFinora,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryFinora,
                    unfocusedBorderColor = CardBorderFinora,
                    focusedLabelColor = PrimaryFinora,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = AccentFinora
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_tx_amount_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryFinora,
                    unfocusedTextColor = PrimaryFinora,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryFinora,
                    unfocusedBorderColor = CardBorderFinora,
                    focusedLabelColor = PrimaryFinora,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = AccentFinora
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Merchant
            OutlinedTextField(
                value = merchant,
                onValueChange = { merchant = it },
                label = { Text("Merchant / Store Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryFinora,
                    unfocusedTextColor = PrimaryFinora,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryFinora,
                    unfocusedBorderColor = CardBorderFinora,
                    focusedLabelColor = PrimaryFinora,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = AccentFinora
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amount > 0) {
                        viewModel.addTransaction(
                            title = title,
                            amount = amount,
                            type = selectedType,
                            category = category,
                            merchant = merchant,
                            paymentMethod = paymentMethod,
                            notes = notes
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_transaction_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AccentFinora),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Save Transaction", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        modifier = Modifier.testTag("add_goal_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text("Create Wealth Goal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Goal Title (e.g., Tokyo Trip, New House)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_goal_title_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryFinora,
                    unfocusedTextColor = PrimaryFinora,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryFinora,
                    unfocusedBorderColor = CardBorderFinora,
                    focusedLabelColor = PrimaryFinora,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = AccentFinora
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = targetText,
                onValueChange = { targetText = it },
                label = { Text("Target Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_goal_target_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryFinora,
                    unfocusedTextColor = PrimaryFinora,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryFinora,
                    unfocusedBorderColor = CardBorderFinora,
                    focusedLabelColor = PrimaryFinora,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = AccentFinora
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val target = targetText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && target > 0) {
                        viewModel.addGoal(
                            title = title,
                            category = GoalCategory.VACATION,
                            targetAmount = target
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_goal_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AccentFinora),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Create Goal", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchSheet(
    viewModel: MainViewModel,
    transactions: List<TransactionEntity>,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val currencyFormat = rememberCurrencyFormat()

    val results = remember(query, transactions) {
        if (query.isBlank()) emptyList()
        else transactions.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.merchant.contains(query, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = BackgroundFinora,
        modifier = Modifier.testTag("global_search_sheet")
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text("Global Financial Search", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Type merchant, category, or title...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryFinora) },
                shape = RoundedCornerShape(18.dp),
                colors = finoraTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (results.isEmpty()) {
                Text("Search across transactions, goals, and bills.", fontSize = 13.sp, color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 300.dp)) {
                    items(results) { tx ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tx.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                                    Text(tx.category, fontSize = 11.sp, color = Color.Gray)
                                }
                                Text(currencyFormat.format(tx.amount), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
