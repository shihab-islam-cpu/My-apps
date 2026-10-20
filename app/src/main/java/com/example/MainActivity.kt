package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.sync.SyncStatus
import com.example.reminder.NotificationHelper
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WebsitesScreen
import com.example.ui.theme.GoalGold
import com.example.ui.theme.GoalGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannels(this)

        val targetTab = intent.getIntExtra("EXTRA_NAV_TAB", -1)
        if (targetTab in 0..4) {
            viewModel.setSelectedTab(targetTab)
        }

        setContent {
            val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDark) {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    val context = LocalContext.current
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val websites by viewModel.websites.collectAsStateWithLifecycle()
    val clientRequests by viewModel.clientRequests.collectAsStateWithLifecycle()
    val crmClients by viewModel.crmClients.collectAsStateWithLifecycle()
    val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val favoriteApps by viewModel.favoriteApps.collectAsStateWithLifecycle()
    val allIncomes by viewModel.allIncomes.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val vaultDocuments by viewModel.vaultDocuments.collectAsStateWithLifecycle()
    val syncInfo by viewModel.syncState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    // Notification permission request for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Handle back button to return to Dashboard tab
    BackHandler(enabled = selectedTab != 0) {
        viewModel.setSelectedTab(0)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.user_logo),
                            contentDescription = "Developer Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ডেভেলপার",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                    }
                },
                actions = {
                    // Dark / Light Mode Switcher
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.testTag("btn_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = if (isDark) GoalGold else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Cloud Sync Indicator
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (syncInfo.status) {
                            SyncStatus.SYNCING -> GoalGold.copy(alpha = 0.15f)
                            SyncStatus.OFFLINE -> MaterialTheme.colorScheme.surfaceVariant
                            else -> GoalGreen.copy(alpha = 0.15f)
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("badge_sync_status")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = when (syncInfo.status) {
                                    SyncStatus.SYNCING -> Icons.Default.CloudSync
                                    SyncStatus.OFFLINE -> Icons.Default.CloudOff
                                    else -> Icons.Default.CloudDone
                                },
                                contentDescription = null,
                                tint = when (syncInfo.status) {
                                    SyncStatus.SYNCING -> GoalGold
                                    SyncStatus.OFFLINE -> MaterialTheme.colorScheme.outline
                                    else -> GoalGreen
                                },
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (syncInfo.status) {
                                    SyncStatus.SYNCING -> "Syncing"
                                    SyncStatus.OFFLINE -> "Offline"
                                    else -> "Synced"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = when (syncInfo.status) {
                                    SyncStatus.SYNCING -> GoalGold
                                    SyncStatus.OFFLINE -> MaterialTheme.colorScheme.onSurfaceVariant
                                    else -> GoalGreen
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("ড্যাশবোর্ড") },
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = { Icon(Icons.Default.Language, contentDescription = "Websites") },
                    label = { Text("ওয়েবসাইট") },
                    modifier = Modifier.testTag("nav_tab_websites")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Finance") },
                    label = { Text("আয়-ব্যয়") },
                    modifier = Modifier.testTag("nav_tab_finance")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.setSelectedTab(3) },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Goals") },
                    label = { Text("লক্ষ্যসমূহ") },
                    modifier = Modifier.testTag("nav_tab_goals")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { viewModel.setSelectedTab(4) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("প্রোফাইল") },
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = selectedTab,
            label = "ScreenTransition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tab ->
            when (tab) {
                0 -> DashboardScreen(
                    userProfile = userProfile,
                    tasks = allTasks,
                    habits = habits,
                    websites = websites,
                    clientRequests = clientRequests,
                    favoriteApps = favoriteApps,
                    syncInfo = syncInfo,
                    onToggleTask = { viewModel.toggleTaskCompletion(it) },
                    onDeleteTask = { viewModel.deleteTask(it) },
                    onEditTask = {
                        viewModel.setSelectedTab(3) // Jump to Goals
                    },
                    onAddTask = { title, desc, cat, priority, due, reminder, progress ->
                        viewModel.addTask(title, desc, cat, priority, due, reminder, progress)
                    },
                    onToggleHabit = { viewModel.toggleHabitToday(it) },
                    onAddHabit = { title, cat, icon, color ->
                        viewModel.addHabit(title, cat, icon, color)
                    },
                    onDeleteHabit = { viewModel.deleteHabit(it) },
                    onAddFavoriteApp = { name, pkg, url, cat ->
                        viewModel.addFavoriteApp(name, pkg, url, cat)
                    },
                    onLaunchApp = { ctx, app ->
                        viewModel.launchFavoriteApp(ctx, app)
                    },
                    onTriggerSummaryNow = { ctx ->
                        viewModel.triggerDailySummaryNow(ctx)
                    },
                    onTriggerReminderNow = { ctx ->
                        viewModel.triggerInstantTaskReminderTest(ctx)
                    },
                    onTriggerSync = {
                        viewModel.triggerCloudSync()
                    },
                    onNavigateToGoals = {
                        viewModel.setSelectedTab(3)
                    },
                    onNavigateToWebsites = {
                        viewModel.setSelectedTab(1)
                    },
                    onNavigateToReminders = {
                        viewModel.setSelectedTab(3)
                    },
                    onNavigateToFinance = {
                        viewModel.setSelectedTab(2)
                    }
                )

                1 -> WebsitesScreen(
                    websites = websites,
                    clientRequests = clientRequests,
                    crmClients = crmClients,
                    onAddWebsite = { title, client, url, tech, status, desc ->
                        viewModel.addWebsiteProject(title, client, url, tech, status, desc)
                    },
                    onUpdateWebsite = { viewModel.updateWebsiteProject(it) },
                    onDeleteWebsite = { viewModel.deleteWebsiteProject(it) },
                    onAddClientRequest = { client, email, phone, type, budget, msg ->
                        viewModel.addClientRequest(client, email, phone, type, budget, msg)
                    },
                    onUpdateClientRequestStatus = { req, status ->
                        viewModel.updateClientRequestStatus(req, status)
                    },
                    onDeleteClientRequest = { viewModel.deleteClientRequest(it) },
                    onAddClientCrm = { name, contact, proj, budget, status, deadline, notes, prev, comm ->
                        viewModel.addClientCrm(name, contact, proj, budget, status, deadline, notes, prev, comm)
                    },
                    onDeleteClientCrm = { viewModel.deleteClientCrm(it) }
                )

                2 -> FinanceScreen(
                    incomes = allIncomes,
                    expenses = allExpenses,
                    onAddIncome = { client, project, amount, status, date, notes ->
                        viewModel.addIncome(client, project, amount, status, date, notes)
                    },
                    onDeleteIncome = { viewModel.deleteIncome(it) },
                    onAddExpense = { category, title, amount, date, notes ->
                        viewModel.addExpense(category, title, amount, date, notes)
                    },
                    onDeleteExpense = { viewModel.deleteExpense(it) }
                )

                3 -> GoalsScreen(
                    tasks = filteredTasks,
                    searchQuery = searchQuery,
                    selectedCategory = selectedCategory,
                    selectedFilter = selectedFilter,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onCategoryChange = { viewModel.setSelectedCategory(it) },
                    onFilterChange = { viewModel.setSelectedFilter(it) },
                    onToggleTask = { viewModel.toggleTaskCompletion(it) },
                    onDeleteTask = { viewModel.deleteTask(it) },
                    onAddTask = { title, desc, cat, priority, due, reminder, progress ->
                        viewModel.addTask(title, desc, cat, priority, due, reminder, progress)
                    },
                    onUpdateTask = { viewModel.updateTask(it) }
                )

                4 -> ProfileScreen(
                    userProfile = userProfile,
                    vaultDocuments = vaultDocuments,
                    favoriteApps = favoriteApps,
                    syncInfo = syncInfo,
                    onUpdateFullProfile = { name, role, email, phone, loc, about, bio, skills, lang, edu, exp, cv, git, link, port, wa, target ->
                        viewModel.updateFullProfile(name, role, email, phone, loc, about, bio, skills, lang, edu, exp, cv, git, link, port, wa, target)
                    },
                    onAddVaultDoc = { cat, title, num, issue, exp, notes, isSensitive ->
                        viewModel.addVaultDocument(cat, title, num, issue, exp, notes, isSensitive)
                    },
                    onDeleteVaultDoc = { viewModel.deleteVaultDocument(it) },
                    onToggleSensitiveLock = { viewModel.toggleSensitiveDataLock() },
                    onUpdateSecuritySettings = { enabled, pin, autoLock ->
                        viewModel.updateSecuritySettings(enabled, pin, autoLock)
                    },
                    onResetAllData = { ctx -> viewModel.resetAllData(ctx) },
                    onTriggerSync = { viewModel.triggerCloudSync() },
                    onToggleOfflineMode = { viewModel.setOfflineMode(it) },
                    onAddFavoriteApp = { name, pkg, url, cat ->
                        viewModel.addFavoriteApp(name, pkg, url, cat)
                    },
                    onDeleteFavoriteApp = { viewModel.deleteFavoriteApp(it) },
                    onLaunchFavoriteApp = { ctx, app ->
                        viewModel.launchFavoriteApp(ctx, app)
                    },
                    getExportJson = { viewModel.getExportJson() },
                    onRestoreBackup = { json, cb -> viewModel.restoreBackup(json, cb) }
                )
            }
        }
    }
}
