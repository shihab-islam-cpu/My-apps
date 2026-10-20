package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClientCrm
import com.example.data.model.ClientRequest
import com.example.data.model.WebsiteProject
import com.example.ui.components.AddClientCrmDialog
import com.example.ui.components.AddClientRequestDialog
import com.example.ui.components.AddWebsiteDialog
import com.example.ui.components.ClientRequestCard
import com.example.ui.components.WebsiteCard
import com.example.ui.theme.GoalGold
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.GoalRose

@Composable
fun WebsitesScreen(
    websites: List<WebsiteProject>,
    clientRequests: List<ClientRequest>,
    crmClients: List<ClientCrm> = emptyList(),
    onAddWebsite: (String, String, String, String, String, String) -> Unit,
    onUpdateWebsite: (WebsiteProject) -> Unit,
    onDeleteWebsite: (WebsiteProject) -> Unit,
    onAddClientRequest: (String, String, String, String, String, String) -> Unit,
    onUpdateClientRequestStatus: (ClientRequest, String) -> Unit,
    onDeleteClientRequest: (ClientRequest) -> Unit,
    onAddClientCrm: (String, String, String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onDeleteClientCrm: (ClientCrm) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Websites, 1: Client Requests, 2: Client CRM
    var showAddWebsiteDialog by remember { mutableStateOf(false) }
    var showAddRequestDialog by remember { mutableStateOf(false) }
    var showAddCrmDialog by remember { mutableStateOf(false) }
    var projectToEdit by remember { mutableStateOf<WebsiteProject?>(null) }

    val liveCount = websites.count { it.status.equals("Live", ignoreCase = true) }
    val newRequestsCount = clientRequests.count { it.status.equals("New Request", ignoreCase = true) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (selectedSubTab) {
                        0 -> showAddWebsiteDialog = true
                        1 -> showAddRequestDialog = true
                        2 -> showAddCrmDialog = true
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("fab_add_website_or_req")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = when (selectedSubTab) {
                        0 -> "Add Website"
                        1 -> "New Request"
                        else -> "Add Client CRM"
                    }
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
                text = "Websites & Client Hub 💻",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Manage your built websites, live links, and client requests & CRM",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sub Tab Selector (Websites vs Client Requests vs CRM)
            TabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.testTag("tabrow_websites_hub")
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Websites ($liveCount Live)")
                        }
                    },
                    modifier = Modifier.testTag("subtab_websites")
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Requests ($newRequestsCount New)")
                        }
                    },
                    modifier = Modifier.testTag("subtab_requests")
                )
                Tab(
                    selected = selectedSubTab == 2,
                    onClick = { selectedSubTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Client CRM")
                        }
                    },
                    modifier = Modifier.testTag("subtab_crm")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedSubTab) {
                0 -> {
                    // Websites List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (websites.isEmpty()) {
                            item {
                                EmptyWebsitesState(
                                    title = "No websites added yet",
                                    subtitle = "Tap '+' to add websites you built with live links, tech stack, and update times."
                                )
                            }
                        } else {
                            items(websites, key = { it.id }) { website ->
                                WebsiteCard(
                                    project = website,
                                    onEdit = { projectToEdit = website },
                                    onDelete = { onDeleteWebsite(website) }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // Client Requests List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (clientRequests.isEmpty()) {
                            item {
                                EmptyWebsitesState(
                                    title = "No client inquiries yet",
                                    subtitle = "Incoming project inquiries and website build requests will be shown here."
                                )
                            }
                        } else {
                            items(clientRequests, key = { it.id }) { request ->
                                ClientRequestCard(
                                    request = request,
                                    onStatusChange = { st -> onUpdateClientRequestStatus(request, st) },
                                    onDelete = { onDeleteClientRequest(request) }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // Client CRM Details
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (crmClients.isEmpty()) {
                            item {
                                EmptyWebsitesState(
                                    title = "No CRM records yet",
                                    subtitle = "Tap '+' to save client contact, budget, deadline, and communication history."
                                )
                            }
                        } else {
                            items(crmClients, key = { it.id }) { client ->
                                ClientCrmCard(
                                    client = client,
                                    onDelete = { onDeleteClientCrm(client) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddWebsiteDialog) {
        AddWebsiteDialog(
            initialProject = null,
            onDismiss = { showAddWebsiteDialog = false },
            onSave = { title, client, url, tech, status, desc ->
                onAddWebsite(title, client, url, tech, status, desc)
                showAddWebsiteDialog = false
            }
        )
    }

    if (projectToEdit != null) {
        AddWebsiteDialog(
            initialProject = projectToEdit,
            onDismiss = { projectToEdit = null },
            onSave = { title, client, url, tech, status, desc ->
                projectToEdit?.let {
                    onUpdateWebsite(
                        it.copy(
                            title = title,
                            clientName = client,
                            liveUrl = url,
                            techStack = tech,
                            status = status,
                            description = desc,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                projectToEdit = null
            }
        )
    }

    if (showAddRequestDialog) {
        AddClientRequestDialog(
            onDismiss = { showAddRequestDialog = false },
            onSave = { client, email, phone, type, budget, msg ->
                onAddClientRequest(client, email, phone, type, budget, msg)
                showAddRequestDialog = false
            }
        )
    }

    if (showAddCrmDialog) {
        AddClientCrmDialog(
            onDismiss = { showAddCrmDialog = false },
            onConfirm = { clientName, contact, project, budget, paymentStatus, deadline, notes, previousProjects, communicationHistory ->
                onAddClientCrm(clientName, contact, project, budget, paymentStatus, deadline, notes, previousProjects, communicationHistory)
                showAddCrmDialog = false
            }
        )
    }
}

@Composable
fun ClientCrmCard(client: ClientCrm, onDelete: () -> Unit) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("crm_card_${client.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = client.clientName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = client.project,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (client.paymentStatus) {
                        "Paid" -> GoalGreen.copy(alpha = 0.15f)
                        "Due" -> GoalRose.copy(alpha = 0.15f)
                        else -> GoalGold.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = client.paymentStatus,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (client.paymentStatus) {
                            "Paid" -> GoalGreen
                            "Due" -> GoalRose
                            else -> GoalGold
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ContactPhone, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = client.contact, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Budget: ${client.budget}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                if (client.deadline.isNotBlank()) {
                    Text(text = "Deadline: ${client.deadline}", style = MaterialTheme.typography.bodySmall, color = GoalGold)
                }
            }

            if (client.previousProjects.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Previous Projects: ${client.previousProjects}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (client.communicationHistory.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Text(
                        text = "History: ${client.communicationHistory}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if (client.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${client.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete CRM", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyWebsitesState(title: String, subtitle: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
