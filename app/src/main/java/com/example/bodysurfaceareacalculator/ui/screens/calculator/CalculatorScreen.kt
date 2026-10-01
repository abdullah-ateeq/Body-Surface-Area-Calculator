package com.example.bodysurfaceareacalculator.ui.screens.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bodysurfaceareacalculator.data.model.BsaFormula
import com.example.bodysurfaceareacalculator.data.model.HeightUnit
import com.example.bodysurfaceareacalculator.data.model.WeightUnit
import com.example.bodysurfaceareacalculator.ui.components.BsaDisclaimerCard
import com.example.bodysurfaceareacalculator.ui.components.BsaResultCard
import com.example.bodysurfaceareacalculator.ui.components.BsaSegmentedControl
import com.example.bodysurfaceareacalculator.ui.viewmodel.CalculatorUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    onHeightUnitChanged: (HeightUnit) -> Unit,
    onHeightCmChanged: (String) -> Unit,
    onHeightFeetChanged: (String) -> Unit,
    onHeightInchesChanged: (String) -> Unit,
    onWeightUnitChanged: (WeightUnit) -> Unit,
    onWeightChanged: (String) -> Unit,
    onFormulaChanged: (BsaFormula) -> Unit,
    onCalculateClicked: () -> Unit,
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BODY SURFACE AREA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Calculate estimated BSA from height and weight.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onResetClicked()
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Reset calculator inputs"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RestartAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWide = maxWidth > 600.dp

            if (isWide) {
                // Tablet / Landscape two-column layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        HeightInputSection(
                            heightUnit = uiState.heightUnit,
                            heightCm = uiState.heightCmInput,
                            heightFeet = uiState.heightFeetInput,
                            heightInches = uiState.heightInchesInput,
                            error = uiState.heightError,
                            onUnitChanged = onHeightUnitChanged,
                            onCmChanged = onHeightCmChanged,
                            onFeetChanged = onHeightFeetChanged,
                            onInchesChanged = onHeightInchesChanged
                        )

                        WeightInputSection(
                            weightUnit = uiState.weightUnit,
                            weight = uiState.weightInput,
                            error = uiState.weightError,
                            onUnitChanged = onWeightUnitChanged,
                            onWeightChanged = onWeightChanged
                        )

                        FormulaSection(
                            formula = uiState.formula,
                            onFormulaChanged = onFormulaChanged
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        val heightDisplay = if (uiState.heightUnit == HeightUnit.CENTIMETERS) {
                            if (uiState.heightCmInput.isNotEmpty()) "${uiState.heightCmInput} cm" else ""
                        } else {
                            if (uiState.heightFeetInput.isNotEmpty() || uiState.heightInchesInput.isNotEmpty()) {
                                "${uiState.heightFeetInput} ft ${uiState.heightInchesInput} in"
                            } else ""
                        }

                        val weightDisplay = if (uiState.weightInput.isNotEmpty()) {
                            "${uiState.weightInput} ${if (uiState.weightUnit == WeightUnit.KILOGRAMS) "kg" else "lb"}"
                        } else ""

                        BsaResultCard(
                            result = uiState.liveResult,
                            heightDisplay = heightDisplay,
                            weightDisplay = weightDisplay
                        )

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onCalculateClicked()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .semantics {
                                    contentDescription = "Calculate body surface area"
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Calculate,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CALCULATE",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        BsaDisclaimerCard()
                    }
                }
            } else {
                // Phone portrait single-column scrollable layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    HeightInputSection(
                        heightUnit = uiState.heightUnit,
                        heightCm = uiState.heightCmInput,
                        heightFeet = uiState.heightFeetInput,
                        heightInches = uiState.heightInchesInput,
                        error = uiState.heightError,
                        onUnitChanged = onHeightUnitChanged,
                        onCmChanged = onHeightCmChanged,
                        onFeetChanged = onHeightFeetChanged,
                        onInchesChanged = onHeightInchesChanged
                    )

                    WeightInputSection(
                        weightUnit = uiState.weightUnit,
                        weight = uiState.weightInput,
                        error = uiState.weightError,
                        onUnitChanged = onWeightUnitChanged,
                        onWeightChanged = onWeightChanged
                    )

                    FormulaSection(
                        formula = uiState.formula,
                        onFormulaChanged = onFormulaChanged
                    )

                    val heightDisplay = if (uiState.heightUnit == HeightUnit.CENTIMETERS) {
                        if (uiState.heightCmInput.isNotEmpty()) "${uiState.heightCmInput} cm" else ""
                    } else {
                        if (uiState.heightFeetInput.isNotEmpty() || uiState.heightInchesInput.isNotEmpty()) {
                            "${uiState.heightFeetInput} ft ${uiState.heightInchesInput} in"
                        } else ""
                    }

                    val weightDisplay = if (uiState.weightInput.isNotEmpty()) {
                        "${uiState.weightInput} ${if (uiState.weightUnit == WeightUnit.KILOGRAMS) "kg" else "lb"}"
                    } else ""

                    BsaResultCard(
                        result = uiState.liveResult,
                        heightDisplay = heightDisplay,
                        weightDisplay = weightDisplay
                    )

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onCalculateClicked()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .semantics {
                                contentDescription = "Calculate body surface area"
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CALCULATE",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    BsaDisclaimerCard()

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun HeightInputSection(
    heightUnit: HeightUnit,
    heightCm: String,
    heightFeet: String,
    heightInches: String,
    error: String?,
    onUnitChanged: (HeightUnit) -> Unit,
    onCmChanged: (String) -> Unit,
    onFeetChanged: (String) -> Unit,
    onInchesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HEIGHT",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            BoxWithConstraints(modifier = Modifier.width(160.dp)) {
                BsaSegmentedControl(
                    items = listOf(HeightUnit.CENTIMETERS, HeightUnit.FEET_INCHES),
                    selectedItem = heightUnit,
                    onItemSelected = onUnitChanged,
                    itemLabel = { it.shortLabel },
                    contentDescriptionPrefix = "Height unit"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (heightUnit == HeightUnit.CENTIMETERS) {
            OutlinedTextField(
                value = heightCm,
                onValueChange = onCmChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "Height input in centimeters"
                    },
                label = { Text("Height") },
                suffix = { Text("cm") },
                placeholder = { Text("170") },
                singleLine = true,
                isError = error != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(10.dp)
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = heightFeet,
                    onValueChange = onFeetChanged,
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            contentDescription = "Height feet input"
                        },
                    label = { Text("Feet") },
                    suffix = { Text("ft") },
                    placeholder = { Text("5") },
                    singleLine = true,
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = heightInches,
                    onValueChange = onInchesChanged,
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            contentDescription = "Height inches input"
                        },
                    label = { Text("Inches") },
                    suffix = { Text("in") },
                    placeholder = { Text("7") },
                    singleLine = true,
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun WeightInputSection(
    weightUnit: WeightUnit,
    weight: String,
    error: String?,
    onUnitChanged: (WeightUnit) -> Unit,
    onWeightChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WEIGHT",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            BoxWithConstraints(modifier = Modifier.width(140.dp)) {
                BsaSegmentedControl(
                    items = listOf(WeightUnit.KILOGRAMS, WeightUnit.POUNDS),
                    selectedItem = weightUnit,
                    onItemSelected = onUnitChanged,
                    itemLabel = { it.shortLabel },
                    contentDescriptionPrefix = "Weight unit"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = onWeightChanged,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Weight input"
                },
            label = { Text("Weight") },
            suffix = { Text(if (weightUnit == WeightUnit.KILOGRAMS) "kg" else "lb") },
            placeholder = { Text(if (weightUnit == WeightUnit.KILOGRAMS) "70" else "154") },
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = null),
            shape = RoundedCornerShape(10.dp)
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun FormulaSection(
    formula: BsaFormula,
    onFormulaChanged: (BsaFormula) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "CALCULATION METHOD",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        BsaSegmentedControl(
            items = listOf(BsaFormula.MOSTELLER, BsaFormula.DU_BOIS),
            selectedItem = formula,
            onItemSelected = onFormulaChanged,
            itemLabel = { it.displayName },
            contentDescriptionPrefix = "BSA Formula"
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Formula selection changes the estimated BSA value.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}
