package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FavoriteApp
import com.example.data.model.UserProfile
import com.example.data.model.VaultDocument
import com.example.data.sync.SyncInfo
import com.example.data.sync.SyncStatus
import com.example.ui.components.AddFavoriteAppDialog
import com.example.ui.components.AddVaultDocDialog
import com.example.ui.components.EditFullProfileDialog
import com.example.ui.components.FavoriteAppChip
import com.example.ui.theme.GoalGold
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.GoalPurple
import com.example.ui.theme.GoalRose
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    vaultDocuments: List<VaultDocument> = emptyList(),
    favoriteApps: List<FavoriteApp> = emptyList(),
    syncInfo: SyncInfo,
    onUpdateFullProfile: (
        name: String,
        titleRole: String,
        email: String,
        phone: String,
        location: String,
        aboutMe: String,
        professionalBio: String,
        skills: String,
        languages: String,
        education: String,
        workExperience: String,
        cvResumeInfo: String,
        githubUrl: String,
        linkedinUrl: String,
        portfolioUrl: String,
        whatsappNumber: String,
        targetGoals: Int
    ) -> Unit,
    onAddVaultDoc: (String, String, String, String, String, String, Boolean) -> Unit = { _, _, _, _, _, _, _ -> },
    onDeleteVaultDoc: (VaultDocument) -> Unit = {},
    onToggleSensitiveLock: () -> Unit = {},
    onUpdateSecuritySettings: (Boolean, String, String) -> Unit = { _, _, _ -> },
    onResetAllData: (Context) -> Unit = {},
    onTriggerSync: () -> Unit,
    onToggleOfflineMode: (Boolean) -> Unit,
    onAddFavoriteApp: (String, String, String, String) -> Unit,
    onDeleteFavoriteApp: (FavoriteApp) -> Unit,
    onLaunchFavoriteApp: (Context, FavoriteApp) -> Unit,
    getExportJson: suspend () -> String,
    onRestoreBackup: (String, (Boolean) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddVaultDialog by remember { mutableStateOf(false) }
    var showAddAppDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportJsonContent by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var selectedVaultCategory by remember { mutableStateOf("All") }

    val vaultCategories = listOf("All", "CV", "Certificates", "ID documents", "Passport information", "Contracts", "Project documents", "Important PDFs", "Receipts")

    val filteredDocs = if (selectedVaultCategory == "All") {
        vaultDocuments
    } else {
        vaultDocuments.filter { it.category.equals(selectedVaultCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Profile Card with user photo & info
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("profile_hero_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Photo with glowing border
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            modifier = Modifier.size(100.dp)
                        ) {}
                        Image(
                            painter = painterResource(id = R.drawable.user_logo),
                            contentDescription = "Developer Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .testTag("img_profile_avatar")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = userProfile.titleRole,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = userProfile.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showEditProfileDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_open_edit_profile")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("প্রোফাইল এডিট করুন (Edit Profile)")
                    }
                }
            }
        }

        // 2. Contact & Social Links
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("profile_contacts_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Contact & Social Presence 🌐",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${userProfile.email}"))
                                context.startActivity(Intent.createChooser(intent, "Send Email"))
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = userProfile.email, style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${userProfile.phone}"))
                                context.startActivity(intent)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = GoalGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = userProfile.phone, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (userProfile.githubUrl.isNotBlank()) {
                            SocialLinkChip(title = "GitHub", url = userProfile.githubUrl, context = context)
                        }
                        if (userProfile.linkedinUrl.isNotBlank()) {
                            SocialLinkChip(title = "LinkedIn", url = userProfile.linkedinUrl, context = context)
                        }
                        if (userProfile.portfolioUrl.isNotBlank()) {
                            SocialLinkChip(title = "Portfolio", url = userProfile.portfolioUrl, context = context)
                        }
                        if (userProfile.whatsappNumber.isNotBlank()) {
                            SocialLinkChip(title = "WhatsApp", url = "https://wa.me/${userProfile.whatsappNumber.replace("+", "").replace("-", "").replace(" ", "")}", context = context)
                        }
                    }
                }
            }
        }

        // 3. About Me & Professional Bio
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("profile_bio_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About Me & Professional Bio 📝",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = userProfile.aboutMe,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = userProfile.professionalBio,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // 4. Skills & Languages
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("profile_skills_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Skills & Languages ⚡",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        userProfile.skills.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            ) {
                                Text(
                                    text = skill,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Languages: ${userProfile.languages}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // 5. Education & Work Experience & CV
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("profile_experience_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Career & Education 🎓",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(imageVector = Icons.Default.Work, contentDescription = null, tint = GoalPurple, modifier = Modifier.size(18.dp).padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Work Experience", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Text(userProfile.workExperience, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = GoalGreen, modifier = Modifier.size(18.dp).padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Education", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Text(userProfile.education, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("CV / Resume", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                Text(userProfile.cvResumeInfo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Opening ${userProfile.cvResumeInfo}", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("View CV", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // 6. Secure Documents Vault (CV, Certificates, ID, Passport, Contracts, PDFs, Receipts)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("profile_vault_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.FolderSpecial, contentDescription = null, tint = GoalGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Vault & Documents (${vaultDocuments.size}) 🔐",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(onClick = onToggleSensitiveLock, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = if (userProfile.sensitiveDataLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Toggle Sensitive Data Lock",
                                tint = if (userProfile.sensitiveDataLocked) GoalRose else GoalGreen
                            )
                        }
                    }

                    Text(
                        text = "CV, Certificates, ID documents, Passport, Contracts, Project PDFs, Receipts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Vault Category filter chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        vaultCategories.forEach { cat ->
                            FilterChip(
                                selected = selectedVaultCategory == cat,
                                onClick = { selectedVaultCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredDocs.isEmpty()) {
                        Text(
                            text = "No documents in this category yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        filteredDocs.forEach { doc ->
                            VaultItemRow(
                                doc = doc,
                                isLocked = userProfile.sensitiveDataLocked && doc.isSensitiveLocked,
                                onDelete = { onDeleteVaultDoc(doc) }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showAddVaultDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_add_vault_doc")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ডকুমেন্ট যোগ করুন (Add Document)")
                    }
                }
            }
        }

        // 7. App Lock, Security & Session Management
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("profile_security_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "App Lock & Security 🛡️",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("App Lock (Fingerprint / PIN)", fontWeight = FontWeight.SemiBold)
                            Text("পিন বা ফিঙ্গারপ্রিন্ট দিয়ে অ্যাপ সুরক্ষিত রাখুন", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = userProfile.appLockEnabled,
                            onCheckedChange = { enabled ->
                                onUpdateSecuritySettings(enabled, userProfile.appLockPin, userProfile.autoLockDuration)
                            },
                            modifier = Modifier.testTag("switch_app_lock")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PIN Code: •••• (${userProfile.appLockPin})", style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = {
                            Toast.makeText(context, "PIN is configured to ${userProfile.appLockPin}", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Change PIN")
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Auto Lock Interval", style = MaterialTheme.typography.bodyMedium)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(userProfile.autoLockDuration, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Session Management", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text(userProfile.sessionDeviceInfo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = {
                            Toast.makeText(context, "Session active on current device", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Check")
                        }
                    }
                }
            }
        }

        // 8. Favorite Apps Dock
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("profile_apps_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Favorite Apps Hub 🚀",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { showAddAppDialog = true }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Favorite App", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Text(
                        text = "পছন্দের প্রয়োজনীয় অ্যাপগুলো দ্রুত ওপেন করুন",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        favoriteApps.forEach { app ->
                            FavoriteAppChip(
                                app = app,
                                onClick = { onLaunchFavoriteApp(context, app) }
                            )
                        }
                    }
                }
            }
        }

        // 9. Data Backup, Export & Danger Zone
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("profile_data_management_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Data Backup & Cloud Sync ☁️",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Offline Mode", fontWeight = FontWeight.SemiBold)
                            Text("ইন্টারনেট ছাড়াও লোকাল ডাটাবেজে কাজ করুন", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = syncInfo.isOfflineMode,
                            onCheckedChange = onToggleOfflineMode,
                            modifier = Modifier.testTag("switch_offline_mode")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onTriggerSync,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("btn_trigger_sync")
                        ) {
                            Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Now")
                        }

                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    exportJsonContent = getExportJson()
                                    showExportDialog = true
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("btn_export_data")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export JSON")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Delete Account / Reset Data (Danger Zone)
                    Button(
                        onClick = { showResetConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoalRose),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_reset_all_data")
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete / Reset All Data (রিসেট ডাটা)")
                    }
                }
            }
        }
    }

    // Dialogs
    if (showEditProfileDialog) {
        EditFullProfileDialog(
            currentProfile = userProfile,
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { name, role, email, phone, location, about, bio, skills, lang, edu, exp, cv, git, link, port, wa, target ->
                onUpdateFullProfile(name, role, email, phone, location, about, bio, skills, lang, edu, exp, cv, git, link, port, wa, target)
                showEditProfileDialog = false
            }
        )
    }

    if (showAddVaultDialog) {
        AddVaultDocDialog(
            onDismiss = { showAddVaultDialog = false },
            onConfirm = { cat, title, num, issue, exp, notes, isSensitive ->
                onAddVaultDoc(cat, title, num, issue, exp, notes, isSensitive)
                showAddVaultDialog = false
            }
        )
    }

    if (showAddAppDialog) {
        AddFavoriteAppDialog(
            onDismiss = { showAddAppDialog = false },
            onSave = { name, pkg, url, cat ->
                onAddFavoriteApp(name, pkg, url, cat)
                showAddAppDialog = false
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Exported Data Backup") },
            text = {
                Column {
                    Text("Copy this JSON or save it for restore / backup:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportJsonContent,
                        onValueChange = {},
                        readOnly = true,
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("GoalPulseBackup", exportJsonContent))
                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    showExportDialog = false
                }) {
                    Text("Copy")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Delete & Reset All Data?", color = GoalRose, fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to reset all goals, habits, finances, documents, and client records? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAllData(context)
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoalRose)
                ) {
                    Text("Yes, Reset Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun VaultItemRow(doc: VaultDocument, isLocked: Boolean, onDelete: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth().testTag("vault_row_${doc.id}")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isLocked) GoalRose.copy(alpha = 0.15f) else GoalPurple.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.Description,
                        contentDescription = null,
                        tint = if (isLocked) GoalRose else GoalPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = doc.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Text(
                        text = doc.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                if (isLocked) {
                    Text(
                        text = "•••••••••••• (Locked Data)",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoalRose
                    )
                } else {
                    if (doc.identifierOrNumber.isNotBlank()) {
                        Text(
                            text = "ID: ${doc.identifierOrNumber}${if (doc.issueDate.isNotBlank()) " • Issued: ${doc.issueDate}" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (doc.notes.isNotBlank()) {
                        Text(
                            text = doc.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Document", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun SocialLinkChip(title: String, url: String, context: Context) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.clickable {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Cannot open $url", Toast.LENGTH_SHORT).show()
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}
