package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun AddIncomeDialog(
    onDismiss: () -> Unit,
    onConfirm: (client: String, project: String, amount: Double, status: String, date: String, notes: String) -> Unit
) {
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var clientName by remember { mutableStateOf("") }
    var projectName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var paymentStatus by remember { mutableStateOf("Paid") } // Paid, Due, Partial
    var paymentDate by remember { mutableStateOf(today) }
    var notes by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("রেকর্ড আয় (Add Income) 💰", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Client Name (ক্লায়েন্টের নাম)") },
                    placeholder = { Text("e.g. Ahmad Enterprises") },
                    singleLine = true,
                    isError = showError && clientName.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_income_client")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Project (প্রজেক্টের নাম)") },
                    placeholder = { Text("e.g. Agency Website") },
                    singleLine = true,
                    isError = showError && projectName.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_income_project")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ৳ / $ (টাকার পরিমাণ)") },
                    placeholder = { Text("e.g. 35000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = showError && amountText.toDoubleOrNull() == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_income_amount")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Payment Status (পেমেন্ট স্ট্যাটাস):", fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    listOf("Paid", "Due", "Partial").forEach { status ->
                        FilterChip(
                            selected = paymentStatus == status,
                            onClick = { paymentStatus = status },
                            label = { Text(status) },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("chip_status_$status")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = paymentDate,
                    onValueChange = { paymentDate = it },
                    label = { Text("Payment Date (তারিখ)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_income_date")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (নোট / পেমেন্ট মাধ্যম)") },
                    placeholder = { Text("e.g. Bank transfer, bKash, milestone 2") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_income_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (clientName.isNotBlank() && projectName.isNotBlank() && amount != null && amount > 0) {
                        onConfirm(clientName.trim(), projectName.trim(), amount, paymentStatus, paymentDate.trim(), notes.trim())
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.testTag("btn_save_income")
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
