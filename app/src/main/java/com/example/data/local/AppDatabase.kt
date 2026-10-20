package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ClientCrm
import com.example.data.model.ClientRequest
import com.example.data.model.ClientWebsite
import com.example.data.model.ExpenseRecord
import com.example.data.model.FavoriteApp
import com.example.data.model.GoalTask
import com.example.data.model.Habit
import com.example.data.model.IncomeRecord
import com.example.data.model.Priority
import com.example.data.model.VaultDocument
import com.example.data.model.WebsiteProject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        GoalTask::class,
        FavoriteApp::class,
        Habit::class,
        WebsiteProject::class,
        ClientRequest::class,
        ClientWebsite::class,
        IncomeRecord::class,
        ExpenseRecord::class,
        VaultDocument::class,
        ClientCrm::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalTaskDao(): GoalTaskDao
    abstract fun favoriteAppDao(): FavoriteAppDao
    abstract fun habitDao(): HabitDao
    abstract fun websiteProjectDao(): WebsiteProjectDao
    abstract fun clientRequestDao(): ClientRequestDao
    abstract fun clientWebsiteDao(): ClientWebsiteDao
    abstract fun financeDao(): FinanceDao
    abstract fun vaultDao(): VaultDao
    abstract fun clientCrmDao(): ClientCrmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "goalpulse_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getDatabase(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(db: AppDatabase) {
            val now = System.currentTimeMillis()
            val hourInMillis = 3600 * 1000L

            val initialGoals = listOf(
                GoalTask(
                    title = "Review Project Roadmap & Sprint Backlog",
                    description = "Prioritize top tasks, review feature requirements, and align timelines.",
                    category = "Work",
                    priority = Priority.HIGH,
                    dueDateMillis = now + 4 * hourInMillis,
                    reminderTimeMillis = now + 2 * hourInMillis,
                    isCompleted = false,
                    progress = 60,
                    isStarred = true
                ),
                GoalTask(
                    title = "Complete App Reminder & Sync Integration",
                    description = "Build intelligent local notification schedules and cloud sync logic.",
                    category = "Work",
                    priority = Priority.HIGH,
                    dueDateMillis = now + 6 * hourInMillis,
                    reminderTimeMillis = null,
                    isCompleted = true,
                    completionDateMillis = now - hourInMillis,
                    progress = 100,
                    isStarred = true
                ),
                GoalTask(
                    title = "Deploy Client E-commerce Website Updates",
                    description = "Optimize image loading, verify checkout flow, and push to production.",
                    category = "Work",
                    priority = Priority.HIGH,
                    dueDateMillis = now + 8 * hourInMillis,
                    reminderTimeMillis = now + 5 * hourInMillis,
                    isCompleted = false,
                    progress = 75,
                    isStarred = true
                )
            )

            val initialApps = listOf(
                FavoriteApp(
                    appName = "Chrome",
                    packageName = "com.android.chrome",
                    webFallbackUrl = "https://www.google.com",
                    iconCategory = "browser",
                    launchCount = 12,
                    isPinned = true
                ),
                FavoriteApp(
                    appName = "GitHub",
                    packageName = "com.github.android",
                    webFallbackUrl = "https://github.com",
                    iconCategory = "code",
                    launchCount = 20,
                    isPinned = true
                ),
                FavoriteApp(
                    appName = "YouTube",
                    packageName = "com.google.android.youtube",
                    webFallbackUrl = "https://youtube.com",
                    iconCategory = "video",
                    launchCount = 8,
                    isPinned = true
                ),
                FavoriteApp(
                    appName = "Gmail",
                    packageName = "com.google.android.gm",
                    webFallbackUrl = "https://mail.google.com",
                    iconCategory = "mail",
                    launchCount = 15,
                    isPinned = true
                )
            )

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(Date())
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)

            val initialHabits = listOf(
                Habit(
                    title = "Daily Coding & Architecture Study",
                    category = "Productivity",
                    iconType = "code",
                    colorHex = "#0284C7",
                    currentStreak = 7,
                    longestStreak = 14,
                    lastCompletedDate = todayStr,
                    completedDatesCsv = "$yesterdayStr,$todayStr"
                ),
                Habit(
                    title = "Review Client Inquiries & Updates",
                    category = "Work",
                    iconType = "fire",
                    colorHex = "#F97316",
                    currentStreak = 5,
                    longestStreak = 10,
                    lastCompletedDate = todayStr,
                    completedDatesCsv = "$yesterdayStr,$todayStr"
                ),
                Habit(
                    title = "Drink 2.5L Water & Hydrate",
                    category = "Health",
                    iconType = "water",
                    colorHex = "#06B6D4",
                    currentStreak = 4,
                    longestStreak = 9,
                    lastCompletedDate = yesterdayStr,
                    completedDatesCsv = yesterdayStr
                )
            )

            val initialWebsites = listOf(
                WebsiteProject(
                    title = "TechSphere IT Agency",
                    clientName = "Ahmad Enterprises",
                    liveUrl = "https://example.com/techsphere",
                    techStack = "Next.js 14, Tailwind CSS, TypeScript",
                    status = "Live",
                    description = "Modern corporate IT consultancy portal with responsive showcase, dynamic pricing, and appointment booking.",
                    updatedAt = now - 2 * hourInMillis
                ),
                WebsiteProject(
                    title = "Gourmet Bites Restaurant",
                    clientName = "Cafe Milano",
                    liveUrl = "https://example.com/gourmet-bites",
                    techStack = "React, Firebase, Tailwind",
                    status = "Live",
                    description = "Online culinary showcase with interactive menu, online table reservation, and customer review feed.",
                    updatedAt = now - 24 * hourInMillis
                ),
                WebsiteProject(
                    title = "Nexus Fashion E-Commerce",
                    clientName = "StyleCraft Ltd",
                    liveUrl = "https://example.com/nexus-store",
                    techStack = "Next.js, Redux, bKash Gateway",
                    status = "In Development",
                    description = "Multi-vendor responsive apparel shopping portal with mobile checkout and instant SMS order tracking.",
                    updatedAt = now - 48 * hourInMillis
                )
            )

            val initialClientWebsites = listOf(
                ClientWebsite(
                    websiteName = "TechSphere IT Portal",
                    clientName = "Ahmad Enterprises",
                    url = "https://example.com/techsphere",
                    status = "Live",
                    lastUpdateTime = now - 2 * hourInMillis,
                    techStack = "Next.js 14, Tailwind CSS",
                    description = "Corporate consulting portal with client appointment booking."
                ),
                ClientWebsite(
                    websiteName = "Gourmet Bites Online",
                    clientName = "Cafe Milano",
                    url = "https://example.com/gourmet-bites",
                    status = "Live",
                    lastUpdateTime = now - 12 * hourInMillis,
                    techStack = "React, Firebase",
                    description = "Digital menu, table reservation, and food ordering portal."
                ),
                ClientWebsite(
                    websiteName = "Nexus Fashion Shop",
                    clientName = "StyleCraft Ltd",
                    url = "https://example.com/nexus-store",
                    status = "In Development",
                    lastUpdateTime = now - 24 * hourInMillis,
                    techStack = "Next.js, Tailwind, bKash API",
                    description = "Fullstack e-commerce store with mobile inventory management."
                ),
                ClientWebsite(
                    websiteName = "CarePlus Dental Care",
                    clientName = "Dr. Rahman",
                    url = "https://example.com/careplus-clinic",
                    status = "Under Review",
                    lastUpdateTime = now - 48 * hourInMillis,
                    techStack = "WordPress, Elementor Pro",
                    description = "Clinic appointment scheduling and patient reports."
                )
            )

            val initialRequests = listOf(
                ClientRequest(
                    clientName = "Tanvir Hasan",
                    clientEmail = "tanvir.hasan@gmail.com",
                    clientPhone = "+880 1712-345678",
                    projectType = "E-Commerce Website",
                    budget = "৳45,000",
                    message = "আমি একটি অনলাইন ক্লথিং ব্র্যান্ডের জন্য ফুলস্ট্যাক ওয়েবসাইট তৈরি করতে চাই। bKash ও Nagad পেমেন্ট গেটওয়ে এবং প্রোডাক্ট ইনভেন্টরি ড্যাশবোর্ড প্রয়োজন। কতদিনে কাজ সম্পন্ন করা সম্ভব?",
                    status = "New Request",
                    isStarred = true,
                    receivedAt = now - 1 * hourInMillis
                ),
                ClientRequest(
                    clientName = "Sophia Miller",
                    clientEmail = "sophia@greenleaf.co",
                    clientPhone = "+1 (555) 234-5678",
                    projectType = "Corporate Agency Website",
                    budget = "$650 USD",
                    message = "We are seeking a high-performance modern website for our sustainable consulting agency. Need smooth scrolling, case studies filter, and interactive contact forms.",
                    status = "In Discussion",
                    isStarred = true,
                    receivedAt = now - 5 * hourInMillis
                )
            )

            val initialIncomes = listOf(
                IncomeRecord(
                    clientName = "Ahmad Enterprises",
                    projectName = "TechSphere IT Agency Website",
                    amount = 35000.0,
                    paymentStatus = "Paid",
                    paymentDate = "2026-10-15",
                    notes = "Final milestone released via Bank Transfer"
                ),
                IncomeRecord(
                    clientName = "Cafe Milano",
                    projectName = "Gourmet Bites Food Portal",
                    amount = 28000.0,
                    paymentStatus = "Paid",
                    paymentDate = "2026-10-18",
                    notes = "Full payment received upon deployment"
                ),
                IncomeRecord(
                    clientName = "StyleCraft Ltd",
                    projectName = "Nexus Fashion E-Commerce",
                    amount = 45000.0,
                    paymentStatus = "Due",
                    paymentDate = "2026-10-28",
                    notes = "Advance 50% paid, final 50% due on delivery"
                )
            )

            val initialExpenses = listOf(
                ExpenseRecord(
                    category = "Hosting",
                    title = "Vercel Pro & VPS Production Server",
                    amount = 2500.0,
                    date = "2026-10-01",
                    notes = "Monthly cloud server for client sites"
                ),
                ExpenseRecord(
                    category = "Domain",
                    title = "Porkbun & Namecheap Client Domains (.dev & .com)",
                    amount = 1800.0,
                    date = "2026-10-03",
                    notes = "Renewals for portfolio and client staging domains"
                ),
                ExpenseRecord(
                    category = "Software",
                    title = "JetBrains IDE & Figma Professional",
                    amount = 3200.0,
                    date = "2026-10-05",
                    notes = "Design and coding suite subscription"
                ),
                ExpenseRecord(
                    category = "API",
                    title = "OpenAI & Resend Transactional Email API",
                    amount = 1400.0,
                    date = "2026-10-10",
                    notes = "AI and automated mail services"
                ),
                ExpenseRecord(
                    category = "Ads",
                    title = "Meta & Google Developer Showcase Ads",
                    amount = 2000.0,
                    date = "2026-10-12",
                    notes = "Client acquisition campaign"
                ),
                ExpenseRecord(
                    category = "Hardware",
                    title = "Keychron Mechanical Keyboard & 1TB NVMe SSD",
                    amount = 8500.0,
                    date = "2026-10-14",
                    notes = "Development workstation hardware upgrade"
                ),
                ExpenseRecord(
                    category = "অন্যান্য",
                    title = "Co-working space & Coffee passes",
                    amount = 1200.0,
                    date = "2026-10-16",
                    notes = "Client meeting overheads"
                )
            )

            val initialVaultDocs = listOf(
                VaultDocument(
                    category = "CV",
                    title = "Shihab_Islam_FullStack_Resume_2026.pdf",
                    identifierOrNumber = "VER-2026.1",
                    issueDate = "2026-01-10",
                    notes = "Updated with Kotlin, Android Jetpack Compose & Next.js 14 projects"
                ),
                VaultDocument(
                    category = "Certificates",
                    title = "Google Cloud Certified Associate Engineer",
                    identifierOrNumber = "GCP-8839210",
                    issueDate = "2025-06-12",
                    expiryDate = "2028-06-12",
                    notes = "Cloud architecture and deployment credentials"
                ),
                VaultDocument(
                    category = "ID documents",
                    title = "National ID Card (NID)",
                    identifierOrNumber = "NID-7392819203",
                    issueDate = "2022-04-10",
                    isSensitiveLocked = true,
                    notes = "Government Smart NID"
                ),
                VaultDocument(
                    category = "Passport information",
                    title = "Republic of Bangladesh Passport",
                    identifierOrNumber = "A09283741",
                    issueDate = "2022-11-20",
                    expiryDate = "2032-11-20",
                    isSensitiveLocked = true,
                    notes = "Passport valid for international client contracts"
                ),
                VaultDocument(
                    category = "Contracts",
                    title = "Master Service Agreement - TechSphere IT",
                    identifierOrNumber = "CTR-2026-03",
                    issueDate = "2026-03-01",
                    notes = "IP assignment and NDA clause"
                ),
                VaultDocument(
                    category = "Project documents",
                    title = "Nexus E-Commerce Architecture & API Spec",
                    identifierOrNumber = "PRJ-NX-01",
                    issueDate = "2026-09-15",
                    notes = "Database schema and webhook specifications"
                ),
                VaultDocument(
                    category = "Important PDFs",
                    title = "Tax Return & TIN Certificate 2025-2026",
                    identifierOrNumber = "TIN-4829103948",
                    issueDate = "2025-11-15",
                    isSensitiveLocked = true,
                    notes = "Official tax assessment receipt"
                ),
                VaultDocument(
                    category = "Receipts",
                    title = "Annual Server & Cloud Invoices Bundle",
                    identifierOrNumber = "INV-2026-AWS",
                    issueDate = "2026-10-01",
                    notes = "Hosting receipts for expense tax deduction"
                )
            )

            val initialCrmClients = listOf(
                ClientCrm(
                    clientName = "Tanvir Hasan",
                    contact = "tanvir.hasan@gmail.com • +880 1712-345678",
                    project = "E-Commerce Website",
                    budget = "৳45,000",
                    paymentStatus = "In Discussion",
                    deadline = "2026-11-20",
                    notes = "bKash & Nagad gateway integration required. Mobile-first design priority.",
                    previousProjects = "None (First-time client)",
                    communicationHistory = "Discussed requirements via WhatsApp on Oct 19. Sent quotation."
                ),
                ClientCrm(
                    clientName = "Ahmad Enterprises",
                    contact = "ahmad@techsphere.com • +880 1819-223344",
                    project = "Corporate Consulting Portal",
                    budget = "৳35,000",
                    paymentStatus = "Paid",
                    deadline = "2026-10-15",
                    notes = "Delivered on schedule. Next step is monthly maintenance retainer.",
                    previousProjects = "TechSphere IT v1 landing page (2024)",
                    communicationHistory = "Sprint demo accepted on Oct 14. Final invoice cleared on Oct 15."
                ),
                ClientCrm(
                    clientName = "Sophia Miller",
                    contact = "sophia@greenleaf.co • +1 (555) 234-5678",
                    project = "Sustainable Consulting Agency",
                    budget = "$650 USD",
                    paymentStatus = "In Discussion",
                    deadline = "2026-11-30",
                    notes = "Global green consultancy firm. Needs case study CMS and smooth animations.",
                    previousProjects = "Referred by Dr. Rahman",
                    communicationHistory = "Initial discovery call scheduled for Thursday 8 PM BST."
                )
            )

            db.goalTaskDao().insertTasks(initialGoals)
            db.favoriteAppDao().insertApps(initialApps)
            db.habitDao().insertHabits(initialHabits)
            db.websiteProjectDao().insertProjects(initialWebsites)
            db.clientRequestDao().insertRequests(initialRequests)
            db.clientWebsiteDao().insertWebsites(initialClientWebsites)
            db.financeDao().insertIncomes(initialIncomes)
            db.financeDao().insertExpenses(initialExpenses)
            db.vaultDao().insertDocuments(initialVaultDocs)
            db.clientCrmDao().insertClients(initialCrmClients)
        }
    }
}
