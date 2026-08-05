package com.example.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast
import com.example.data.local.AppDatabase
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val body = message.messageBody ?: continue
                val sender = message.originatingAddress ?: "Bank"
                val timestamp = message.timestampMillis

                val parsedTransaction = SmsTransactionParser.parseSmsMessage(body, sender, timestamp)
                if (parsedTransaction != null) {
                    val db = AppDatabase.getDatabase(context.applicationContext)
                    val repository = FinanceRepository(db.financeDao())

                    CoroutineScope(Dispatchers.IO).launch {
                        repository.addTransaction(parsedTransaction)
                    }

                    Toast.makeText(
                        context.applicationContext,
                        "Finora AI Auto-Saved: ${parsedTransaction.title} (₹${parsedTransaction.amount})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
