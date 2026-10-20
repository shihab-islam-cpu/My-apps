package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
fun AddClientRequestDialog(
    onDismiss: () -> Unit,
    onSave: (clientName: String, clientEmail: String, phone: String, projectType: String, budget: String, message: String) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var projectType by remember { mutableStateOf("E-commerce Store") }
    var budget by remember { mutableStateOf("৳30,000") }
    var message by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var messageError by remember { mutableStateOf(false) }

    val projectTypes = listOf("E-commerce Store", "Agency Website", "Portfolio", "Landing Page", "Web App")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Client Website Request 📩",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = {
                        clientName = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("Client Name *") },
                    placeholder = { Text("e.g. Rafiqul Islam / Apex Ltd") },
                    isError = nameError,
                    supportingText = { if (nameError) Text("Client name is required") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_req_client_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = clientEmail,
                    onValueChange = { clientEmail = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("client@example.com") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_req_client_email")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = clientPhone,
                    onValueChange = { clientPhone = it },
                    label = { Text("Phone / WhatsApp") },
                    placeholder = { Text("+880 1700-000000") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_req_client_phone")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Project Category",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    projectTypes.take(3).forEach { type ->
                        FilterChip(
                            selected = projectType == type,
                            onClick = { projectType = type },
                            label = { Text(type) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    projectTypes.drop(3).forEach { type ->
                        FilterChip(
                            selected = projectType == type,
                            onClick = { projectType = type },
                            label = { Text(type) }
                        )
                    }
                }

                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Client Budget") },
                    placeholder = { Text("e.g. ৳40,000 / $500") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_req_budget")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = {
                        message = it
                        if (it.isNotBlank()) messageError = false
                    },
                    label = { Text("Website Requirements / Message *") },
                    placeholder = { Text("Describe what pages, design style, and features the client requested...") },
                    isError = messageError,
                    supportingText = { if (messageError) Text("Requirements message is required") },
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_req_message")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (clientName.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    if (message.isBlank()) {
                        messageError = true
                        return@Button
                    }
                    onSave(clientName.trim(), clientEmail.trim(), clientPhone.trim(), projectType, budget.trim(), message.trim())
                },
                modifier = Modifier.testTag("btn_save_request")
            ) {
                Text("Save Request")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_request")
            ) {
                Text("Cancel")
            }
        }
    )
}
