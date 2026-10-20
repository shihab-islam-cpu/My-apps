package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp

@Composable
fun AddClientCrmDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        clientName: String,
        contact: String,
        project: String,
        budget: String,
        paymentStatus: String,
        deadline: String,
        notes: String,
        previousProjects: String,
        communicationHistory: String
    ) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var project by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var paymentStatus by remember { mutableStateOf("In Discussion") }
    var deadline by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var previousProjects by remember { mutableStateOf("") }
    var communicationHistory by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("ক্লায়েন্ট CRM যোগ করুন (Client CRM) 👥", fontWeight = FontWeight.Bold)
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
                    placeholder = { Text("e.g. Tanvir Hasan / Sophia Miller") },
                    singleLine = true,
                    isError = showError && clientName.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("crm_input_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact (ইমেইল / ফোন / WhatsApp)") },
                    placeholder = { Text("e.g. client@example.com • +880 1712...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("crm_input_contact")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = project,
                    onValueChange = { project = it },
                    label = { Text("Project (ওয়েবসাইট / প্রজেক্ট)") },
                    placeholder = { Text("e.g. Modern E-commerce or Portfolio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("crm_input_project")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = { Text("Budget (বাজেট)") },
                        placeholder = { Text("৳45,000 / $650") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).padding(end = 4.dp).testTag("crm_input_budget")
                    )
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Deadline (ডেডলাইন)") },
                        placeholder = { Text("2026-11-20") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).padding(start = 4.dp).testTag("crm_input_deadline")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Payment Status:", fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    listOf("In Discussion", "Paid", "Due").forEach { st ->
                        FilterChip(
                            selected = paymentStatus == st,
                            onClick = { paymentStatus = st },
                            label = { Text(st) },
                            modifier = Modifier.padding(end = 4.dp).testTag("crm_chip_$st")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = previousProjects,
                    onValueChange = { previousProjects = it },
                    label = { Text("Previous Projects (পূর্ববর্তী কাজ)") },
                    placeholder = { Text("e.g. Landing page v1 in 2024") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("crm_input_prev")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = communicationHistory,
                    onValueChange = { communicationHistory = it },
                    label = { Text("Communication History (যোগাযোগের ইতিহাস)") },
                    placeholder = { Text("e.g. WhatsApp call on Oct 19, quotation accepted.") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("crm_input_comm")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (বিশেষ নোট)") },
                    placeholder = { Text("Client requested dark mode and bKash integration.") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("crm_input_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (clientName.isNotBlank()) {
                        onConfirm(
                            clientName.trim(),
                            contact.trim(),
                            project.trim(),
                            budget.trim(),
                            paymentStatus,
                            deadline.trim(),
                            notes.trim(),
                            previousProjects.trim(),
                            communicationHistory.trim()
                        )
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.testTag("btn_save_crm_client")
            ) {
                Text("যুক্ত করুন")
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
