package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.repository.FinanceRepository
import com.example.data.repository.FinancialHealthMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class CurrentScreen {
    object Splash : CurrentScreen()
    object Onboarding : CurrentScreen()
    object Main : CurrentScreen()
    object FinancialInsights : CurrentScreen()
    object InvestmentPlanner : CurrentScreen()
    object DebtManager : CurrentScreen()
    object BudgetPlanner : CurrentScreen()
    object SubscriptionManager : CurrentScreen()
    object BillsReminder : CurrentScreen()
    object FinancialCalculators : CurrentScreen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = FinanceRepository(database.financeDao())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Clear all tables and set up a clean profile without hardcoded sample data
                database.financeDao().clearAllData()
                database.financeDao().insertProfile(
                    ProfileEntity(
                        id = 1,
                        name = "User",
                        occupation = "Member",
                        monthlyIncome = 0.0,
                        currencySymbol = "₹",
                        riskProfile = "Moderate",
                        hasCompletedOnboarding = true,
                        isDarkMode = false
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // UI Navigation State
    private val _currentScreen = MutableStateFlow<CurrentScreen>(CurrentScreen.Main)
    val currentScreen: StateFlow<CurrentScreen> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Home, 1: Transactions, 2: AI Advisor, 3: Goals, 4: Profile
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // UI Data Streams
    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val budgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val debts: StateFlow<List<DebtEntity>> = repository.allDebts
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val subscriptions: StateFlow<List<SubscriptionEntity>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val bills: StateFlow<List<BillEntity>> = repository.allBills
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val investments: StateFlow<List<InvestmentEntity>> = repository.allInvestments
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val profile: StateFlow<ProfileEntity?> = repository.profile
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val aiMessages: StateFlow<List<AiMessageEntity>> = repository.aiMessages
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val healthMetrics: StateFlow<FinancialHealthMetrics> = repository.financialHealthMetrics
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            FinancialHealthMetrics(50, "Good", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0)
        )

    // AI Loading State
    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Bottom Sheet States
    private val _showAddTransactionSheet = MutableStateFlow(false)
    val showAddTransactionSheet: StateFlow<Boolean> = _showAddTransactionSheet.asStateFlow()

    private val _showAddGoalSheet = MutableStateFlow(false)
    val showAddGoalSheet: StateFlow<Boolean> = _showAddGoalSheet.asStateFlow()

    private val _showGlobalSearch = MutableStateFlow(false)
    val showGlobalSearch: StateFlow<Boolean> = _showGlobalSearch.asStateFlow()

    // Navigation triggers
    fun navigateTo(screen: CurrentScreen) {
        _currentScreen.value = screen
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
        if (_currentScreen.value != CurrentScreen.Main) {
            _currentScreen.value = CurrentScreen.Main
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val current = profile.value ?: ProfileEntity()
            repository.updateProfile(current.copy(hasCompletedOnboarding = true))
            _currentScreen.value = CurrentScreen.Main
        }
    }

    fun toggleAddTransactionSheet(show: Boolean) {
        _showAddTransactionSheet.value = show
    }

    fun toggleAddGoalSheet(show: Boolean) {
        _showAddGoalSheet.value = show
    }

    fun toggleGlobalSearch(show: Boolean) {
        _showGlobalSearch.value = show
    }

    // Actions
    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        merchant: String,
        paymentMethod: String,
        notes: String
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                title = title,
                merchant = merchant.ifBlank { "Finora Merchant" },
                amount = amount,
                type = type,
                category = category.ifBlank { "General" },
                paymentMethod = paymentMethod,
                notes = notes
            )
            repository.addTransaction(tx)
            toggleAddTransactionSheet(false)
        }
    }

    fun deleteTransaction(txId: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(txId)
        }
    }

    fun addGoal(title: String, category: GoalCategory, targetAmount: Double) {
        viewModelScope.launch {
            val goal = GoalEntity(
                title = title,
                category = category,
                targetAmount = targetAmount,
                savedAmount = 0.0
            )
            repository.addGoal(goal)
            toggleAddGoalSheet(false)
        }
    }

    fun depositToGoal(goalId: Long, currentSaved: Double, amountToAdd: Double) {
        viewModelScope.launch {
            repository.updateGoalSavedAmount(goalId, currentSaved + amountToAdd)
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
        }
    }

    fun addInvestment(
        name: String,
        type: InvestmentType,
        investedAmount: Double,
        currentValue: Double,
        expectedReturnPercent: Double,
        riskLevel: String
    ) {
        viewModelScope.launch {
            val inv = InvestmentEntity(
                name = name,
                type = type,
                investedAmount = investedAmount,
                currentValue = currentValue,
                expectedReturnPercent = expectedReturnPercent,
                riskLevel = riskLevel
            )
            repository.addInvestment(inv)
        }
    }

    fun deleteInvestment(id: Long) {
        viewModelScope.launch {
            repository.deleteInvestment(id)
        }
    }

    fun addDebt(
        name: String,
        type: LoanType,
        totalAmount: Double,
        outstandingAmount: Double,
        interestRatePercent: Double,
        emiAmount: Double,
        remainingMonths: Int,
        aiPriority: String = "Moderate"
    ) {
        viewModelScope.launch {
            val debt = DebtEntity(
                name = name,
                type = type,
                totalAmount = totalAmount,
                outstandingAmount = outstandingAmount,
                interestRatePercent = interestRatePercent,
                emiAmount = emiAmount,
                remainingMonths = remainingMonths,
                aiPriority = aiPriority
            )
            repository.addDebt(debt)
        }
    }

    fun payEmi(debtId: Long, currentOutstanding: Double, emiAmount: Double) {
        viewModelScope.launch {
            val newOutstanding = (currentOutstanding - emiAmount).coerceAtLeast(0.0)
            repository.updateDebtOutstanding(debtId, newOutstanding)
        }
    }

    fun deleteDebt(id: Long) {
        viewModelScope.launch {
            repository.deleteDebt(id)
        }
    }

    fun addBudget(category: String, limitAmount: Double, monthYear: String = "2026-08") {
        viewModelScope.launch {
            val budget = BudgetEntity(category = category, limitAmount = limitAmount, monthYear = monthYear)
            repository.addBudget(budget)
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            repository.deleteBudget(id)
        }
    }

    fun addSubscription(name: String, monthlyCost: Double, category: String, billingDateDay: Int) {
        viewModelScope.launch {
            val sub = SubscriptionEntity(
                name = name,
                monthlyCost = monthlyCost,
                category = category,
                billingDateDay = billingDateDay,
                isUsedFrequently = true
            )
            repository.addSubscription(sub)
        }
    }

    fun deleteSubscription(id: Long) {
        viewModelScope.launch {
            repository.deleteSubscription(id)
        }
    }

    fun addBill(title: String, provider: String, amount: Double, category: String, dueDaysFromNow: Int = 5) {
        viewModelScope.launch {
            val dueDateMs = System.currentTimeMillis() + (dueDaysFromNow * 24 * 60 * 60 * 1000L)
            val bill = BillEntity(
                title = title,
                provider = provider,
                amount = amount,
                dueDateEpochMillis = dueDateMs,
                category = category,
                isPaid = false
            )
            repository.addBill(bill)
        }
    }

    fun toggleBillPaid(billId: Long, currentPaid: Boolean) {
        viewModelScope.launch {
            repository.toggleBillPaid(billId, !currentPaid)
        }
    }

    fun deleteBill(id: Long) {
        viewModelScope.launch {
            repository.deleteBill(id)
        }
    }

    fun updateUserProfile(name: String, occupation: String, monthlyIncome: Double, riskProfile: String) {
        viewModelScope.launch {
            val current = profile.value ?: ProfileEntity()
            val updated = current.copy(
                name = name,
                occupation = occupation,
                monthlyIncome = monthlyIncome,
                riskProfile = riskProfile,
                currencySymbol = "₹"
            )
            repository.updateProfile(updated)
        }
    }

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            database.financeDao().clearAllData()
            database.financeDao().insertProfile(
                ProfileEntity(
                    id = 1,
                    name = "User",
                    occupation = "Member",
                    monthlyIncome = 0.0,
                    currencySymbol = "₹",
                    riskProfile = "Moderate",
                    hasCompletedOnboarding = true,
                    isDarkMode = false
                )
            )
        }
    }

    fun askAi(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isAiThinking.value = true
            try {
                repository.askAiAdvisor(prompt)
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun processIncomingSmsText(smsText: String, sender: String = "HDFC-BANK") {
        viewModelScope.launch {
            val parsedTx = com.example.data.receiver.SmsTransactionParser.parseSmsMessage(smsText, sender)
            if (parsedTx != null) {
                repository.addTransaction(parsedTx)
            }
        }
    }
}
