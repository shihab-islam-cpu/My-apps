package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("EXTRA_TASK_ID", 0L)
        val title = intent.getStringExtra("EXTRA_TASK_TITLE") ?: "Scheduled Task Reminder"
        val description = intent.getStringExtra("EXTRA_TASK_DESC") ?: ""
        val priority = intent.getStringExtra("EXTRA_TASK_PRIORITY") ?: "HIGH"

        NotificationHelper.showTaskReminder(
            context = context,
            taskId = if (taskId != 0L) taskId else System.currentTimeMillis(),
            title = title,
            description = description,
            priority = priority
        )
    }
}
