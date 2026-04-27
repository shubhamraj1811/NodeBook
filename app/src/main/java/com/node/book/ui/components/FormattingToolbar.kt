package com.node.book.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.node.book.ui.theme.*

// ─── Note background color options ────────────────────────
val noteColorOptions = listOf(
    "#FFFFFF" to NoteColorWhite,
    "#FFF9C4" to NoteColorYellow,
    "#C8E6C9" to NoteColorGreen,
    "#BBDEFB" to NoteColorBlue,
    "#F8BBD0" to NoteColorPink,
    "#E1BEE7" to NoteColorPurple,
    "#FFE0B2" to NoteColorOrange,
    "#F5F5F5" to NoteColorGray,
)

// ─── Formatting Toolbar ────────────────────────────────
@Composable
fun FormattingToolbar(
    isBold: Boolean,
    isItalic: Boolean,
    textSize: Float,
    selectedColor: String,
    onBoldToggle: () -> Unit,
    onItalicToggle: () -> Unit,
    onTextSizeIncrease: () -> Unit,
    onTextSizeDecrease: () -> Unit,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {

            // ─── Row 1: Text Formatting Buttons ───────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Bold
                FormatButton(
                    icon = Icons.Default.FormatBold,
                    label = "Bold",
                    isActive = isBold,
                    onClick = onBoldToggle
                )

                // Italic
                FormatButton(
                    icon = Icons.Default.FormatItalic,
                    label = "Italic",
                    isActive = isItalic,
                    onClick = onItalicToggle
                )

                VerticalDivider(
                    modifier = Modifier.height(28.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // Text size decrease
                FormatButton(
                    icon = Icons.Default.TextDecrease,
                    label = "Decrease",
                    isActive = false,
                    onClick = onTextSizeDecrease
                )

                // Current text size indicator
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${textSize.toInt()}sp",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Text size increase
                FormatButton(
                    icon = Icons.Default.TextIncrease,
                    label = "Increase",
                    isActive = false,
                    onClick = onTextSizeIncrease
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            // ─── Row 2: Background Color Picker ───────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Color:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                noteColorOptions.forEach { (hex, color) ->
                    ColorCircle(
                        color = color,
                        isSelected = selectedColor == hex,
                        onClick = { onColorSelected(hex) }
                    )
                }
            }
        }
    }
}

// ─── Reusable Format Toggle Button ────────────────────────
@Composable
private fun FormatButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isActive)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    else
        Color.Transparent

    val iconTint = if (isActive)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ─── Color Circle Selector ────────────────────────────────
@Composable
private fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable { onClick() }
    )
}