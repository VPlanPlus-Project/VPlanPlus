# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn org.slf4j.**

# Simple XML
-dontwarn javax.xml.**

-dontobfuscate
-keepattributes SourceFile,LineNumberTable

-keep public class org.simpleframework.**{ *; }
-keep class org.simpleframework.xml.**{ *; }
-keep class org.simpleframework.xml.core.**{ *; }
-keep class org.simpleframework.xml.util.**{ *; }

-keepattributes ElementList, Root, Text, Element

-keepclassmembers class * {
    @org.simpleframework.xml.* *;
}


-keepattributes Signature
-keepattributes *Annotation*

# Ignore our XML Serialization classes
-keep public class es.jvbabi.vplanplus.domain.model.xml.**{ *; }

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE