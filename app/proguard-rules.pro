# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\mehar\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.picasso.**
-dontwarn com.facebook.**
-dontwarn sun.misc.Unsafe.**

-dontnote com.facebook.**
-dontnote com.squareup.picasso.**

-keepattributes Signature

-ignorewarnings
-keep class * {
    public private *;
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve all annotations.
-keepattributes *Annotation*
-keepattributes InnerClasses

-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-keep public class com.appyvet.rangebar.**
-keep public class com.google.maps.android.**
-keep public class com.tobishiba.snappingseekbar.library.**
-keep public class com.prolificinteractive.materialcalendarview.**
-keep, includedescriptorclasses public class com.squareup.picasso.**

-keep public class com.google.firebase.**

# support library
-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

-keep public class com.clicbrics.consumer.customview.**
-keep public class com.clicbrics.consumer.imagegestures.**
-keep interface com.clicbrics.consumer.imagegestures.** { *; }

-keep class com.facebook.** { *; }
-keep class com.google.ads.**
-keep public class com.google.android.gms.**
#-keep public class com.google.android.gms.common.**
#-keep public class com.google.android.gms.locaiton.**
#-keep public class com.google.android.gms.maps.**
#-keep public class com.google.android.gms.auth.**
#-keep public class com.google.android.gms.plus.**
#-keep public class com.google.android.gms.gcm.**
#-keep public class com.google.android.gms.identity.**
#-keep public class com.google.android.gms.analytics.**
#-keep public class com.google.android.gms.drive.**
#-keep public class com.google.android.gms.nearby.**




-dontnote com.google.vending.licensing.ILicensingService
-dontnote **ILicensingService

-keep class com.google.**
-dontwarn com.google.**