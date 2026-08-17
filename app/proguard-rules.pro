# Room and Hilt generate code that is referenced reflectively; the libraries
# ship their own consumer rules, so nothing extra is required here yet.
# Keep model classes if minification is enabled later.
-keep class com.asitkg.bmitracker.data.local.entity.** { *; }
