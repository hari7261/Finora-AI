package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.GoalCategory
import com.example.data.local.GoalEntity
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun GoalsScreen(
    viewModel: MainViewModel,
    goals: List<GoalEntity>,
    modifier: Modifier = Modifier
) {
    val currencyFormat = rememberCurrencyFormat()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("goals_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = BackgroundFinora,
        topBar = {
            FinoraHeaderBar(
                title = "Goal Planner",
                subtitle = "${goals.size} Active Wealth Targets",
                actionIcon = Icons.Default.Add,
                onActionClick = { viewModel.toggleAddGoalSheet(true) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleAddGoalSheet(true) },
                containerColor = AccentFinora,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_goal_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal", modifier = Modifier.size(28.dp))
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

            if (goals.isEmpty()) {
                FinoraEmptyState(
                    title = "No Financial Goals Yet",
                    message = "Set a goal like an Emergency Fund or New House to start tracking your progress.",
                    buttonText = "Create Goal",
                    onButtonClick = { viewModel.toggleAddGoalSheet(true) },
                    icon = Icons.Default.Flag
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(goals) { goal ->
                        GoalCardItem(
                            goal = goal,
                            currencyFormat = currencyFormat,
                            onDelete = { viewModel.deleteGoal(goal.id) },
                            onDeposit = { amount -> viewModel.depositToGoal(goal.id, goal.savedAmount, amount) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalCardItem(
    goal: GoalEntity,
    currencyFormat: java.text.NumberFormat,
    onDelete: () -> Unit,
    onDeposit: (Double) -> Unit
) {
    val progress = (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)
    var showDepositDialog by remember { mutableStateOf(false) }

    val categoryIcon: ImageVector = when (goal.category) {
        GoalCategory.EMERGENCY_FUND -> Icons.Default.Shield
        GoalCategory.VEHICLE -> Icons.Default.DirectionsCar
        GoalCategory.HOUSING -> Icons.Default.Home
        GoalCategory.VACATION -> Icons.Default.FlightTakeoff
        GoalCategory.TECH -> Icons.Default.Laptop
        GoalCategory.EDUCATION -> Icons.Default.School
        else -> Icons.Default.Flag
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(24.dp))
            .border(1.dp, CardBorderFinora, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SecondaryFinora.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = goal.title,
                            tint = PrimaryFinora,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = goal.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryFinora
                        )
                        Text(
                            text = "Target: ${currencyFormat.format(goal.targetAmount)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Goal", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar & Ring stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Saved: ${currencyFormat.format(goal.savedAmount)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryFinora
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentFinora
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = AccentFinora,
                        trackColor = SecondaryFinora.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remaining: ${currencyFormat.format(remaining)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Button(
                            onClick = { showDepositDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryFinora.copy(alpha = 0.2f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryFinora, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Deposit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryFinora)
                        }

                        if (showDepositDialog) {
                            DepositGoalDialog(
                                goalTitle = goal.title,
                                onDismiss = { showDepositDialog = false },
                                onDeposit = { amount ->
                                    onDeposit(amount)
                                    showDepositDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DepositGoalDialog(
    goalTitle: String,
    onDismiss: () -> Unit,
    onDeposit: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Deposit to $goalTitle", fontWeight = FontWeight.Bold, color = PrimaryFinora) },
        text = {
            Column {
                Text("Enter amount to add towards this goal:", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (₹)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = finoraTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onDeposit(amt)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentFinora)
            ) {
                Text("Add Deposit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = Color.White
    )
}
