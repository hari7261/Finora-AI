package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME, EXPENSE, INVESTMENT, LOAN, TRANSFER
}

enum class GoalCategory {
    EMERGENCY_FUND, VEHICLE, HOUSING, VACATION, TECH, EDUCATION, OTHER
}

enum class InvestmentType {
    MUTUAL_FUND, STOCK, GOLD, CRYPTO, FIXED_DEPOSIT, OTHER
}

enum class LoanType {
    PERSONAL, CAR, HOME, EDUCATION, OTHER
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val merchant: String = "",
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val dateEpochMillis: Long = System.currentTimeMillis(),
    val paymentMethod: String = "Credit Card",
    val notes: String = "",
    val status: String = "Completed"
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: GoalCategory,
    val targetAmount: Double,
    val savedAmount: Double,
    val targetDateEpochMillis: Long = System.currentTimeMillis() + 180L * 24 * 60 * 60 * 1000,
    val isCompleted: Boolean = false
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val limitAmount: Double,
    val monthYear: String // e.g. "2026-08"
)

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: LoanType,
    val totalAmount: Double,
    val outstandingAmount: Double,
    val interestRatePercent: Double,
    val emiAmount: Double,
    val remainingMonths: Int,
    val aiPriority: String = "High"
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val monthlyCost: Double,
    val category: String,
    val billingDateDay: Int = 1,
    val isUsedFrequently: Boolean = true,
    val logoName: String = "default"
)

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val provider: String,
    val amount: Double,
    val dueDateEpochMillis: Long,
    val category: String,
    val isPaid: Boolean = false
)

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: InvestmentType,
    val investedAmount: Double,
    val currentValue: Double,
    val expectedReturnPercent: Double,
    val riskLevel: String = "Moderate"
)

@Entity(tableName = "user_profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Hariom Pandit",
    val occupation: String = "Senior Software Architect",
    val monthlyIncome: Double = 85000.0,
    val currencySymbol: String = "₹",
    val riskProfile: String = "Moderate Aggressive",
    val hasCompletedOnboarding: Boolean = false,
    val isDarkMode: Boolean = false
)

@Entity(tableName = "ai_messages")
data class AiMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prompt: String,
    val summary: String,
    val reason: String,
    val suggestion: String,
    val confidencePercent: Int,
    val actionText: String = "View Plan",
    val isUser: Boolean = false,
    val timestampEpochMillis: Long = System.currentTimeMillis()
)
