package com.factory.macromindainutritioncoach.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.factory.macromindainutritioncoach.ui.viewmodel.CoachViewModel
import com.factory.macromindainutritioncoach.ui.viewmodel.CoachingTip
import com.factory.macromindainutritioncoach.ui.viewmodel.WeeklyInsights
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.factory.macromindainutritioncoach.ui.theme.CalorieRingColor
import com.factory.macromindainutritioncoach.ui.theme.InfoColor
import com.factory.macromindainutritioncoach.ui.theme.ProteinColor
import com.factory.macromindainutritioncoach.ui.theme.TipColor
import com.factory.macromindainutritioncoach.ui.theme.WarningColor

// Free tier: first 2 tips visible; the rest require PRO
private const val FREE_TIPS_LIMIT = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachScreen(
    viewModel: CoachViewModel,
    isPremium: Boolean,
    onUpgradeClick: () -> Unit
) {
    val coachingTips by viewModel.coachingTips.collectAsStateWithLifecycle()
    val weeklyInsights by viewModel.weeklyInsights.collectAsStateWithLifecycle()

    val freeTips = coachingTips.take(FREE_TIPS_LIMIT)
    val lockedTips = coachingTips.drop(FREE_TIPS_LIMIT)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Your AI Coach", fontWeight = FontWeight.Bold)
                        if (!isPremium) {
                            Spacer(modifier = Modifier.width(8.dp))
                            ProBadge()
                        }
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
                // Weekly Insights is a PRO feature
                if (isPremium) {
                    WeeklyInsightsCard(insights = weeklyInsights)
                } else {
                    LockedWeeklyInsightsCard(onUpgradeClick = onUpgradeClick)
                }
            }

            item {
                Text(
                    text = "Personalized Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (coachingTips.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No tips yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Log some food to get personalized coaching tips",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Free tips visible to everyone
            items(freeTips) { tip ->
                CoachingTipCard(tip = tip)
            }

            // Locked tips for non-premium users
            if (!isPremium && lockedTips.isNotEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Show a blurred/dimmed preview of the next tip
                        CoachingTipCard(
                            tip = lockedTips.first(),
                            modifier = Modifier.fillMaxWidth(),
                            dimmed = true
                        )
                        LockedFeatureOverlay(onUnlockClick = onUpgradeClick)
                    }
                }
            } else if (isPremium) {
                items(lockedTips) { tip ->
                    CoachingTipCard(tip = tip)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                MotivationalQuoteCard()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun WeeklyInsightsCard(insights: WeeklyInsights) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Weekly Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                ProBadge()
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InsightStat(
                    value = if (insights.avgCalories > 0) "${insights.avgCalories}" else "—",
                    label = "Avg Calories",
                    color = MaterialTheme.colorScheme.primary
                )
                InsightStat(
                    value = if (insights.avgProtein > 0) "${insights.avgProtein.toInt()}g" else "—",
                    label = "Avg Protein",
                    color = ProteinColor
                )
                InsightStat(
                    value = if (insights.streak > 0) "${insights.streak}" else "0",
                    label = "Day Streak",
                    color = WarningColor
                )
            }
        }
    }
}

@Composable
fun LockedWeeklyInsightsCard(onUpgradeClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Weekly Insights",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ProBadge()
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InsightStat(value = "—", label = "Avg Calories", color = MaterialTheme.colorScheme.primary)
                    InsightStat(value = "—", label = "Avg Protein", color = ProteinColor)
                    InsightStat(value = "—", label = "Day Streak", color = WarningColor)
                }
            }
            LockedFeatureOverlay(onUnlockClick = onUpgradeClick)
        }
    }
}

@Composable
fun InsightStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CoachingTipCard(
    tip: CoachingTip,
    modifier: Modifier = Modifier,
    dimmed: Boolean = false
) {
    val categoryColor = when (tip.category) {
        "NUTRITION" -> CalorieRingColor
        "PROTEIN" -> ProteinColor
        "CALORIES" -> WarningColor
        "HABITS" -> TipColor
        "HYDRATION" -> InfoColor
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "${tip.title}. ${tip.description}"
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (dimmed)
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f))
                    .clearAndSetSemantics { },
                contentAlignment = Alignment.Center
            ) {
                Text(text = tip.icon, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tip.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tip.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tip.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MotivationalQuoteCard() {
    val quotes = listOf(
        "\"Take care of your body. It's the only place you have to live.\" — Jim Rohn",
        "\"Let food be thy medicine and medicine be thy food.\" — Hippocrates",
        "\"The groundwork for all happiness is good health.\" — Leigh Hunt",
        "\"Your body is a reflection of your lifestyle.\" — Anonymous",
        "\"A healthy outside starts from the inside.\" — Robert Urich"
    )
    val quote = quotes[(System.currentTimeMillis() / 86400000 % quotes.size).toInt()]

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Daily Inspiration",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
