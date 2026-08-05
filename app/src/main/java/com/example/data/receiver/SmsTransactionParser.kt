package com.example.data.receiver

import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType
import java.util.regex.Pattern

object SmsTransactionParser {

    private val amountRegex = Pattern.compile(
        "(?:rs|inr|₹|usd|\\$|eur|gbp|amount|debited|credited|spent|paid|vpa|a/c)\\s*[.:-]?\\s*([\\d,]+(?:\\.\\d{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    private val numbersOnlyRegex = Pattern.compile("([\\d,]+\\.\\d{2}|[\\d,]{2,})")

    fun parseSmsMessage(body: String, sender: String = "Bank SMS", timestamp: Long = System.currentTimeMillis()): TransactionEntity? {
        val lower = body.lowercase()

        // Check if message is a financial transaction message
        val isFinancial = lower.contains("debited") || lower.contains("credited") ||
                lower.contains("spent") || lower.contains("paid") || lower.contains("withdrawn") ||
                lower.contains("received") || lower.contains("purchase") || lower.contains("a/c") ||
                lower.contains("bank") || lower.contains("upi") || lower.contains("card") ||
                lower.contains("txn") || lower.contains("transaction")

        if (!isFinancial) return null

        // Extract amount
        var amount = 0.0
        val matcher = amountRegex.matcher(body)
        if (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "")
            amount = amountStr?.toDoubleOrNull() ?: 0.0
        }

        if (amount <= 0.0) {
            val numMatcher = numbersOnlyRegex.matcher(body)
            if (numMatcher.find()) {
                val valStr = numMatcher.group(1)?.replace(",", "")
                amount = valStr?.toDoubleOrNull() ?: 0.0
            }
        }

        if (amount <= 0.0) return null

        // Determine Transaction Type
        val type = when {
            lower.contains("credited") || lower.contains("received") || lower.contains("deposited") || lower.contains("added") || lower.contains("salary") -> TransactionType.INCOME
            lower.contains("invested") || lower.contains("mutual fund") || lower.contains("sip") || lower.contains("stock") -> TransactionType.INVESTMENT
            lower.contains("loan") || lower.contains("emi") -> TransactionType.LOAN
            lower.contains("transfer") || lower.contains("sent to") -> TransactionType.TRANSFER
            else -> TransactionType.EXPENSE
        }

        // Detect Merchant / Store
        val merchant = extractMerchant(body, sender)

        // Categorize
        val category = extractCategory(merchant, body)

        val title = if (type == TransactionType.INCOME) "Income from $merchant" else "Spent at $merchant"

        return TransactionEntity(
            title = title,
            merchant = merchant,
            amount = amount,
            type = type,
            category = category,
            dateEpochMillis = timestamp,
            paymentMethod = if (lower.contains("upi")) "UPI" else if (lower.contains("card")) "Debit/Credit Card" else "Bank Auto-SMS",
            notes = "Auto-parsed from real-time Bank SMS: \"${body.take(60)}...\"",
            status = "Completed"
        )
    }

    private fun extractMerchant(body: String, sender: String): String {
        val lower = body.lowercase()
        return when {
            lower.contains("starbucks") -> "Starbucks"
            lower.contains("amazon") -> "Amazon"
            lower.contains("uber") -> "Uber"
            lower.contains("swiggy") || lower.contains("zomato") -> "Food Delivery"
            lower.contains("walmart") || lower.contains("target") -> "Supermarket"
            lower.contains("netflix") || lower.contains("spotify") -> "Subscriptions"
            lower.contains("shell") || lower.contains("fuel") || lower.contains("petrol") -> "Fuel Station"
            lower.contains("apple") || lower.contains("google") -> "App Store Tech"
            lower.contains("salary") || lower.contains("payroll") -> "Employer Payroll"
            lower.contains("at ") -> {
                val parts = body.split(Regex("(?i)\\bat\\b"))
                if (parts.size > 1) {
                    parts[1].trim().take(20).split(Regex("[.,\\n]")).firstOrNull()?.trim() ?: "Merchant Store"
                } else "Bank Merchant"
            }
            else -> if (sender.isNotBlank() && !sender.contains("123")) sender else "Bank Transaction"
        }
    }

    private fun extractCategory(merchant: String, body: String): String {
        val lower = (merchant + " " + body).lowercase()
        return when {
            lower.contains("starbucks") || lower.contains("food") || lower.contains("swiggy") || lower.contains("zomato") || lower.contains("cafe") -> "Food & Dining"
            lower.contains("amazon") || lower.contains("walmart") || lower.contains("target") || lower.contains("shopping") -> "Shopping"
            lower.contains("uber") || lower.contains("travel") || lower.contains("flight") || lower.contains("fuel") -> "Travel & Fuel"
            lower.contains("netflix") || lower.contains("spotify") -> "Subscriptions"
            lower.contains("salary") || lower.contains("payroll") -> "Salary"
            lower.contains("apple") || lower.contains("gadget") || lower.contains("tech") -> "Gadgets"
            lower.contains("rent") || lower.contains("housing") || lower.contains("electricity") -> "Housing & Utilities"
            else -> "General"
        }
    }
}
