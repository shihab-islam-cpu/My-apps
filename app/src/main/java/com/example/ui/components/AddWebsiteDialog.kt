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
import com.example.data.model.WebsiteProject

@Composable
fun AddWebsiteDialog(
    initialProject: WebsiteProject? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, clientName: String, url: String, techStack: String, status: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf(initialProject?.title ?: "") }
    var clientName by remember { mutableStateOf(initialProject?.clientName ?: "") }
    var url by remember { mutableStateOf(initialProject?.liveUrl ?: "") }
    var techStack by remember { mutableStateOf(initialProject?.techStack ?: "Next.js, Tailwind, TypeScript") }
    var status by remember { mutableStateOf(initialProject?.status ?: "Live") }
    var description by remember { mutableStateOf(initialProject?.description ?: "") }
    var titleError by remember { mutableStateOf(false) }
    var urlError by remember { mutableStateOf(false) }

    val statusOptions = listOf("Live", "In Development", "Under Review", "Completed")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialProject != null) "Edit Website Project 🌐" else "Add New Website 🌐",
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
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Website Title *") },
                    placeholder = { Text("e.g. Apex Corporate Portal") },
                    isError = titleError,
                    supportingText = { if (titleError) Text("Title is required") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_website_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        if (it.isNotBlank()) urlError = false
                    },
                    label = { Text("Live URL / Link *") },
                    placeholder = { Text("https://mywebsite.com") },
                    isError = urlError,
                    supportingText = { if (urlError) Text("URL is required") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_website_url")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Client Name") },
                    placeholder = { Text("e.g. Acme Corp / Mr. Karim") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_website_client")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = techStack,
                    onValueChange = { techStack = it },
                    label = { Text("Technologies Used") },
                    placeholder = { Text("Next.js, React, Tailwind, WordPress...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_website_tech")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Project Status",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusOptions.forEach { opt ->
                        FilterChip(
                            selected = status == opt,
                            onClick = { status = opt },
                            label = { Text(opt) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Project Details / Highlights") },
                    placeholder = { Text("Key features built, delivery notes...") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_website_desc")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                        return@Button
                    }
                    if (url.isBlank()) {
                        urlError = true
                        return@Button
                    }
                    val fullUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        "https://$url"
                    } else url

                    onSave(title.trim(), clientName.trim(), fullUrl.trim(), techStack.trim(), status, description.trim())
                },
                modifier = Modifier.testTag("btn_save_website")
            ) {
                Text("Save Website")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_website")
            ) {
                Text("Cancel")
            }
        }
    )
}
