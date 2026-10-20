package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import com.example.data.model.UserProfile

@Composable
fun EditProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (name: String, email: String, motto: String, target: Int) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var email by remember { mutableStateOf(currentProfile.email) }
    var motto by remember { mutableStateOf(currentProfile.motto) }
    var dailyTargetStr by remember { mutableStateOf(currentProfile.dailyTargetGoals.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Profile & Targets 👤",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Google Account / Email") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_email")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = motto,
                    onValueChange = { motto = it },
                    label = { Text("Personal Motto / Focus") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_motto")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dailyTargetStr,
                    onValueChange = { dailyTargetStr = it.filter { char -> char.isDigit() } },
                    label = { Text("Daily Target Goals Count") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_target")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = dailyTargetStr.toIntOrNull() ?: 5
                    onSave(
                        name.ifBlank { "Shihab Islam" },
                        email.ifBlank { "shihabislam30072007@gmail.com" },
                        motto,
                        target.coerceIn(1, 25)
                    )
                },
                modifier = Modifier.testTag("btn_save_profile")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_profile")
            ) {
                Text("Cancel")
            }
        }
    )
}
