# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Ktor
-keep class io.ktor.** { *; }
-keep class io.ktor.client.** { *; }

# Android
-keep class androidx.** { *; }
-keep class android.** { *; }

# App classes
-keep class com.example.korokoai.** { *; }

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
