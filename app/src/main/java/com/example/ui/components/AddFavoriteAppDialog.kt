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

data class AppPreset(
    val name: String,
    val packageName: String,
    val url: String,
    val category: String
)

@Composable
fun AddFavoriteAppDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, packageName: String, url: String, iconCategory: String) -> Unit
) {
    val presets = listOf(
        AppPreset("Chrome", "com.android.chrome", "https://google.com", "browser"),
        AppPreset("YouTube", "com.google.android.youtube", "https://youtube.com", "video"),
        AppPreset("Gmail", "com.google.android.gm", "https://mail.google.com", "mail"),
        AppPreset("Calendar", "com.google.android.calendar", "https://calendar.google.com", "calendar"),
        AppPreset("Keep Notes", "com.google.android.keep", "https://keep.google.com", "notes"),
        AppPreset("Calculator", "com.google.android.calculator", "https://google.com/search?q=calculator", "calculator"),
        AppPreset("Maps", "com.google.android.apps.maps", "https://maps.google.com", "browser"),
        AppPreset("GitHub", "com.github.android", "https://github.com", "code")
    )

    var name by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var iconCategory by remember { mutableStateOf("general") }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Favorite App 📱",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.take(4).forEach { p ->
                        FilterChip(
                            selected = name == p.name,
                            onClick = {
                                name = p.name
                                packageName = p.packageName
                                url = p.url
                                iconCategory = p.category
                                nameError = false
                            },
                            label = { Text(p.name) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.drop(4).forEach { p ->
                        FilterChip(
                            selected = name == p.name,
                            onClick = {
                                name = p.name
                                packageName = p.packageName
                                url = p.url
                                iconCategory = p.category
                                nameError = false
                            },
                            label = { Text(p.name) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text("App Name *") },
                    placeholder = { Text("e.g. Notion, Slack, Drive") },
                    isError = nameError,
                    supportingText = { if (nameError) Text("Name cannot be empty") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_fav_app_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Android Package (Optional)") },
                    placeholder = { Text("e.g. com.google.android.gm") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_fav_app_package")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Web Fallback URL") },
                    placeholder = { Text("https://...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_fav_app_url")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Icon Category:",
                    style = MaterialTheme.typography.labelMedium
                )

                val categories = listOf("browser", "video", "mail", "notes", "calendar", "calculator", "code", "chat")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(4).forEach { c ->
                        FilterChip(
                            selected = iconCategory == c,
                            onClick = { iconCategory = c },
                            label = { Text(c) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    onSave(name.trim(), packageName.trim(), url.trim(), iconCategory)
                },
                modifier = Modifier.testTag("btn_save_fav_app")
            ) {
                Text("Add to Dock")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_fav_app")
            ) {
                Text("Cancel")
            }
        }
    )
}
