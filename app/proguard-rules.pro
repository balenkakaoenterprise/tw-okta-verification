# Keep attributes for Firebase and Google Play Services
-keepattributes Signature
-keepattributes *Annotation*

# Keep Firebase and Google Play Services classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep attributes for source file and line number information
-keepattributes SourceFile,LineNumberTable