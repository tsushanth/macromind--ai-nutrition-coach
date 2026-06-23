package com.factory.macromindainutritioncoach.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.factory.macromindainutritioncoach.data.local.entity.CommonFood
import com.factory.macromindainutritioncoach.ui.viewmodel.FoodLogViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddFoodScreen(
    viewModel: FoodLogViewModel,
    onNavigateBack: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val selectedMealType by viewModel.selectedMealType.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()

    var showManualEntry by remember { mutableStateOf(false) }
    var selectedFood by remember { mutableStateOf<CommonFood?>(null) }
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.resetSaveSuccess()
            onNavigateBack()
        }
    }

    val mealTypes = listOf("BREAKFAST", "LUNCH", "DINNER", "SNACK")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Food", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Meal Type", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    mealTypes.forEach { mealType ->
                        val label = when (mealType) {
                            "BREAKFAST" -> "Breakfast"
                            "LUNCH" -> "Lunch"
                            "DINNER" -> "Dinner"
                            "SNACK" -> "Snack"
                            else -> mealType
                        }
                        FilterChip(
                            selected = selectedMealType == mealType,
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.updateMealType(mealType)
                            },
                            label = { Text(label) }
                        )
                    }
                }
            }

            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Manually",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = showManualEntry,
                        onCheckedChange = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            showManualEntry = it
                            if (it) selectedFood = null
                        },
                        modifier = Modifier.semantics { contentDescription = if (showManualEntry) "Manual entry enabled" else "Manual entry disabled" }
                    )
                }
            }

            if (!showManualEntry) {
                item {
                    val focusManager = LocalFocusManager.current
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        label = { Text("Search foods...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Search foods" },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                    )
                }

                if (selectedFood != null) {
                    item {
                        SelectedFoodForm(
                            food = selectedFood!!,
                            servingSize = formState.servingSize,
                            onServingSizeChange = { size ->
                                viewModel.selectCommonFood(selectedFood!!, size.toFloatOrNull() ?: 100f)
                            },
                            onSave = { viewModel.saveFoodEntry() },
                            onClear = {
                                selectedFood = null
                                viewModel.updateFormField("name", "")
                            }
                        )
                    }
                } else {
                    items(searchResults) { food ->
                        FoodSearchResultItem(
                            food = food,
                            onClick = {
                                selectedFood = food
                                viewModel.selectCommonFood(food, food.defaultServingSize)
                            }
                        )
                    }
                    if (searchResults.isEmpty() && searchQuery.isNotBlank()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "No results for \"$searchQuery\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Try a different name or switch to manual entry",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    ManualFoodEntryForm(
                        formState = formState,
                        onFieldChange = { field, value -> viewModel.updateFormField(field, value) },
                        onSave = { viewModel.saveFoodEntry() }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun SelectedFoodForm(
    food: CommonFood,
    servingSize: String,
    onServingSizeChange: (String) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit
) {
    var localServing by remember { mutableStateOf(servingSize) }
    val focusManager = LocalFocusManager.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = food.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = "Per 100g: ${food.caloriesPer100g} kcal | P: ${food.proteinPer100g}g | C: ${food.carbsPer100g}g | F: ${food.fatPer100g}g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = localServing,
                onValueChange = {
                    localServing = it
                    onServingSizeChange(it)
                },
                label = { Text("Serving size (${food.defaultServingUnit})") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            val serving = localServing.toFloatOrNull() ?: 0f
            val factor = serving / 100f
            if (serving > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nutrition for ${serving.toInt()}${food.defaultServingUnit}:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(food.caloriesPer100g * factor).toInt()} kcal | " +
                            "P: ${(food.proteinPer100g * factor).toInt()}g | " +
                            "C: ${(food.carbsPer100g * factor).toInt()}g | " +
                            "F: ${(food.fatPer100g * factor).toInt()}g",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSave, modifier = Modifier.weight(1f)) {
                    Text("Add to Log")
                }
                androidx.compose.material3.OutlinedButton(onClick = onClear) {
                    Text("Clear")
                }
            }
        }
    }
}

@Composable
fun FoodSearchResultItem(food: CommonFood, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "${food.name}, ${food.category}, " +
                    "${food.defaultServingSize.toInt()}${food.defaultServingUnit} serving, " +
                    "${food.caloriesPer100g} calories per 100g. Double tap to select."
                role = Role.Button
            }
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = food.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(
                    text = "${food.category} • ${food.defaultServingSize.toInt()}${food.defaultServingUnit} serving",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${food.caloriesPer100g} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "per 100g",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ManualFoodEntryForm(
    formState: com.factory.macromindainutritioncoach.ui.viewmodel.FoodFormState,
    onFieldChange: (String, String) -> Unit,
    onSave: () -> Unit
) {
    val caloriesFocus = remember { FocusRequester() }
    val servingFocus = remember { FocusRequester() }
    val proteinFocus = remember { FocusRequester() }
    val carbsFocus = remember { FocusRequester() }
    val fatFocus = remember { FocusRequester() }
    val unitFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Manual Entry", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

        OutlinedTextField(
            value = formState.name,
            onValueChange = { onFieldChange("name", it) },
            label = { Text("Food Name *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { caloriesFocus.requestFocus() })
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = formState.calories,
                onValueChange = { onFieldChange("calories", it) },
                label = { Text("Calories *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { servingFocus.requestFocus() }),
                modifier = Modifier.weight(1f).focusRequester(caloriesFocus),
                singleLine = true
            )
            OutlinedTextField(
                value = formState.servingSize,
                onValueChange = { onFieldChange("servingSize", it) },
                label = { Text("Serving") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { proteinFocus.requestFocus() }),
                modifier = Modifier.weight(1f).focusRequester(servingFocus),
                singleLine = true
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = formState.protein,
                onValueChange = { onFieldChange("protein", it) },
                label = { Text("Protein (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { carbsFocus.requestFocus() }),
                modifier = Modifier.weight(1f).focusRequester(proteinFocus),
                singleLine = true
            )
            OutlinedTextField(
                value = formState.carbs,
                onValueChange = { onFieldChange("carbs", it) },
                label = { Text("Carbs (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { fatFocus.requestFocus() }),
                modifier = Modifier.weight(1f).focusRequester(carbsFocus),
                singleLine = true
            )
            OutlinedTextField(
                value = formState.fat,
                onValueChange = { onFieldChange("fat", it) },
                label = { Text("Fat (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { unitFocus.requestFocus() }),
                modifier = Modifier.weight(1f).focusRequester(fatFocus),
                singleLine = true
            )
        }
        OutlinedTextField(
            value = formState.servingUnit,
            onValueChange = { onFieldChange("servingUnit", it) },
            label = { Text("Unit (g, ml, oz...)") },
            modifier = Modifier.fillMaxWidth().focusRequester(unitFocus),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = formState.name.isNotBlank() && formState.calories.isNotBlank()
        ) {
            Text("Save Food Entry")
        }
    }
}
