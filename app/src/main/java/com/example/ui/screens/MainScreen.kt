package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.ui.CurrentScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.*

data class NavTabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val transactions by viewModel.transactions.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val aiMessages by viewModel.aiMessages.collectAsState()
    val healthMetrics by viewModel.healthMetrics.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()

    val showAddTransactionSheet by viewModel.showAddTransactionSheet.collectAsState()
    val showAddGoalSheet by viewModel.showAddGoalSheet.collectAsState()
    val showGlobalSearch by viewModel.showGlobalSearch.collectAsState()

    val tabs = listOf(
        NavTabItem("Home", Icons.Default.Home, Icons.Default.Home, "tab_home"),
        NavTabItem("Transactions", Icons.Default.ReceiptLong, Icons.Default.ReceiptLong, "tab_transactions"),
        NavTabItem("AI Advisor", Icons.Default.AutoAwesome, Icons.Default.AutoAwesome, "tab_ai_advisor"),
        NavTabItem("Goals", Icons.Default.Flag, Icons.Default.Flag, "tab_goals"),
        NavTabItem("Profile", Icons.Default.Person, Icons.Default.Person, "tab_profile")
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = PrimaryFinora,
        bottomBar = {
            if (currentScreen == CurrentScreen.Main) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .testTag("bottom_navigation_bar"),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        containerColor = Color.White,
                        tonalElevation = 0.dp
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            val isSelected = selectedTab == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.selectTab(index) },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(if (isSelected) 42.dp else 28.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) PrimaryFinora else Color.Transparent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = tab.selectedIcon,
                                            contentDescription = tab.title,
                                            tint = if (isSelected) AccentFinora else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PrimaryFinora else Color.Gray
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                CurrentScreen.Splash -> {
                    SplashScreen(
                        onSplashFinished = {
                            viewModel.navigateTo(CurrentScreen.Main)
                        }
                    )
                }

                CurrentScreen.Onboarding -> {
                    OnboardingScreen(
                        onOnboardingCompleted = { viewModel.completeOnboarding() }
                    )
                }

                CurrentScreen.Main -> {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                        label = "tabContent"
                    ) { tabIndex ->
                        when (tabIndex) {
                            0 -> HomeScreen(
                                viewModel = viewModel,
                                healthMetrics = healthMetrics,
                                transactions = transactions,
                                userName = profile?.name ?: "Hariom Pandit",
                                onNavigate = { viewModel.navigateTo(it) },
                                onSelectTab = { viewModel.selectTab(it) }
                            )
                            1 -> TransactionsScreen(
                                viewModel = viewModel,
                                transactions = transactions
                            )
                            2 -> AiAdvisorScreen(
                                viewModel = viewModel,
                                aiMessages = aiMessages,
                                isThinking = isThinking
                            )
                            3 -> GoalsScreen(
                                viewModel = viewModel,
                                goals = goals
                            )
                            4 -> ProfileScreen(
                                viewModel = viewModel,
                                profile = profile,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }
                    }
                }

                CurrentScreen.FinancialInsights -> {
                    FinancialInsightsScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo(CurrentScreen.Main) }
                    )
                }

                CurrentScreen.InvestmentPlanner -> {
                    InvestmentPlannerScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo(CurrentScreen.Main) }
                    )
                }

                CurrentScreen.DebtManager -> {
                    DebtManagerScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo(CurrentScreen.Main) }
                    )
                }

                CurrentScreen.BudgetPlanner -> {
                    BudgetPlannerScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo(CurrentScreen.Main) }
                    )
                }

                CurrentScreen.SubscriptionManager -> {
                    SubscriptionManagerScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo(CurrentScreen.Main) }
                    )
                }

                CurrentScreen.BillsReminder -> {
                    BillsReminderScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo(CurrentScreen.Main) }
                    )
                }

                CurrentScreen.FinancialCalculators -> {
                    FinancialCalculatorsScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo(CurrentScreen.Main) }
                    )
                }
            }

            // Bottom Sheets
            if (showAddTransactionSheet) {
                AddTransactionBottomSheet(
                    viewModel = viewModel,
                    onDismiss = { viewModel.toggleAddTransactionSheet(false) }
                )
            }

            if (showAddGoalSheet) {
                AddGoalBottomSheet(
                    viewModel = viewModel,
                    onDismiss = { viewModel.toggleAddGoalSheet(false) }
                )
            }

            if (showGlobalSearch) {
                GlobalSearchSheet(
                    viewModel = viewModel,
                    transactions = transactions,
                    onDismiss = { viewModel.toggleGlobalSearch(false) }
                )
            }
        }
    }
}
