package com.factory.macromindainutritioncoach.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.factory.macromindainutritioncoach.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    isPremium: Boolean,
    onUpgradeClick: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val tdee by viewModel.tdee.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val nameFocus = remember { FocusRequester() }
    val ageFocus = remember { FocusRequester() }
    val weightFocus = remember { FocusRequester() }
    val heightFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(userProfile) {
        userProfile?.let { viewModel.loadFromProfile(it) }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            scope.launch { snackbarHostState.showSnackbar("Profile saved successfully!") }
            viewModel.resetSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profile & Goals", fontWeight = FontWeight.Bold) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Personal Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Personal Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider()
                    val nameError = formState.name.isBlank()
                    val ageError = formState.age.toIntOrNull()?.let { it <= 0 } ?: formState.age.isNotBlank()
                    val weightError = formState.weightKg.toFloatOrNull()?.let { it <= 0f } ?: formState.weightKg.isNotBlank()
                    val heightError = formState.heightCm.toFloatOrNull()?.let { it <= 0f } ?: formState.heightCm.isNotBlank()

                    OutlinedTextField(
                        value = formState.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth().focusRequester(nameFocus),
                        singleLine = true,
                        isError = nameError,
                        supportingText = if (nameError) { { Text("Name is required") } } else null,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { ageFocus.requestFocus() })
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = formState.age,
                            onValueChange = { viewModel.updateAge(it) },
                            label = { Text("Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { weightFocus.requestFocus() }),
                            modifier = Modifier.weight(1f).focusRequester(ageFocus),
                            singleLine = true,
                            isError = ageError,
                            supportingText = if (ageError) { { Text("Enter a valid age") } } else null
                        )
                        OutlinedTextField(
                            value = formState.weightKg,
                            onValueChange = { viewModel.updateWeightKg(it) },
                            label = { Text("Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { heightFocus.requestFocus() }),
                            modifier = Modifier.weight(1f).focusRequester(weightFocus),
                            singleLine = true,
                            isError = weightError,
                            supportingText = if (weightError) { { Text("Enter a valid weight") } } else null
                        )
                    }
                    OutlinedTextField(
                        value = formState.heightCm,
                        onValueChange = { viewModel.updateHeightCm(it) },
                        label = { Text("Height (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.fillMaxWidth().focusRequester(heightFocus),
                        singleLine = true,
                        isError = heightError,
                        supportingText = if (heightError) { { Text("Enter a valid height") } } else null
                    )

                    Text("Gender", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = formState.gender == "MALE",
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.updateGender("MALE")
                            },
                            label = { Text("Male") }
                        )
                        FilterChip(
                            selected = formState.gender == "FEMALE",
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.updateGender("FEMALE")
                            },
                            label = { Text("Female") }
                        )
                    }
                }
            }

            // Activity & Goal
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Activity & Goal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider()

                    ActivityLevelDropdown(
                        selectedLevel = formState.activityLevel,
                        onLevelSelected = { viewModel.updateActivityLevel(it) }
                    )

                    Text("Goal", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = formState.goal == "LOSE",
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.updateGoal("LOSE")
                            },
                            label = { Text("Lose Weight") }
                        )
                        FilterChip(
                            selected = formState.goal == "MAINTAIN",
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.updateGoal("MAINTAIN")
                            },
                            label = { Text("Maintain") }
                        )
                        FilterChip(
                            selected = formState.goal == "GAIN",
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.updateGoal("GAIN")
                            },
                            label = { Text("Gain Muscle") }
                        )
                    }
                }
            }

            // TDEE & Macro Targets — PRO feature
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Estimated Daily Needs",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            ProBadge()
                        }
                        HorizontalDivider()
                        Text(
                            text = if (isPremium) "TDEE: $tdee kcal/day" else "TDEE: ??? kcal/day",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Based on your ${formState.activityLevel.replace("_", " ").lowercase()} activity level and goal to ${formState.goal.lowercase()} weight.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        val macros = deriveMacros(tdee, formState.goal)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MacroTargetBadge(
                                "Protein",
                                if (isPremium) "${macros.first}g" else "—",
                                com.factory.macromindainutritioncoach.ui.theme.ProteinColor
                            )
                            MacroTargetBadge(
                                "Carbs",
                                if (isPremium) "${macros.second}g" else "—",
                                com.factory.macromindainutritioncoach.ui.theme.CarbsColor
                            )
                            MacroTargetBadge(
                                "Fat",
                                if (isPremium) "${macros.third}g" else "—",
                                com.factory.macromindainutritioncoach.ui.theme.FatColor
                            )
                        }
                    }

                    if (!isPremium) {
                        LockedFeatureOverlay(onUnlockClick = onUpgradeClick)
                    }
                }
            }

            Button(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.saveProfile()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Profile", style = MaterialTheme.typography.labelLarge)
            }

            // Upgrade / manage subscription CTA
            if (!isPremium) {
                UpgradeCard(onUpgradeClick = onUpgradeClick)
            } else {
                PremiumActiveCard()
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun UpgradeCard(onUpgradeClick: () -> Unit) {
    val hapticFeedback = LocalHapticFeedback.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = Color(0xFFFFAB00),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Unlock MacroMind PRO",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "AI coaching, detailed analytics, smart macro targets, and more.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
            Button(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onUpgradeClick()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB00))
            ) {
                Text(
                    text = "Upgrade to PRO",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PremiumActiveCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = Color(0xFFFFAB00),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "PRO Active",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "You have full access to all premium features.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLevelDropdown(selectedLevel: String, onLevelSelected: (String) -> Unit) {
    val options = listOf(
        "SEDENTARY" to "Sedentary (little/no exercise)",
        "LIGHTLY_ACTIVE" to "Lightly Active (1-3 days/week)",
        "MODERATELY_ACTIVE" to "Moderately Active (3-5 days/week)",
        "VERY_ACTIVE" to "Very Active (6-7 days/week)",
        "EXTRA_ACTIVE" to "Extra Active (very hard exercise)"
    )
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = options.find { it.first == selectedLevel }?.second ?: selectedLevel

    Text("Activity Level", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Activity Level") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onLevelSelected(key)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MacroTargetBadge(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun deriveMacros(calories: Int, goal: String): Triple<Int, Int, Int> {
    return when (goal) {
        "LOSE" -> Triple(
            (calories * 0.35f / 4).toInt(),
            (calories * 0.35f / 4).toInt(),
            (calories * 0.30f / 9).toInt()
        )
        "GAIN" -> Triple(
            (calories * 0.30f / 4).toInt(),
            (calories * 0.45f / 4).toInt(),
            (calories * 0.25f / 9).toInt()
        )
        else -> Triple(
            (calories * 0.30f / 4).toInt(),
            (calories * 0.40f / 4).toInt(),
            (calories * 0.30f / 9).toInt()
        )
    }
}
