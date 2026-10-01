package com.example.bodysurfaceareacalculator.ui.screens.settings

import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodysurfaceareacalculator.data.model.AppSettings
import com.example.bodysurfaceareacalculator.data.model.AppThemeMode
import com.example.bodysurfaceareacalculator.data.model.BsaFormula
import com.example.bodysurfaceareacalculator.data.model.HeightUnit
import com.example.bodysurfaceareacalculator.data.model.WeightUnit
import com.example.bodysurfaceareacalculator.ui.components.BsaSegmentedControl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onThemeModeChanged: (AppThemeMode) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    onDefaultHeightUnitChanged: (HeightUnit) -> Unit,
    onDefaultWeightUnitChanged: (WeightUnit) -> Unit,
    onDefaultFormulaChanged: (BsaFormula) -> Unit,
    onShowFormulaComparisonChanged: (Boolean) -> Unit,
    onHapticFeedbackChanged: (Boolean) -> Unit,
    onSoundEffectsChanged: (Boolean) -> Unit,
    onResetSettings: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetSettingsDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section 1: Appearance
            SettingsSectionHeader(title = "APPEARANCE", icon = Icons.Outlined.Palette)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Theme Mode",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BsaSegmentedControl(
                        items = listOf(AppThemeMode.SYSTEM, AppThemeMode.LIGHT, AppThemeMode.DARK),
                        selectedItem = settings.themeMode,
                        onItemSelected = onThemeModeChanged,
                        itemLabel = { it.displayName },
                        contentDescriptionPrefix = "Theme mode"
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Dynamic Color",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Use colors derived from wallpaper palette",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = settings.dynamicColor,
                                onCheckedChange = onDynamicColorChanged,
                                modifier = Modifier.semantics {
                                    contentDescription = "Dynamic color toggle"
                                }
                            )
                        }
                    }
                }
            }

            // Section 2: Calculation Defaults
            SettingsSectionHeader(title = "CALCULATION DEFAULTS", icon = Icons.Outlined.Tune)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Default Height Unit",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BsaSegmentedControl(
                        items = listOf(HeightUnit.CENTIMETERS, HeightUnit.FEET_INCHES),
                        selectedItem = settings.defaultHeightUnit,
                        onItemSelected = onDefaultHeightUnitChanged,
                        itemLabel = { it.label },
                        contentDescriptionPrefix = "Default height unit"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )

                    Text(
                        text = "Default Weight Unit",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BsaSegmentedControl(
                        items = listOf(WeightUnit.KILOGRAMS, WeightUnit.POUNDS),
                        selectedItem = settings.defaultWeightUnit,
                        onItemSelected = onDefaultWeightUnitChanged,
                        itemLabel = { it.label },
                        contentDescriptionPrefix = "Default weight unit"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )

                    Text(
                        text = "Default Formula",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BsaSegmentedControl(
                        items = listOf(BsaFormula.MOSTELLER, BsaFormula.DU_BOIS),
                        selectedItem = settings.defaultFormula,
                        onItemSelected = onDefaultFormulaChanged,
                        itemLabel = { it.displayName },
                        contentDescriptionPrefix = "Default formula"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Show Formula Comparison",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Display alternative formula estimate side-by-side",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.showFormulaComparison,
                            onCheckedChange = onShowFormulaComparisonChanged,
                            modifier = Modifier.semantics {
                                contentDescription = "Show formula comparison toggle"
                            }
                        )
                    }
                }
            }

            // Section 3: Feedback
            SettingsSectionHeader(title = "FEEDBACK & AUDIO", icon = Icons.Outlined.Vibration)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Haptic Feedback",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Subtle tactile response for calculations and actions",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.hapticFeedback,
                            onCheckedChange = onHapticFeedbackChanged,
                            modifier = Modifier.semantics {
                                contentDescription = "Haptic feedback toggle"
                            }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sound Effects",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Subtle click tone upon calculation and save",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.soundEffects,
                            onCheckedChange = onSoundEffectsChanged,
                            modifier = Modifier.semantics {
                                contentDescription = "Sound effects toggle"
                            }
                        )
                    }
                }
            }

            // Section 4: Data Management
            SettingsSectionHeader(title = "DATA MANAGEMENT", icon = Icons.Outlined.DeleteSweep)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showResetSettingsDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Reset settings to defaults"
                            },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RestartAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Reset Settings to Default")
                    }

                    OutlinedButton(
                        onClick = { showClearHistoryDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Clear all calculation history"
                            },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clear Calculation History",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Dialogs
        if (showResetSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showResetSettingsDialog = false },
                title = { Text("Reset Settings") },
                text = { Text("Reset all preferences and calculation defaults to factory values? Calculation history will be kept.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showResetSettingsDialog = false
                            onResetSettings()
                        }
                    ) {
                        Text("Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetSettingsDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showClearHistoryDialog) {
            AlertDialog(
                onDismissRequest = { showClearHistoryDialog = false },
                title = { Text("Clear All History") },
                text = { Text("Are you sure you want to delete all saved calculations? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearHistoryDialog = false
                            onClearHistory()
                        }
                    ) {
                        Text("Clear All", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearHistoryDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
