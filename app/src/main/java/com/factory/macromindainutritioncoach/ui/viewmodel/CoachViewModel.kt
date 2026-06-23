package com.factory.macromindainutritioncoach.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.factory.macromindainutritioncoach.MacroMindApplication
import com.factory.macromindainutritioncoach.data.local.entity.FoodEntry
import com.factory.macromindainutritioncoach.data.local.entity.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CoachingTip(
    val title: String,
    val description: String,
    val icon: String, // Unicode emoji or icon name
    val category: String // NUTRITION, PROTEIN, CALORIES, HABITS, HYDRATION
)

data class WeeklyInsights(
    val avgCalories: Int = 0,
    val avgProtein: Float = 0f,
    val streak: Int = 0,
    val bestDay: String = ""
)

class CoachViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MacroMindApplication).repository
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val today = LocalDate.now()
    private val sevenDaysAgo = today.minusDays(6).format(dateFormatter)
    private val todayStr = today.format(dateFormatter)

    private val weeklyEntries: StateFlow<List<FoodEntry>> =
        repository.getFoodEntriesForDateRange(sevenDaysAgo, todayStr)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weeklyInsights: StateFlow<WeeklyInsights> = combine(weeklyEntries, userProfile) { entries, _ ->
        calculateWeeklyInsights(entries)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeeklyInsights())

    val coachingTips: StateFlow<List<CoachingTip>> = combine(weeklyEntries, userProfile) { entries, profile ->
        generateTips(entries, profile)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), getDefaultTips())

    private fun calculateWeeklyInsights(entries: List<FoodEntry>): WeeklyInsights {
        if (entries.isEmpty()) return WeeklyInsights()

        val byDate = entries.groupBy { it.dateString }
        val dailyCalories = byDate.mapValues { (_, dayEntries) -> dayEntries.sumOf { it.calories } }
        val avgCalories = if (dailyCalories.isNotEmpty()) dailyCalories.values.average().toInt() else 0
        val avgProtein = if (byDate.isNotEmpty()) {
            byDate.values.map { dayEntries -> dayEntries.sumOf { it.protein.toDouble() }.toFloat() }.average().toFloat()
        } else 0f

        // Calculate logging streak
        var streak = 0
        var checkDate = today
        while (true) {
            val dateStr = checkDate.format(dateFormatter)
            if (byDate.containsKey(dateStr)) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }

        val bestDay = dailyCalories.maxByOrNull { it.value }?.key ?: ""

        return WeeklyInsights(
            avgCalories = avgCalories,
            avgProtein = avgProtein,
            streak = streak,
            bestDay = bestDay
        )
    }

    private fun generateTips(entries: List<FoodEntry>, profile: UserProfile?): List<CoachingTip> {
        val tips = mutableListOf<CoachingTip>()

        if (profile == null) {
            return listOf(
                CoachingTip(
                    title = "Set Up Your Profile",
                    description = "Complete your profile to receive personalized nutrition coaching tailored to your goals.",
                    icon = "👤",
                    category = "HABITS"
                )
            ) + getDefaultTips()
        }

        val byDate = entries.groupBy { it.dateString }
        val todayEntries = byDate[todayStr] ?: emptyList()
        val todayCalories = todayEntries.sumOf { it.calories }
        val todayProtein = todayEntries.sumOf { it.protein.toDouble() }.toFloat()

        // Calorie tips
        when {
            todayCalories == 0 -> tips.add(
                CoachingTip(
                    title = "Start Logging Today!",
                    description = "You haven't logged any food today. Start tracking to stay on top of your ${profile.goal.lowercase()} goal.",
                    icon = "📝",
                    category = "HABITS"
                )
            )
            todayCalories < profile.targetCalories * 0.7 -> tips.add(
                CoachingTip(
                    title = "You're Under Your Calorie Goal",
                    description = "You've consumed ${todayCalories} kcal out of your ${profile.targetCalories} kcal target. Make sure to eat enough to fuel your body.",
                    icon = "⚡",
                    category = "CALORIES"
                )
            )
            todayCalories > profile.targetCalories * 1.15 -> tips.add(
                CoachingTip(
                    title = "Calorie Intake Is High",
                    description = "You've consumed ${todayCalories} kcal, which is above your ${profile.targetCalories} kcal target. Consider lighter options for your next meal.",
                    icon = "🔥",
                    category = "CALORIES"
                )
            )
            else -> tips.add(
                CoachingTip(
                    title = "On Track With Calories!",
                    description = "Great job! You're hitting close to your ${profile.targetCalories} kcal daily target. Keep it up!",
                    icon = "✅",
                    category = "CALORIES"
                )
            )
        }

        // Protein tips
        when {
            todayProtein < profile.targetProtein * 0.6 -> tips.add(
                CoachingTip(
                    title = "Boost Your Protein Intake",
                    description = "You've only consumed ${todayProtein.toInt()}g of protein today. Aim for ${profile.targetProtein}g. Try adding chicken, eggs, or Greek yogurt.",
                    icon = "💪",
                    category = "PROTEIN"
                )
            )
            todayProtein >= profile.targetProtein * 0.9 -> tips.add(
                CoachingTip(
                    title = "Great Protein Intake!",
                    description = "You're meeting your protein goal of ${profile.targetProtein}g. Protein helps build and repair muscles and keeps you feeling full.",
                    icon = "🏆",
                    category = "PROTEIN"
                )
            )
        }

        // Goal-specific tips
        when (profile.goal) {
            "LOSE" -> tips.add(
                CoachingTip(
                    title = "Weight Loss Tip",
                    description = "Focus on high-volume, low-calorie foods like vegetables and lean proteins. They keep you full without breaking your calorie budget.",
                    icon = "🥗",
                    category = "NUTRITION"
                )
            )
            "GAIN" -> tips.add(
                CoachingTip(
                    title = "Muscle Building Tip",
                    description = "Combine your calorie surplus with strength training for optimal muscle gain. Don't forget to eat enough carbs to fuel your workouts.",
                    icon = "💪",
                    category = "NUTRITION"
                )
            )
            "MAINTAIN" -> tips.add(
                CoachingTip(
                    title = "Maintenance Strategy",
                    description = "Consistency is key for maintenance. Keep logging your meals to stay aware of your intake and avoid gradual weight drift.",
                    icon = "⚖️",
                    category = "NUTRITION"
                )
            )
        }

        // Streak tip
        val streak = calculateStreak(byDate)
        if (streak >= 3) {
            tips.add(
                CoachingTip(
                    title = "Logging Streak: $streak Days!",
                    description = "Amazing! You've been consistently tracking your food for $streak days. Consistent logging is the #1 predictor of nutrition success.",
                    icon = "🔥",
                    category = "HABITS"
                )
            )
        }

        // Hydration tip (always good to include)
        tips.add(
            CoachingTip(
                title = "Stay Hydrated",
                description = "Aim for 8 glasses (2L) of water daily. Proper hydration boosts metabolism, improves energy levels, and helps control appetite.",
                icon = "💧",
                category = "HYDRATION"
            )
        )

        // Meal variety tip
        if (byDate.isNotEmpty()) {
            val mealTypes = entries.map { it.mealType }.distinct()
            if (mealTypes.size < 3) {
                tips.add(
                    CoachingTip(
                        title = "Eat More Consistently",
                        description = "Try to spread your meals across breakfast, lunch, and dinner. Regular meal timing helps stabilize energy levels and blood sugar.",
                        icon = "🍽️",
                        category = "HABITS"
                    )
                )
            }
        }

        // Fiber/veggie tip
        val hasVeggies = entries.any { it.name.lowercase().let { n ->
            n.contains("broccoli") || n.contains("spinach") || n.contains("salad") ||
            n.contains("vegetable") || n.contains("kale") || n.contains("carrot")
        }}
        if (!hasVeggies && entries.isNotEmpty()) {
            tips.add(
                CoachingTip(
                    title = "Add More Vegetables",
                    description = "Vegetables are packed with fiber, vitamins, and minerals. Aim for at least 5 servings per day for optimal health.",
                    icon = "🥦",
                    category = "NUTRITION"
                )
            )
        }

        return tips.take(6) // Return top 6 tips
    }

    private fun calculateStreak(byDate: Map<String, List<FoodEntry>>): Int {
        var streak = 0
        var checkDate = today
        while (true) {
            val dateStr = checkDate.format(dateFormatter)
            if (byDate.containsKey(dateStr)) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }

    private fun getDefaultTips(): List<CoachingTip> {
        return listOf(
            CoachingTip(
                title = "Balance Your Macros",
                description = "A balanced diet typically consists of 30% protein, 40% carbs, and 30% fat. Adjust based on your specific goals.",
                icon = "⚖️",
                category = "NUTRITION"
            ),
            CoachingTip(
                title = "Prioritize Protein",
                description = "Protein is the most satiating macronutrient. Aim for 0.8-1.2g per pound of body weight to support muscle and satiety.",
                icon = "🥩",
                category = "PROTEIN"
            ),
            CoachingTip(
                title = "Stay Hydrated",
                description = "Aim for 8 glasses (2L) of water daily. Proper hydration boosts metabolism and helps control appetite.",
                icon = "💧",
                category = "HYDRATION"
            ),
            CoachingTip(
                title = "Eat Whole Foods",
                description = "Minimize processed foods and focus on whole grains, lean proteins, fruits, and vegetables for better nutrition density.",
                icon = "🥗",
                category = "NUTRITION"
            ),
            CoachingTip(
                title = "Consistent Meal Timing",
                description = "Eating at consistent times helps regulate your metabolism and reduces late-night snacking urges.",
                icon = "⏰",
                category = "HABITS"
            )
        )
    }
}
