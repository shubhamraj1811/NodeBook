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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.node.book.ui.theme.*

// ─── Background color options ──────────────────────────────
val noteColorOptions = listOf(
    "default" to Color.Transparent,
    "#FFF9C4" to NoteColorYellow,
    "#C8E6C9" to NoteColorGreen,
    "#BBDEFB" to NoteColorBlue,
    "#F8BBD0" to NoteColorPink,
    "#E1BEE7" to NoteColorPurple,
    "#FFE0B2" to NoteColorOrange,
    "#F5F5F5" to NoteColorGray,
)

// ─── Text color options ────────────────────────────────────
val textColorOptions = listOf(
    "default" to Color.Transparent,
    "#000000" to TextColorBlack,
    "#FFFFFF" to TextColorWhite,
    "#007AFF" to TextColorBlue,
    "#FF3B30" to TextColorRed,
    "#34C759" to TextColorGreen,
    "#FFD60A" to TextColorYellow,
    "#FF2D55" to TextColorPink,
    "#AF52DE" to TextColorPurple,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormattingBottomSheet(
    isBold: Boolean,
    isItalic: Boolean,
    textSize: Float,
    selectedBgColor: String,
    selectedTextColor: String,
    onBoldToggle: () -> Unit,
    onItalicToggle: () -> Unit,
    onTextSizeIncrease: () -> Unit,
    onTextSizeDecrease: () -> Unit,
    onBgColorSelected: (String) -> Unit,
    onTextColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {

            // ─── Sheet Title ──────────────────────────────
            Text(
                text = "Text & Style",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // ─── SECTION: Text Style ──────────────────────
            SheetSectionLabel("TEXT STYLE")
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bold
                FormatChip(
                    label = "B",
                    isActive = isBold,
                    onClick = onBoldToggle,
                    isBold = true
                )

                // Italic
                FormatChip(
                    label = "I",
                    isActive = isItalic,
                    onClick = onItalicToggle,
                    isItalic = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                // H1
                FormatChip(
                    label = "H1",
                    isActive = textSize >= 28f,
                    onClick = { onTextSizeDecrease() /* will be overridden */ }
                )

                // H2
                FormatChip(
                    label = "H2",
                    isActive = textSize in 22f..27f,
                    onClick = { onTextSizeDecrease() }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // A- and A+ with size display
                IconButton(
                    onClick = onTextSizeDecrease,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        Icons.Default.TextDecrease,
                        contentDescription = "A-",
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${textSize.toInt()}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 10.dp
                        )
                    )
                }

                IconButton(
                    onClick = onTextSizeIncrease,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        Icons.Default.TextIncrease,
                        contentDescription = "A+",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(20.dp))

            // ─── SECTION: Text Color ──────────────────────
            SheetSectionLabel("TEXT COLOR")
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                textColorOptions.forEach { (hex, color) ->
                    ColorCircle(
                        color = color,
                        isSelected = selectedTextColor == hex,
                        isDefault = hex == "default",
                        defaultIcon = Icons.Default.FormatColorText,
                        onClick = { onTextColorSelected(hex) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(20.dp))

            // ─── SECTION: Background Color ────────────────
            SheetSectionLabel("BACKGROUND COLOR")
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                noteColorOptions.forEach { (hex, color) ->
                    ColorCircle(
                        color = color,
                        isSelected = selectedBgColor == hex,
                        isDefault = hex == "default",
                        defaultIcon = Icons.Default.FormatColorFill,
                        onClick = { onBgColorSelected(hex) }
                    )
                }
            }
        }
    }
}

// ─── Section label ────────────────────────────────────────
@Composable
private fun SheetSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.sp
    )
}

// ─── Format chip (Bold / Italic / H1 / H2) ───────────────
@Composable
private fun FormatChip(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    isBold: Boolean = false,
    isItalic: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isActive)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else
            MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .height(40.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isBold || isActive) FontWeight.Bold
                    else FontWeight.Normal,
                    color = if (isActive)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

// ─── Color circle ─────────────────────────────────────────
@Composable
fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    isDefault: Boolean = false,
    defaultIcon: androidx.compose.ui.graphics.vector.ImageVector =
        Icons.Default.Circle,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (isDefault) MaterialTheme.colorScheme.surfaceVariant
                else color
            )
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isDefault) {
            Icon(
                imageVector = defaultIcon,
                contentDescription = "Default",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}