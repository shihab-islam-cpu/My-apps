package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FavoriteApp

@Composable
fun FavoriteAppChip(
    app: FavoriteApp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, tintColor, bgColor) = when (app.iconCategory.lowercase()) {
        "browser" -> Triple(Icons.Default.Language, Color(0xFF0284C7), Color(0xFFE0F2FE))
        "video" -> Triple(Icons.Default.PlayArrow, Color(0xFFDC2626), Color(0xFFFEE2E2))
        "mail" -> Triple(Icons.Default.Email, Color(0xFFEA4335), Color(0xFFFEE2E2))
        "notes" -> Triple(Icons.Default.EditNote, Color(0xFFD97706), Color(0xFFFEF3C7))
        "calendar" -> Triple(Icons.Default.CalendarToday, Color(0xFF2563EB), Color(0xFFDBEAFE))
        "calculator" -> Triple(Icons.Default.Calculate, Color(0xFF059669), Color(0xFFD1FAE5))
        "chat" -> Triple(Icons.Default.Chat, Color(0xFF16A34A), Color(0xFFDCFCE7))
        "code" -> Triple(Icons.Default.Code, Color(0xFF7C3AED), Color(0xFFEDE9FE))
        else -> Triple(Icons.Default.Apps, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .width(76.dp)
            .clickable { onClick() }
            .testTag("app_chip_${app.appName.lowercase()}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = bgColor,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = app.appName,
                    tint = tintColor,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Text(
                text = app.appName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
