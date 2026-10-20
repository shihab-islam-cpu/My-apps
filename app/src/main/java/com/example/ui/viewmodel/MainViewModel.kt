package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ClientCrm
import com.example.data.model.ClientRequest
import com.example.data.model.ClientWebsite
import com.example.data.model.ExpenseRecord
import com.example.data.model.FavoriteApp
import com.example.data.model.GoalTask
import com.example.data.model.Habit
import com.example.data.model.IncomeRecord
import com.example.data.model.Priority
import com.example.data.model.UserProfile
import com.example.data.model.VaultDocument
import com.example.data.model.WebsiteProject
import com.example.data.sync.SyncInfo
import com.example.data.sync.SyncManager
import com.example.reminder.NotificationHelper
import com.example.reminder.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val syncManager = SyncManager(application)

    // User profile state
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Navigation and filters
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All") // "All", "Pending", "Completed", "High Priority"
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // Dark & Light Mode state
    private val _isDarkMode = MutableStateFlow(true) // Default modern executive dark
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Database streams
    val allTasks: StateFlow<List<GoalTask>> = db.goalTaskDao().getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteApps: StateFlow<List<FavoriteApp>> = db.favoriteAppDao().getAllFavoriteApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<Habit>> = db.habitDao().getAllHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val websites: StateFlow<List<WebsiteProject>> = db.websiteProjectDao().getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clientWebsites: StateFlow<List<ClientWebsite>> = db.clientWebsiteDao().getAllClientWebsites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clientRequests: StateFlow<List<ClientRequest>> = db.clientRequestDao().getAllRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allIncomes: StateFlow<List<IncomeRecord>> = db.financeDao().getAllIncomes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<ExpenseRecord>> = db.financeDao().getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vaultDocuments: StateFlow<List<VaultDocument>> = db.vaultDao().getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val crmClients: StateFlow<List<ClientCrm>> = db.clientCrmDao().getAllClients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncState: StateFlow<SyncInfo> = syncManager.syncState

    // Filtered tasks for Goals tab
    val filteredTasks: StateFlow<List<GoalTask>> = combine(
        allTasks,
        searchQuery,
        selectedCategory,
        selectedFilter
    ) { tasks, query, category, filter ->
        tasks.filter { task ->
            val matchesQuery = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" || task.category.equals(category, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Pending" -> !task.isCompleted
                "Completed" -> task.isCompleted
                "High Priority" -> task.priority == Priority.HIGH
                else -> true
            }

            matchesQuery && matchesCategory && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // Task Actions
    fun addTask(
        title: String,
        description: String,
        category: String,
        priority: Priority,
        dueDateMillis: Long,
        reminderTimeMillis: Long?,
        progress: Int = 0
    ) {
        viewModelScope.launch {
            val task = GoalTask(
                title = title,
                description = description,
                category = category,
                priority = priority,
                dueDateMillis = dueDateMillis,
                reminderTimeMillis = reminderTimeMillis,
                progress = progress
            )
            val insertedId = db.goalTaskDao().insertTask(task)
            if (reminderTimeMillis != null && reminderTimeMillis > System.currentTimeMillis()) {
                val fullTask = task.copy(id = insertedId)
                ReminderScheduler.scheduleTaskReminder(getApplication(), fullTask)
            }
        }
    }

    fun updateTask(task: GoalTask) {
        viewModelScope.launch {
            db.goalTaskDao().updateTask(task)
            if (task.reminderTimeMillis != null && task.reminderTimeMillis > System.currentTimeMillis()) {
                ReminderScheduler.scheduleTaskReminder(getApplication(), task)
            }
        }
    }

    fun toggleTaskCompletion(task: GoalTask) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                completionDateMillis = if (!task.isCompleted) System.currentTimeMillis() else null,
                progress = if (!task.isCompleted) 100 else task.progress
            )
            db.goalTaskDao().updateTask(updated)
            if (updated.isCompleted) {
                ReminderScheduler.cancelTaskReminder(getApplication(), task.id)
            }
        }
    }

    fun deleteTask(task: GoalTask) {
        viewModelScope.launch {
            db.goalTaskDao().deleteTask(task)
            ReminderScheduler.cancelTaskReminder(getApplication(), task.id)
        }
    }

    // Favorite Apps Actions
    fun addFavoriteApp(name: String, packageName: String, fallbackUrl: String, category: String) {
        viewModelScope.launch {
            val app = FavoriteApp(
                appName = name,
                packageName = packageName,
                webFallbackUrl = fallbackUrl,
                iconCategory = category
            )
            db.favoriteAppDao().insertApp(app)
        }
    }

    fun deleteFavoriteApp(app: FavoriteApp) {
        viewModelScope.launch {
            db.favoriteAppDao().deleteApp(app)
        }
    }

    fun launchFavoriteApp(context: Context, app: FavoriteApp) {
        viewModelScope.launch {
            db.favoriteAppDao().updateApp(app.copy(launchCount = app.launchCount + 1))
        }

        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(app.packageName)

        if (launchIntent != null) {
            try {
                context.startActivity(launchIntent)
                return
            } catch (e: Exception) {
                // fallback to web
            }
        }

        if (app.webFallbackUrl.isNotBlank()) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(app.webFallbackUrl))
                context.startActivity(browserIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Cannot open ${app.appName}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "${app.appName} is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    // Habits & Streaks
    fun toggleHabitToday(habit: Habit) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(Date())
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)

            val completedDates = habit.completedDatesCsv.split(",").filter { it.isNotBlank() }.toMutableList()
            val isDoneToday = completedDates.contains(todayStr)

            val updatedHabit = if (isDoneToday) {
                completedDates.remove(todayStr)
                val newLastDate = completedDates.lastOrNull() ?: ""
                habit.copy(
                    currentStreak = maxOf(0, habit.currentStreak - 1),
                    lastCompletedDate = newLastDate,
                    completedDatesCsv = completedDates.joinToString(",")
                )
            } else {
                if (!completedDates.contains(todayStr)) {
                    completedDates.add(todayStr)
                }
                val newStreak = when (habit.lastCompletedDate) {
                    yesterdayStr -> habit.currentStreak + 1
                    todayStr -> habit.currentStreak
                    else -> 1
                }
                val newLongest = maxOf(habit.longestStreak, newStreak)
                habit.copy(
                    currentStreak = newStreak,
                    longestStreak = newLongest,
                    lastCompletedDate = todayStr,
                    completedDatesCsv = completedDates.joinToString(",")
                )
            }

            db.habitDao().updateHabit(updatedHabit)

            if (updatedHabit.currentStreak > _userProfile.value.streakDays) {
                _userProfile.value = _userProfile.value.copy(streakDays = updatedHabit.currentStreak)
            }
        }
    }

    fun addHabit(title: String, category: String, iconType: String, colorHex: String) {
        viewModelScope.launch {
            val habit = Habit(
                title = title,
                category = category,
                iconType = iconType,
                colorHex = colorHex
            )
            db.habitDao().insertHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            db.habitDao().deleteHabit(habit)
        }
    }

    // Website Projects CRUD
    fun addWebsiteProject(title: String, clientName: String, url: String, techStack: String, status: String, desc: String) {
        viewModelScope.launch {
            val project = WebsiteProject(
                title = title,
                clientName = clientName,
                liveUrl = url,
                techStack = techStack,
                status = status,
                description = desc
            )
            db.websiteProjectDao().insertProject(project)
        }
    }

    fun updateWebsiteProject(project: WebsiteProject) {
        viewModelScope.launch {
            db.websiteProjectDao().updateProject(project)
        }
    }

    fun deleteWebsiteProject(project: WebsiteProject) {
        viewModelScope.launch {
            db.websiteProjectDao().deleteProject(project)
        }
    }

    // Client Requests & Messages CRUD
    fun addClientRequest(clientName: String, email: String, phone: String, type: String, budget: String, message: String) {
        viewModelScope.launch {
            val request = ClientRequest(
                clientName = clientName,
                clientEmail = email,
                clientPhone = phone,
                projectType = type,
                budget = budget,
                message = message,
                status = "New Request"
            )
            db.clientRequestDao().insertRequest(request)
        }
    }

    fun updateClientRequestStatus(request: ClientRequest, status: String) {
        viewModelScope.launch {
            db.clientRequestDao().updateRequest(request.copy(status = status))
        }
    }

    fun deleteClientRequest(request: ClientRequest) {
        viewModelScope.launch {
            db.clientRequestDao().deleteRequest(request)
        }
    }

    // Incomes CRUD
    fun addIncome(clientName: String, projectName: String, amount: Double, status: String, date: String, notes: String) {
        viewModelScope.launch {
            val record = IncomeRecord(
                clientName = clientName,
                projectName = projectName,
                amount = amount,
                paymentStatus = status,
                paymentDate = date,
                notes = notes
            )
            db.financeDao().insertIncome(record)
        }
    }

    fun updateIncome(record: IncomeRecord) {
        viewModelScope.launch {
            db.financeDao().updateIncome(record)
        }
    }

    fun deleteIncome(record: IncomeRecord) {
        viewModelScope.launch {
            db.financeDao().deleteIncome(record)
        }
    }

    // Expenses CRUD
    fun addExpense(category: String, title: String, amount: Double, date: String, notes: String) {
        viewModelScope.launch {
            val record = ExpenseRecord(
                category = category,
                title = title,
                amount = amount,
                date = date,
                notes = notes
            )
            db.financeDao().insertExpense(record)
        }
    }

    fun updateExpense(record: ExpenseRecord) {
        viewModelScope.launch {
            db.financeDao().updateExpense(record)
        }
    }

    fun deleteExpense(record: ExpenseRecord) {
        viewModelScope.launch {
            db.financeDao().deleteExpense(record)
        }
    }

    // Vault Documents CRUD
    fun addVaultDocument(
        category: String,
        title: String,
        identifier: String,
        issueDate: String,
        expiryDate: String,
        notes: String,
        isSensitive: Boolean
    ) {
        viewModelScope.launch {
            val doc = VaultDocument(
                category = category,
                title = title,
                identifierOrNumber = identifier,
                issueDate = issueDate,
                expiryDate = expiryDate,
                notes = notes,
                isSensitiveLocked = isSensitive
            )
            db.vaultDao().insertDocument(doc)
        }
    }

    fun updateVaultDocument(doc: VaultDocument) {
        viewModelScope.launch {
            db.vaultDao().updateDocument(doc)
        }
    }

    fun deleteVaultDocument(doc: VaultDocument) {
        viewModelScope.launch {
            db.vaultDao().deleteDocument(doc)
        }
    }

    // Client CRM CRUD
    fun addClientCrm(
        clientName: String,
        contact: String,
        project: String,
        budget: String,
        paymentStatus: String,
        deadline: String,
        notes: String,
        previousProjects: String,
        communicationHistory: String
    ) {
        viewModelScope.launch {
            val crm = ClientCrm(
                clientName = clientName,
                contact = contact,
                project = project,
                budget = budget,
                paymentStatus = paymentStatus,
                deadline = deadline,
                notes = notes,
                previousProjects = previousProjects,
                communicationHistory = communicationHistory
            )
            db.clientCrmDao().insertClient(crm)
        }
    }

    fun updateClientCrm(client: ClientCrm) {
        viewModelScope.launch {
            db.clientCrmDao().updateClient(client)
        }
    }

    fun deleteClientCrm(client: ClientCrm) {
        viewModelScope.launch {
            db.clientCrmDao().deleteClient(client)
        }
    }

    // User Profile Updates
    fun updateFullProfile(
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
    ) {
        _userProfile.value = _userProfile.value.copy(
            name = name,
            titleRole = titleRole,
            email = email,
            phone = phone,
            location = location,
            aboutMe = aboutMe,
            professionalBio = professionalBio,
            skills = skills,
            languages = languages,
            education = education,
            workExperience = workExperience,
            cvResumeInfo = cvResumeInfo,
            githubUrl = githubUrl,
            linkedinUrl = linkedinUrl,
            portfolioUrl = portfolioUrl,
            whatsappNumber = whatsappNumber,
            dailyTargetGoals = targetGoals
        )
    }

    fun updateSecuritySettings(
        appLockEnabled: Boolean,
        appLockPin: String,
        autoLockDuration: String
    ) {
        _userProfile.value = _userProfile.value.copy(
            appLockEnabled = appLockEnabled,
            appLockPin = appLockPin,
            autoLockDuration = autoLockDuration
        )
    }

    fun toggleSensitiveDataLock() {
        _userProfile.value = _userProfile.value.copy(
            sensitiveDataLocked = !_userProfile.value.sensitiveDataLocked
        )
    }

    fun resetAllData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            db.clearAllTables()
            // Reset to defaults
            _userProfile.value = UserProfile()
            Toast.makeText(context, "All data reset to clean state", Toast.LENGTH_SHORT).show()
        }
    }

    // Reminders & Scheduling
    fun triggerDailySummaryNow(context: Context) {
        val total = allTasks.value.size
        val completed = allTasks.value.count { it.isCompleted }
        val pending = total - completed
        val urgent = allTasks.value.count { !it.isCompleted && it.priority == Priority.HIGH }
        NotificationHelper.showDailySummary(
            context = context,
            totalTasks = total,
            completedTasks = completed,
            pendingTasks = pending,
            urgentTasks = urgent,
            quote = _userProfile.value.motto
        )
        Toast.makeText(context, "Daily summary notification sent!", Toast.LENGTH_SHORT).show()
    }

    fun triggerInstantTaskReminderTest(context: Context) {
        val highPriorityTask = allTasks.value.firstOrNull { !it.isCompleted && it.priority == Priority.HIGH }
            ?: allTasks.value.firstOrNull { !it.isCompleted }

        if (highPriorityTask != null) {
            NotificationHelper.showTaskReminder(
                context = context,
                taskId = highPriorityTask.id,
                title = highPriorityTask.title,
                description = highPriorityTask.description,
                priority = highPriorityTask.priority.name
            )
            Toast.makeText(context, "Task reminder alert sent!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No pending tasks found to remind", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateDailySummarySchedule(context: Context, morningHour: Int, morningMinute: Int, eveningHour: Int, eveningMinute: Int) {
        val morningStr = String.format("%02d:%02d", morningHour, morningMinute)
        val eveningStr = String.format("%02d:%02d", eveningHour, eveningMinute)

        _userProfile.value = _userProfile.value.copy(
            morningBriefingTime = morningStr,
            eveningReviewTime = eveningStr
        )

        ReminderScheduler.scheduleDailySummary(context, morningHour, morningMinute, 1001)
        ReminderScheduler.scheduleDailySummary(context, eveningHour, eveningMinute, 1002)

        Toast.makeText(context, "Updated daily reminders: $morningStr & $eveningStr", Toast.LENGTH_SHORT).show()
    }

    // Sync
    fun triggerCloudSync() {
        viewModelScope.launch {
            syncManager.performSync(db)
        }
    }

    fun setOfflineMode(enabled: Boolean) {
        syncManager.setOfflineMode(enabled)
    }

    suspend fun getExportJson(): String {
        return syncManager.createExportJson(db)
    }

    fun restoreBackup(json: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = syncManager.restoreFromJson(json, db)
            onComplete(result)
        }
    }
}
