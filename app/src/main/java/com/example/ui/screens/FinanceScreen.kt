package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseRecord
import com.example.data.model.IncomeRecord
import com.example.ui.components.AddExpenseDialog
import com.example.ui.components.AddIncomeDialog
import com.example.ui.theme.GoalGold
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.GoalRose
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FinanceScreen(
    incomes: List<IncomeRecord>,
    expenses: List<ExpenseRecord>,
    onAddIncome: (String, String, Double, String, String, String) -> Unit,
    onDeleteIncome: (IncomeRecord) -> Unit,
    onAddExpense: (String, String, Double, String, String) -> Unit,
    onDeleteExpense: (ExpenseRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Income, 1: Expenses
    var selectedExpenseFilter by remember { mutableStateOf("All") }
    var showAddIncomeDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    val totalEarned = incomes.filter { it.paymentStatus.equals("Paid", ignoreCase = true) }.sumOf { it.amount }
    val totalDue = incomes.filter { !it.paymentStatus.equals("Paid", ignoreCase = true) }.sumOf { it.amount }
    val totalExpenses = expenses.sumOf { it.amount }
    val netProfit = totalEarned - totalExpenses

    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    val expenseCategories = listOf("All", "Hosting", "Domain", "Software", "API", "Ads", "Hardware", "অন্যান্য")

    val filteredExpenses = if (selectedExpenseFilter == "All") {
        expenses
    } else {
        expenses.filter { it.category.equals(selectedExpenseFilter, ignoreCase = true) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddIncomeDialog = true else showAddExpenseDialog = true
                },
                shape = CircleShape,
                containerColor = if (selectedTab == 0) GoalGreen else GoalRose,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("fab_add_finance")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (selectedTab == 0) "Add Income" else "Add Expense"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Screen Header
            Text(
                text = "Finance & Earnings 💵",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Track your client project incomes, paid/due amounts, and infra expenses",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics Summary Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("card_finance_summary")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Net Developer Profit (নীট লাভ)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "৳ ${formatter.format(netProfit)}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (netProfit >= 0) GoalGreen else GoalRose
                                )
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = (if (netProfit >= 0) GoalGreen else GoalRose).copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (netProfit >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (netProfit >= 0) GoalGreen else GoalRose
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Earned (আদায়)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("৳ ${formatter.format(totalEarned)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GoalGreen))
                        }
                        Column {
                            Text("Due / Pending (বকেয়া)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("৳ ${formatter.format(totalDue)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GoalGold))
                        }
                        Column {
                            Text("Expenses (ব্যয়)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("৳ ${formatter.format(totalExpenses)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GoalRose))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.testTag("tabrow_finance")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Client Incomes (${incomes.size})", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Infra Expenses (${expenses.size})", fontWeight = FontWeight.SemiBold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Incomes list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (incomes.isEmpty()) {
                        item {
                            EmptyFinanceState(message = "No income records found. Tap '+' to log a client payment.")
                        }
                    } else {
                        items(incomes, key = { it.id }) { income ->
                            IncomeItemCard(income = income, onDelete = { onDeleteIncome(income) })
                        }
                    }
                }
            } else {
                // Expenses list with categories filter
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(expenseCategories) { cat ->
                        FilterChip(
                            selected = selectedExpenseFilter == cat,
                            onClick = { selectedExpenseFilter = cat },
                            label = { Text(cat) },
                            modifier = Modifier.testTag("filter_expense_$cat")
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filteredExpenses.isEmpty()) {
                        item {
                            EmptyFinanceState(message = "No expense records found in this category. Tap '+' to add.")
                        }
                    } else {
                        items(filteredExpenses, key = { it.id }) { expense ->
                            ExpenseItemCard(expense = expense, onDelete = { onDeleteExpense(expense) })
                        }
                    }
                }
            }
        }
    }

    if (showAddIncomeDialog) {
        AddIncomeDialog(
            onDismiss = { showAddIncomeDialog = false },
            onConfirm = { client, project, amount, status, date, notes ->
                onAddIncome(client, project, amount, status, date, notes)
                showAddIncomeDialog = false
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onConfirm = { category, title, amount, date, notes ->
                onAddExpense(category, title, amount, date, notes)
                showAddExpenseDialog = false
            }
        )
    }
}

@Composable
fun IncomeItemCard(income: IncomeRecord, onDelete: () -> Unit) {
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }
    val isPaid = income.paymentStatus.equals("Paid", ignoreCase = true)
    val isDue = income.paymentStatus.equals("Due", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("income_card_${income.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = income.clientName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = income.projectName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${income.currency} ${formatter.format(income.amount)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isPaid) GoalGreen else GoalGold
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            isPaid -> GoalGreen.copy(alpha = 0.15f)
                            isDue -> GoalRose.copy(alpha = 0.15f)
                            else -> GoalGold.copy(alpha = 0.15f)
                        },
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = income.paymentStatus,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when {
                                isPaid -> GoalGreen
                                isDue -> GoalRose
                                else -> GoalGold
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payment Date: ${income.paymentDate}${if (income.notes.isNotBlank()) " • ${income.notes}" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Income",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseItemCard(expense: ExpenseRecord, onDelete: () -> Unit) {
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }
    val icon = when (expense.category.lowercase()) {
        "hosting" -> Icons.Default.CloudQueue
        "domain" -> Icons.Default.Domain
        "software" -> Icons.Default.Computer
        "api" -> Icons.Default.Code
        "ads" -> Icons.Default.Campaign
        "hardware" -> Icons.Default.Computer
        else -> Icons.Default.MoreHoriz
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("expense_card_${expense.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = GoalRose.copy(alpha = 0.12f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = GoalRose, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = expense.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = expense.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${expense.date}${if (expense.notes.isNotBlank()) " • ${expense.notes}" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "- ৳ ${formatter.format(expense.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoalRose
                    )
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp).padding(top = 2.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyFinanceState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
