# Keep the default ProGuard rules from Android SDK
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Kotlin
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.** { *; }

# Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Kotlinx Serialization
-keepclassmembers class com.bakudapa.adventure.** {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class kotlinx.serialization.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Coil
-dontwarn coil.**

# Maplibre
-dontwarn org.maplibre.**
-keep class org.maplibre.** { *; }

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }

# App models (parcelable/serialized)
-keep class com.bakudapa.adventure.** { *; }
