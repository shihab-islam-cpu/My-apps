package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun EditFullProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onConfirm: (
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
    ) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var titleRole by remember { mutableStateOf(currentProfile.titleRole) }
    var email by remember { mutableStateOf(currentProfile.email) }
    var phone by remember { mutableStateOf(currentProfile.phone) }
    var location by remember { mutableStateOf(currentProfile.location) }
    var aboutMe by remember { mutableStateOf(currentProfile.aboutMe) }
    var professionalBio by remember { mutableStateOf(currentProfile.professionalBio) }
    var skills by remember { mutableStateOf(currentProfile.skills) }
    var languages by remember { mutableStateOf(currentProfile.languages) }
    var education by remember { mutableStateOf(currentProfile.education) }
    var workExperience by remember { mutableStateOf(currentProfile.workExperience) }
    var cvResumeInfo by remember { mutableStateOf(currentProfile.cvResumeInfo) }
    var githubUrl by remember { mutableStateOf(currentProfile.githubUrl) }
    var linkedinUrl by remember { mutableStateOf(currentProfile.linkedinUrl) }
    var portfolioUrl by remember { mutableStateOf(currentProfile.portfolioUrl) }
    var whatsappNumber by remember { mutableStateOf(currentProfile.whatsappNumber) }
    var targetGoalsText by remember { mutableStateOf(currentProfile.dailyTargetGoals.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("প্রোফাইল আপডেট করুন (Edit Profile) 👤", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name (নিজের নাম)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titleRole,
                    onValueChange = { titleRole = it },
                    label = { Text("Title / Role") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_role")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aboutMe,
                    onValueChange = { aboutMe = it },
                    label = { Text("About Me (আমার সম্পর্কে)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_about")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (লোকেশন — যতটুকু দরকার)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_location")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (ইমেইল)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_email")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone / WhatsApp (ফোন)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_phone")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = skills,
                    onValueChange = { skills = it },
                    label = { Text("Skills (দক্ষতা — কমা দিয়ে আলাদা করুন)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_skills")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = languages,
                    onValueChange = { languages = it },
                    label = { Text("Languages (ভাষা)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_languages")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = education,
                    onValueChange = { education = it },
                    label = { Text("Education (শিক্ষাগত যোগ্যতা)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_education")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = workExperience,
                    onValueChange = { workExperience = it },
                    label = { Text("Work Experience (কাজের অভিজ্ঞতা)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_experience")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cvResumeInfo,
                    onValueChange = { cvResumeInfo = it },
                    label = { Text("CV / Resume (ডকুমেন্ট রেফারেন্স / লিংক)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_cv")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = professionalBio,
                    onValueChange = { professionalBio = it },
                    label = { Text("Professional Bio (পেশাদার বিবরণ)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_bio")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = githubUrl,
                    onValueChange = { githubUrl = it },
                    label = { Text("GitHub Link") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_github")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = linkedinUrl,
                    onValueChange = { linkedinUrl = it },
                    label = { Text("LinkedIn Link") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_linkedin")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = portfolioUrl,
                    onValueChange = { portfolioUrl = it },
                    label = { Text("Portfolio Website Link") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prof_portfolio")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetGoalsText.toIntOrNull() ?: currentProfile.dailyTargetGoals
                    onConfirm(
                        name.trim(),
                        titleRole.trim(),
                        email.trim(),
                        phone.trim(),
                        location.trim(),
                        aboutMe.trim(),
                        professionalBio.trim(),
                        skills.trim(),
                        languages.trim(),
                        education.trim(),
                        workExperience.trim(),
                        cvResumeInfo.trim(),
                        githubUrl.trim(),
                        linkedinUrl.trim(),
                        portfolioUrl.trim(),
                        whatsappNumber.trim(),
                        target
                    )
                },
                modifier = Modifier.testTag("btn_confirm_edit_profile")
            ) {
                Text("আপডেট করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}
