package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.ui.theme.GoalGold
import com.example.ui.theme.GoalGreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProHabitCompactWidget(
    habits: List<Habit>,
    onToggleHabit: (Habit) -> Unit,
    onAddHabit: () -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = habits.size
    val doneCount = habits.count { it.isCompletedToday() }

    // 7-day mini tracker ending with today
    val weekDots = remember(habits) {
        val list = mutableListOf<Triple<String, Boolean, Boolean>>() // DayLetter, isToday, isCompleted
        val todayStr = Habit.getTodayDateString()
        val dayFormat = SimpleDateFormat("EE", Locale.getDefault())
        val fullFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            val dStr = fullFormat.format(c.time)
            val dayInitial = dayFormat.format(c.time).take(1).uppercase()
            val isToday = dStr == todayStr
            val completed = habits.any { it.isCompletedOnDate(dStr) }
            list.add(Triple(dayInitial, isToday, completed))
        }
        list
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("pro_habit_compact_widget")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Header: Pro title, counter badge, 7-day dots, add button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFF97316), Color(0xFFEA580C))
                                )
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Daily Habits & Streaks",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (doneCount == totalCount && totalCount > 0) {
                            GoalGreen.copy(alpha = 0.15f)
                        } else {
                            GoalGold.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = "$doneCount/$totalCount Done",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = if (doneCount == totalCount && totalCount > 0) GoalGreen else GoalGold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Mini Week Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        weekDots.forEach { (letter, isToday, completed) ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            completed -> GoalGreen
                                            isToday -> Color(0xFFF97316).copy(alpha = 0.35f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .border(
                                        width = if (isToday) 1.dp else 0.dp,
                                        color = if (isToday) Color(0xFFF97316) else Color.Transparent,
                                        shape = CircleShape
                                    )
                            ) {
                                Text(
                                    text = letter,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (completed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onAddHabit,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("btn_add_habit_compact")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Habit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // The 3 options consolidated in one sleek row
            if (habits.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "No habits added yet. Tap + to set daily habits.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    habits.forEach { habit ->
                        val isDone = habit.isCompletedToday()
                        val (iconVector, iconColor) = when (habit.iconType.lowercase()) {
                            "code" -> Pair(Icons.Default.Code, Color(0xFF0284C7))
                            "mail", "inquiry" -> Pair(Icons.Default.Email, Color(0xFFF59E0B))
                            "water" -> Pair(Icons.Default.WaterDrop, Color(0xFF06B6D4))
                            "fitness" -> Pair(Icons.Default.FitnessCenter, Color(0xFF10B981))
                            "book" -> Pair(Icons.Default.MenuBook, Color(0xFF8B5CF6))
                            "meditation" -> Pair(Icons.Default.SelfImprovement, Color(0xFF6366F1))
                            else -> Pair(Icons.Default.LocalFireDepartment, Color(0xFFF97316))
                        }

                        val cardBg by animateColorAsState(
                            targetValue = if (isDone) {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
                            },
                            label = "habitCompactBg"
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = cardBg,
                            border = BorderStroke(
                                1.dp,
                                if (isDone) GoalGreen.copy(alpha = 0.35f)
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .combinedClickable(
                                    onClick = { onToggleHabit(habit) },
                                    onLongClick = { onDeleteHabit(habit) }
                                )
                                .testTag("compact_habit_${habit.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 7.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Top row: Icon + Streak count
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = null,
                                        tint = iconColor,
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocalFireDepartment,
                                            contentDescription = null,
                                            tint = Color(0xFFF97316),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = "${habit.currentStreak}d",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            color = Color(0xFFF97316)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Title (short / ellipsis)
                                Text(
                                    text = when {
                                        habit.title.contains("Coding", ignoreCase = true) -> "Daily Coding"
                                        habit.title.contains("Client", ignoreCase = true) || habit.title.contains("Inquir", ignoreCase = true) -> "Client Inq."
                                        habit.title.contains("Water", ignoreCase = true) -> "2.5L Water"
                                        else -> habit.title
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // 1-Tap Toggle Button
                                Surface(
                                    shape = RoundedCornerShape(7.dp),
                                    color = if (isDone) GoalGreen else Color(0xFFF97316).copy(alpha = 0.15f),
                                    border = BorderStroke(
                                        0.75.dp,
                                        if (isDone) GoalGreen else Color(0xFFF97316).copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(24.dp)
                                        .clickable { onToggleHabit(habit) }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (isDone) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Done",
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Done!",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                ),
                                                color = Color.White
                                            )
                                        } else {
                                            Text(
                                                text = "+1 🔥",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                ),
                                                color = Color(0xFFEA580C)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
