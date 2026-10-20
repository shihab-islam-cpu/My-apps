package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ClientWebsite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClientWebsiteViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.clientWebsiteDao()

    // Observable stream of all client websites sorted by lastUpdateTime DESC
    val allWebsites: StateFlow<List<ClientWebsite>> = dao.getAllClientWebsites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("All")
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    // Filtered stream by search query and status filter
    val filteredWebsites: StateFlow<List<ClientWebsite>> = combine(
        allWebsites,
        searchQuery,
        selectedStatusFilter
    ) { websites, query, status ->
        websites.filter { site ->
            val matchesQuery = query.isBlank() ||
                    site.websiteName.contains(query, ignoreCase = true) ||
                    site.clientName.contains(query, ignoreCase = true) ||
                    site.url.contains(query, ignoreCase = true) ||
                    site.techStack.contains(query, ignoreCase = true)

            val matchesStatus = status == "All" || site.status.equals(status, ignoreCase = true)

            matchesQuery && matchesStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Summary counters
    val liveCount: StateFlow<Int> = allWebsites.map { list ->
        list.count { it.status.equals("Live", ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val inDevelopmentCount: StateFlow<Int> = allWebsites.map { list ->
        list.count { it.status.equals("In Development", ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: String) {
        _selectedStatusFilter.value = status
    }

    fun addClientWebsite(
        websiteName: String,
        clientName: String,
        url: String,
        status: String = "Live",
        techStack: String = "Next.js, Tailwind CSS",
        description: String = "",
        maintenanceNotes: String = ""
    ) {
        viewModelScope.launch {
            val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else url

            val website = ClientWebsite(
                websiteName = websiteName.trim(),
                clientName = clientName.trim(),
                url = formattedUrl.trim(),
                status = status,
                lastUpdateTime = System.currentTimeMillis(),
                techStack = techStack.trim(),
                description = description.trim(),
                monthlyMaintenanceNotes = maintenanceNotes.trim()
            )
            dao.insertWebsite(website)
        }
    }

    fun updateClientWebsite(website: ClientWebsite) {
        viewModelScope.launch {
            val updated = website.copy(
                lastUpdateTime = System.currentTimeMillis()
            )
            dao.updateWebsite(updated)
        }
    }

    fun updateWebsiteStatus(id: Long, newStatus: String) {
        viewModelScope.launch {
            dao.updateStatus(id, newStatus, System.currentTimeMillis())
        }
    }

    fun deleteClientWebsite(website: ClientWebsite) {
        viewModelScope.launch {
            dao.deleteWebsite(website)
        }
    }

    fun openWebsite(context: Context, url: String) {
        try {
            val fullUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else url
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open browser for $url", Toast.LENGTH_SHORT).show()
        }
    }
}
