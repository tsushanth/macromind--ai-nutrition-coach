# MacroMind: AI Nutrition Coach

MacroMind is an Android nutrition-tracking app that helps you log meals, monitor daily macros (protein, carbs, fat), and receive personalised on-device AI coaching to reach your fitness goals — whether you want to lose weight, maintain, or build muscle.

## Features

- **Dashboard** — Animated daily calorie ring with 1.2 s arc animation, real-time macro progress bars (Protein / Carbs / Fat), meal-by-meal summary for the day, and a first-launch setup prompt for new users
- **Food Log** — Log meals by type (Breakfast, Lunch, Dinner, Snacks); swipe left to delete entries with a confirmation dismiss; expand/collapse each meal section; full empty state when nothing is logged
- **Add Food** — Search a built-in database of 44+ common foods with live macro calculation by serving size; manual-entry form with a fully chained keyboard tab order; no-results empty state with suggestions
- **AI Coach** — On-device rule-based coaching tips personalised to calorie intake, protein targets, streak, and meal variety; daily motivational quotes; weekly insights (PRO); tips empty state for new users
- **Profile & Goals** — Set name, age, weight, height, gender, activity level, and goal (Lose / Maintain / Gain); inline field-level validation errors; TDEE and macro targets auto-calculated (PRO)
- **MacroMind PRO** — Unlock all premium features via Google Play subscriptions (weekly / monthly / yearly) or a lifetime in-app purchase; animated plan selector with haptic feedback
- **Accessibility** — Full TalkBack support: merged `contentDescription` on every interactive card and data element; `ImeAction` and `FocusRequester` keyboard chains on all forms; haptic feedback on every tap target
- **Dark Mode & Dynamic Color** — Material 3 dynamic theming on Android 12+ (`dynamicDarkColorScheme` / `dynamicLightColorScheme`); hand-crafted dark/light fallback colour schemes on older devices; semantic colour constants for all macro, meal, and category colours

## Requirements

| Requirement | Version |
|---|---|
| Android | 8.0 (API 26) or higher |
| Target SDK | 34 (Android 14) |
| Android Studio | Hedgehog (2023.1.1) or later |
| JDK | 17 |
| Gradle | 8.4+ (via wrapper) |

## Build Instructions

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd "MacroMind: AI Nutrition Coach"
   ```

2. **Open in Android Studio**
   File → Open → select the project root directory.

3. **Sync Gradle**
   Android Studio will prompt to sync automatically. The project uses a version catalog at `gradle/libs.versions.toml`.

4. **Run a debug build**
   ```bash
   ./gradlew assembleDebug
   # Install on a connected device:
   ./gradlew installDebug
   ```

5. **Run a release build**
   ```bash
   ./gradlew assembleRelease
   ```
   > Configure your signing keystore in `app/build.gradle.kts` under `signingConfigs` before building a release APK for distribution.

6. **Google Play Billing**
   IAP features require a real device with Google Play Services and a properly configured app in the Google Play Console. For local UI testing, `PremiumManager` can be toggled directly via `SharedPreferences`.

## Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose 2.7 |
| Database | Room 2.6 (SQLite, KSP) |
| Async | Kotlin Coroutines + Flow |
| ViewModel | AndroidX Lifecycle 2.7 |
| Payments | Google Play Billing 6.1 |
| Build | AGP 8.4, Kotlin 1.9, KSP 1.9.24 |

## Project Structure

```
app/src/main/java/com/factory/macromindainutritioncoach/
├── MacroMindApplication.kt        # Application class; lazy singletons for repo and billing
├── MainActivity.kt                # Single-activity entry point with SplashScreen
├── billing/
│   ├── BillingManager.kt          # Google Play Billing v6 integration
│   └── PremiumManager.kt          # Persists premium state to SharedPreferences
├── data/
│   ├── local/
│   │   ├── database/              # Room database definition and callback (seeds 44 foods)
│   │   ├── dao/                   # FoodEntryDao, UserProfileDao, CommonFoodDao
│   │   └── entity/                # FoodEntry, UserProfile, CommonFood Room entities
│   └── repository/
│       └── NutritionRepository.kt # Single source of truth; TDEE + macro calculation
├── navigation/
│   └── Navigation.kt              # NavHost, bottom navigation bar, screen routing
└── ui/
    ├── theme/
    │   ├── Color.kt               # Full Material 3 palette + nutrient/meal semantic colors
    │   ├── Theme.kt               # MacroMindTheme with dark mode + dynamic color support
    │   └── Type.kt                # Material 3 typography scale
    ├── screens/
    │   ├── DashboardScreen.kt     # Calorie ring, macro bars, meal summary
    │   ├── FoodLogScreen.kt       # Grouped meal log with swipe-to-delete
    │   ├── AddFoodScreen.kt       # Food search + manual entry form
    │   ├── CoachScreen.kt         # Coaching tips, weekly insights, motivational quotes
    │   ├── ProfileScreen.kt       # User profile form, TDEE display, upgrade CTA
    │   └── PaywallScreen.kt       # PRO paywall, plan selector, shared ProBadge / LockedFeatureOverlay
    └── viewmodel/
        ├── DashboardViewModel.kt
        ├── FoodLogViewModel.kt
        ├── ProfileViewModel.kt
        ├── CoachViewModel.kt
        └── PaywallViewModel.kt
```

## Architecture

- **Pattern**: MVVM — `AndroidViewModel` per screen, `StateFlow` for reactive UI state collected with `collectAsStateWithLifecycle`
- **Navigation**: Single-activity, Compose Navigation; ViewModels scoped to the NavHost so state survives tab switches
- **Freemium gating**: `isPremium: StateFlow<Boolean>` from `PremiumManager` flows into composables; UI branches in-place rather than route-level gating
- **No DI framework**: Singletons constructed lazily in `MacroMindApplication`; ViewModels use the default `AndroidViewModelFactory`
- **On-device AI**: The coaching engine in `CoachViewModel` is fully local — no network calls or API keys required

## Accessibility

MacroMind targets WCAG 2.1 AA via the Android Accessibility APIs:

- **TalkBack / screen readers**: All interactive elements have `contentDescription`. Data cards use `semantics(mergeDescendants = true)` to expose a single, human-readable announcement (e.g. "Breakfast: 2 items, 540 calories"). Decorative icons use `contentDescription = null` or `clearAndSetSemantics {}`.
- **Keyboard / IME**: Every form field has an explicit `ImeAction` (`Next`, `Done`, or `Search`). `FocusRequester` chains move focus through fields in logical tab order without the user having to tap.
- **Haptic feedback**: `HapticFeedbackType.LongPress` is triggered on all tap targets — FABs, navigation bar items, filter chips, switches, plan cards, and action buttons.
- **Semantic roles**: Clickable food-search result cards declare `Role.Button`; legal-link `clickable` modifiers include `onClickLabel` for clear action announcement.

## Dark Mode & Theming

- `MacroMindTheme` uses `isSystemInDarkTheme()` to select the appropriate scheme automatically.
- On Android 12+ (`Build.VERSION.SDK_INT >= 31`), Material 3 dynamic colour (`dynamicDarkColorScheme` / `dynamicLightColorScheme`) adapts the palette to the device wallpaper.
- On older devices, a hand-crafted green/teal `DarkColorScheme` and `LightColorScheme` are applied.
- All nutrient, meal type, and coaching-category colours are defined as named constants in `Color.kt` (`ProteinColor`, `BreakfastColor`, `CalorieRingColor`, etc.) and referenced from composables — no inline `Color(0xFF…)` literals appear in UI code.
