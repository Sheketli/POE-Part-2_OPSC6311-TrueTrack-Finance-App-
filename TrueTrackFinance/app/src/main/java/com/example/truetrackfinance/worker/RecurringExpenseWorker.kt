package com.example.truetrackfinance.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.truetrackfinance.data.db.dao.ExpenseDao
import com.example.truetrackfinance.data.db.entity.Expense
import com.example.truetrackfinance.util.DateUtil
import com.example.truetrackfinance.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*

private const val TAG = "RecurringWorker"

/**
 * RecurringExpenseWorker periodically audits scheduled transactions.
 * Implements the automation engine for repeating expenses and income.
 */
@HiltWorker
class RecurringExpenseWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val expenseDao: ExpenseDao,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Worker: Starting background recurring transaction audit")
        
        val now = System.currentTimeMillis()
        val in24Hours = now + 86_400_000L

        try {
            // Requirement: Prototype assumes User ID 1 for testing background jobs
            // In a production app, we would iterate through all users with active recurring series.
            
            // 1. Process Due Transactions (Auto-logging)
            val dueExpenses = expenseDao.getRecurringExpensesDueBefore(userId = 1L, before = now)
            Log.d(TAG, "Audit: Found ${dueExpenses.size} transactions due for logging")
            dueExpenses.forEach { expense ->
                autoLogExpense(expense)
            }

            // 2. Requirement: Pre-log Notification 24 hours before each auto-log.
            val upcomingExpenses = expenseDao.getRecurringExpensesDueBefore(userId = 1L, before = in24Hours)
            upcomingExpenses.filter { it.nextRecurrenceDate != null && it.nextRecurrenceDate!! > now }.forEach { expense ->
                Log.i(TAG, "Security: Sending 24h pre-log reminder for series '${expense.description}'")
                notificationHelper.sendRecurringReminder(expense)
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Critical Fault: Recurring audit failed", e)
            return Result.retry()
        }
    }

    /**
     * Requirement: Auto-log this expense.
     * Generates a new transaction entry and schedules the next recurrence.
     */
    private suspend fun autoLogExpense(series: Expense) {
        Log.i(TAG, "Action: Auto-logging recurring expense '${series.description}' (R${series.amount})")
        
        // 1. Clone the series into a static historical transaction
        val newLog = series.copy(
            id = 0, // Reset ID for new Room entry
            date = series.nextRecurrenceDate ?: System.currentTimeMillis(),
            isRecurring = false, // The log is a point-in-time entry
            nextRecurrenceDate = null
        )
        expenseDao.insertExpense(newLog)

        // 2. Advance the series to its next scheduled date based on frequency
        val nextDate = DateUtil.calculateNextDate(series.nextRecurrenceDate ?: System.currentTimeMillis(), series.recurringFrequency)
        expenseDao.updateExpense(series.copy(nextRecurrenceDate = nextDate))
        
        Log.v(TAG, "Logic: Series advanced to ${java.util.Date(nextDate)}")
    }
}
