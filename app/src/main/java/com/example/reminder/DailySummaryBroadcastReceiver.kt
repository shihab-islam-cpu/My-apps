package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.model.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailySummaryBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val allTasks = db.goalTaskDao().getAllTasksSync()
                val completed = allTasks.count { it.isCompleted }
                val pending = allTasks.count { !it.isCompleted }
                val urgent = allTasks.count { !it.isCompleted && it.priority == Priority.HIGH }
                val total = allTasks.size

                val quotes = listOf(
                    "Small disciplined steps daily lead to massive annual achievements.",
                    "Focus on being productive instead of just busy.",
                    "The secret of getting ahead is getting started.",
                    "Your future is created by what you do today, not tomorrow.",
                    "Consistency over intensity. Keep your streak alive!"
                )
                val randomQuote = quotes.random()

                NotificationHelper.showDailySummary(
                    context = context,
                    totalTasks = total,
                    completedTasks = completed,
                    pendingTasks = pending,
                    urgentTasks = urgent,
                    quote = randomQuote
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}
