# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools/proguard/proguard-android.txt

# Keep NavigationState and domain models for serialization
-keep class com.cardinal.core.domain.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }

# MapLibre
-keep class org.maplibre.gl.** { *; }
