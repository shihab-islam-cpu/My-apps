package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (category: String, title: String, amount: Double, date: String, notes: String) -> Unit
) {
    val categories = listOf("Hosting", "Domain", "Software", "API", "Ads", "Hardware", "অন্যান্য")
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    var selectedCategory by remember { mutableStateOf("Hosting") }
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var expenseDate by remember { mutableStateOf(today) }
    var notes by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("রেকর্ড ব্যয় (Add Expense) 📉", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Category (ক্যাটাগরি):", fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    categories.take(4).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("chip_exp_$cat")
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    categories.drop(4).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("chip_exp_$cat")
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title (বিবরণ)") },
                    placeholder = { Text("e.g. Vercel Pro Server / Domain Renewal") },
                    singleLine = true,
                    isError = showError && title.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_expense_title")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ৳ / $ (টাকার পরিমাণ)") },
                    placeholder = { Text("e.g. 2500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = showError && amountText.toDoubleOrNull() == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_expense_amount")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = expenseDate,
                    onValueChange = { expenseDate = it },
                    label = { Text("Date (তারিখ)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_expense_date")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (নোট / রসিদ রেফারেন্স)") },
                    placeholder = { Text("e.g. Annual renewal, client project expense") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_expense_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (title.isNotBlank() && amount != null && amount > 0) {
                        onConfirm(selectedCategory, title.trim(), amount, expenseDate.trim(), notes.trim())
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.testTag("btn_save_expense")
            ) {
                Text("সেভ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}
