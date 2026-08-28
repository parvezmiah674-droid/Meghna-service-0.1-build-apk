package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryContainer
import com.example.ui.theme.BrandOnPrimaryContainer

fun getCategoryIcon(category: String): ImageVector {
    val lower = category.lowercase()
    return when {
        lower.contains("ইলেকট্রিক") || lower.contains("বিদ্যুৎ") || lower.contains("electric") -> Icons.Default.Bolt
        lower.contains("সিসি") || lower.contains("ক্যামেরা") || lower.contains("camera") -> Icons.Default.Videocam
        lower.contains("সোলার") || lower.contains("আইপিএস") || lower.contains("solar") || lower.contains("ips") -> Icons.Default.SolarPower
        lower.contains("এসি") || lower.contains("ফ্রিজ") || lower.contains("ac") || lower.contains("fridge") -> Icons.Default.AcUnit
        lower.contains("পেইন্ট") || lower.contains("রং") || lower.contains("paint") -> Icons.Default.FormatPaint
        else -> Icons.Default.Build
    }
}

@Composable
fun CategoryBadge(
    category: String,
    modifier: Modifier = Modifier,
    statusText: String = "সক্রিয়",
    isHighlighted: Boolean = false
) {
    val containerBg = if (isHighlighted) BrandPrimaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isHighlighted) BrandOnPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = containerBg,
        modifier = modifier
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun TechnicianAvatar(
    name: String,
    category: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(BrandPrimary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = getCategoryIcon(category),
            contentDescription = "সার্ভিস আইকন",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

