package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        GoalEntity::class,
        BudgetEntity::class,
        DebtEntity::class,
        SubscriptionEntity::class,
        BillEntity::class,
        InvestmentEntity::class,
        ProfileEntity::class,
        AiMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finora_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.financeDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: FinanceDao) {
            dao.clearAllData()

            // Default Clean Profile with 0 income and no hardcoded entries
            dao.insertProfile(
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
}
