package com.example.data.repository

import com.example.data.ai.AiAdvisorResponse
import com.example.data.ai.GeminiService
import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar

data class FinancialHealthMetrics(
    val score: Int, // e.g. 82 / 100
    val scoreGrade: String, // "Excellent", "Good", "Needs Attention"
    val totalIncome: Double,
    val totalExpenses: Double,
    val totalSavings: Double,
    val totalInvestments: Double,
    val totalDebts: Double,
    val netCashFlow: Double,
    val emergencyFundProgressPercent: Int
)

class FinanceRepository(private val dao: FinanceDao) {

    // Streams
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val allGoals: Flow<List<GoalEntity>> = dao.getAllGoals()
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()
    val allDebts: Flow<List<DebtEntity>> = dao.getAllDebts()
    val allSubscriptions: Flow<List<SubscriptionEntity>> = dao.getAllSubscriptions()
    val allBills: Flow<List<BillEntity>> = dao.getAllBills()
    val allInvestments: Flow<List<InvestmentEntity>> = dao.getAllInvestments()
    val profile: Flow<ProfileEntity?> = dao.getProfile()
    val aiMessages: Flow<List<AiMessageEntity>> = dao.getAllAiMessages()

    // Calculated Health Score & Summary Flow
    val financialHealthMetrics: Flow<FinancialHealthMetrics> = allTransactions.map { txList ->
        val income = txList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = txList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val investments = txList.filter { it.type == TransactionType.INVESTMENT }.sumOf { it.amount }
        val debtPayments = txList.filter { it.type == TransactionType.LOAN }.sumOf { it.amount }

        val netCashFlow = income - expenses - investments - debtPayments
        val savings = if (netCashFlow > 0) netCashFlow else 0.0

        // Score calculation algorithm
        var score = 50
        if (income > 0) {
            val savingsRate = (savings + investments) / income
            score += (savingsRate * 40).toInt().coerceIn(0, 35)

            val debtRatio = debtPayments / income
            if (debtRatio < 0.20) score += 15 else if (debtRatio < 0.35) score += 10
        }

        score = score.coerceIn(10, 100)
        val grade = when {
            score >= 80 -> "Excellent"
            score >= 65 -> "Good"
            else -> "Needs Attention"
        }

        FinancialHealthMetrics(
            score = score,
            scoreGrade = grade,
            totalIncome = income,
            totalExpenses = expenses,
            totalSavings = savings,
            totalInvestments = investments,
            totalDebts = debtPayments,
            netCashFlow = netCashFlow,
            emergencyFundProgressPercent = 0
        )
    }

    // Mutators
    suspend fun addTransaction(transaction: TransactionEntity) {
        dao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        dao.deleteTransactionById(id)
    }

    suspend fun addGoal(goal: GoalEntity) {
        dao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: GoalEntity) {
        dao.updateGoal(goal)
    }

    suspend fun updateGoalSavedAmount(id: Long, newSavedAmount: Double) {
        dao.updateGoalSavedAmount(id, newSavedAmount)
    }

    suspend fun deleteGoal(id: Long) {
        dao.deleteGoalById(id)
    }

    suspend fun addBudget(budget: BudgetEntity) {
        dao.insertBudget(budget)
    }

    suspend fun deleteBudget(id: Long) {
        dao.deleteBudgetById(id)
    }

    suspend fun addDebt(debt: DebtEntity) {
        dao.insertDebt(debt)
    }

    suspend fun updateDebtOutstanding(id: Long, newOutstanding: Double) {
        dao.updateDebtOutstanding(id, newOutstanding)
    }

    suspend fun deleteDebt(id: Long) {
        dao.deleteDebtById(id)
    }

    suspend fun addSubscription(subscription: SubscriptionEntity) {
        dao.insertSubscription(subscription)
    }

    suspend fun deleteSubscription(id: Long) {
        dao.deleteSubscriptionById(id)
    }

    suspend fun addBill(bill: BillEntity) {
        dao.insertBill(bill)
    }

    suspend fun toggleBillPaid(id: Long, isPaid: Boolean) {
        dao.updateBillPaidStatus(id, isPaid)
    }

    suspend fun deleteBill(id: Long) {
        dao.deleteBillById(id)
    }

    suspend fun addInvestment(investment: InvestmentEntity) {
        dao.insertInvestment(investment)
    }

    suspend fun deleteInvestment(id: Long) {
        dao.deleteInvestmentById(id)
    }

    suspend fun updateProfile(profile: ProfileEntity) {
        dao.insertProfile(profile)
    }

    // AI Query Execution
    suspend fun askAiAdvisor(prompt: String): AiMessageEntity {
        // Build rich context summary from local DB
        val txs = allTransactions.first()
        val currentProfile = profile.first()
        val goals = allGoals.first()
        val budgets = allBudgets.first()
        val debts = allDebts.first()
        val subscriptions = allSubscriptions.first()
        val bills = allBills.first()
        val investments = allInvestments.first()
        val historyMsgs = aiMessages.first()

        val income = txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val investmentSum = txs.filter { it.type == TransactionType.INVESTMENT }.sumOf { it.amount }
        val debtSum = txs.filter { it.type == TransactionType.LOAN }.sumOf { it.amount }

        val contextSummary = """
            User Profile: Name = ${currentProfile?.name ?: "User"}, Profile Monthly Income = ₹${currentProfile?.monthlyIncome ?: 0.0}, Occupation = ${currentProfile?.occupation ?: "Member"}, Risk Profile = ${currentProfile?.riskProfile ?: "Moderate"}
            Recorded Income: ₹$income
            Recorded Expenses: ₹$expenses
            Recorded Investments: ₹$investmentSum
            Recorded Debt Payments: ₹$debtSum
            Transactions (${txs.size} total): ${txs.take(5).joinToString { "${it.title}: ₹${it.amount} (${it.category})" }.ifEmpty { "None" }}
            Active Goals (${goals.size}): ${goals.joinToString { "${it.title}: Saved ₹${it.savedAmount} of Target ₹${it.targetAmount}" }.ifEmpty { "None" }}
            Budgets (${budgets.size}): ${budgets.joinToString { "${it.category}: Limit ₹${it.limitAmount}" }.ifEmpty { "None" }}
            Debts (${debts.size}): ${debts.joinToString { "${it.name}: Outstanding ₹${it.outstandingAmount}, EMI ₹${it.emiAmount}" }.ifEmpty { "None" }}
            Subscriptions (${subscriptions.size}): ${subscriptions.joinToString { "${it.name}: ₹${it.monthlyCost}/mo" }.ifEmpty { "None" }}
            Bills (${bills.size}): ${bills.joinToString { "${it.title}: ₹${it.amount} (Paid: ${it.isPaid})" }.ifEmpty { "None" }}
            Investments (${investments.size}): ${investments.joinToString { "${it.name}: Invested ₹${it.investedAmount}, Current ₹${it.currentValue}" }.ifEmpty { "None" }}
        """.trimIndent()

        // Insert user prompt first
        val userMsg = AiMessageEntity(
            prompt = prompt,
            summary = prompt,
            reason = "",
            suggestion = "",
            confidencePercent = 100,
            isUser = true,
            timestampEpochMillis = System.currentTimeMillis()
        )
        dao.insertAiMessage(userMsg)

        // Query Gemini Service with multi-turn history
        val aiResponse: AiAdvisorResponse = GeminiService.queryFinoraAi(
            userPrompt = prompt,
            financialContextSummary = contextSummary,
            history = historyMsgs
        )

        val aiMsg = AiMessageEntity(
            prompt = prompt,
            summary = aiResponse.summary,
            reason = aiResponse.reason,
            suggestion = aiResponse.suggestion,
            confidencePercent = aiResponse.confidencePercent,
            actionText = aiResponse.actionText,
            isUser = false,
            timestampEpochMillis = System.currentTimeMillis() + 50
        )

        dao.insertAiMessage(aiMsg)
        return aiMsg
    }
}
