# Şifahane release küçültme kuralları
-keepattributes Signature,*Annotation*

# Room
-keep class * extends androidx.room.RoomDatabase { *; }

# Apache POI / Excel export
-dontwarn org.apache.xmlbeans.**
-dontwarn org.openxmlformats.schemas.**
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }

# CameraX and ML Kit barcode scanner
-keep class androidx.camera.** { *; }
-keep class com.google.mlkit.** { *; }

# SQLCipher JNI sınıfları
-keep,includedescriptorclasses class net.zetetic.database.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.zetetic.database.sqlcipher.** { *; }
