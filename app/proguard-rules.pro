# ── MacroMind ProGuard / R8 rules ──────────────────────────────────────────

# Keep all Room entity, DAO, and database classes (KSP-generated code needs them)
-keep class com.factory.macromindainutritioncoach.data.** { *; }
-keep class androidx.room.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }

# Keep Google Play Billing models (parceled across IPC)
-keep class com.android.billingclient.** { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }

# Keep Compose state restoration keys
-keepclassmembers class * implements androidx.compose.runtime.saveable.Saver { *; }

# Suppress warnings from unused reflection in third-party libs
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
