package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

object NotificationHelper {
    const val CHANNEL_REMINDERS_ID = "goalpulse_reminders_channel"
    const val CHANNEL_SUMMARY_ID = "goalpulse_summary_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Task Reminders Channel
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS_ID,
                "Goal & Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts and timely notifications for scheduled goals and tasks"
                enableVibration(true)
                setShowBadge(true)
            }

            // Daily Work Summary Channel
            val summaryChannel = NotificationChannel(
                CHANNEL_SUMMARY_ID,
                "Daily Work Updates & Summaries",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily briefings, evening reviews, and productivity summaries"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(summaryChannel)
        }
    }

    fun showTaskReminder(
        context: Context,
        taskId: Long,
        title: String,
        description: String,
        priority: String = "HIGH"
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TASK_ID", taskId)
            putExtra("EXTRA_NAV_TAB", 1) // Switch to Goals
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val priorityBadge = if (priority.equals("HIGH", ignoreCase = true)) "🔥 [HIGH PRIORITY]" else "⏰ [REMINDER]"

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("$priorityBadge $title")
            .setContentText(if (description.isNotBlank()) description else "Time to focus on completing this goal!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$priorityBadge $title\n${if (description.isNotBlank()) description else "Time to take action on your scheduled task!"}\n\nStay on track with your daily goal streak.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(taskId.toInt(), notification)
    }

    fun showDailySummary(
        context: Context,
        totalTasks: Int,
        completedTasks: Int,
        pendingTasks: Int,
        urgentTasks: Int,
        quote: String
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAV_TAB", 0) // Dashboard
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val percent = if (totalTasks > 0) (completedTasks * 100 / totalTasks) else 0

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val summaryText = buildString {
            append("📊 Today's Progress: $percent% ($completedTasks of $totalTasks completed)\n")
            if (urgentTasks > 0) {
                append("⚠️ $urgentTasks high-priority task(s) awaiting your attention.\n")
            } else if (pendingTasks > 0) {
                append("🎯 $pendingTasks task(s) remaining for today.\n")
            } else {
                append("🎉 Amazing work! All daily goals completed!\n")
            }
            append("💡 \"$quote\"")
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_SUMMARY_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("GoalPulse Daily Work Update 📋")
            .setContentText("Progress: $percent% | $completedTasks done, $pendingTasks pending")
            .setStyle(NotificationCompat.BigTextStyle().bigText(summaryText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(9999, notification)
    }
}
