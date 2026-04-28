# TrueTrackFinance ProGuard Rules

# Keep Room entities
-keep class com.example.truetrackfinance.data.db.entity.** { *; }

# Keep Hilt generated components
-keep class com.example.truetrackfinance.di.** { *; }

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# Lottie
-keep class com.airbnb.lottie.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl { *; }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# OpenCSV
-keep class com.opencsv.** { *; }

# bcrypt
-keep class at.favre.lib.crypto.bcrypt.** { *; }
