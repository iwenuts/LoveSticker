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

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#优化  不优化输入的类文件
-dontoptimize
-dontwarn android.annotation

 #崩溃日志保留行号
-renamesourcefileattribute SourceFile

#debug模式保留行信息
-keepattributes SourceFile,LineNumberTable

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

#序列化
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 删除log和打印，优化
#-assumenosideeffects class android.util.Log {
#   *;
#}
-assumenosideeffects class java.io.PrintStream {
    public *** println(...);
    public *** print(...);
}

#------------------------------------ start google ---------------------------------------#
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *

-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keep class com.google.android.gms.** {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}

-keep public class com.google.android.gms.** { public protected *; }

-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
#-------------------------------------end google --------------------------------------#



# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
# Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepattributes Exceptions
-dontwarn javax.annotation.**

#-------------------------------------- start okhttp okio----------------------------------------#
-dontwarn org.conscrypt.**
-dontwarn java.lang.instrument.**
-dontwarn sun.misc.SignalHandler

-dontwarn org.codehaus.mojo.animal_sniffer.*

-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.**{*;}

-dontwarn com.squareup.**
-dontwarn okio.**
-keep class com.squareup.okio.**{*;}
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
#--------------------------------- end okhttp okio----------------------------------------#

#--------------------------------------- start util code---------------------------------------#
-keep public class com.blankj.utilcode.util.**  {*; }
#-------------------------------------end  util code---------------------------------------#

#---------------------------------------start immersion bar---------------------------------------#
-keep class com.gyf.immersionbar.* {*;}
-dontwarn com.gyf.immersionbar.**
#-------------------------------------end  immersion bar---------------------------------------#

#--------------------------------------- start gson  ---------------------------------------#
-keep class sun.misc.Unsafe { *;}
-keep class com.google.gson.stream.** { *;}
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
#-------------------------------------end  gson---------------------------------------#

#--------------------------------------  start billing------------------------------------------#

-keep class com.android.vending.billing.**
-keep class com.change.art.main.tool.loder.mod.** {*;}
#--------------------------------------- end billing ---------------------------------------#

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

-keepclassmembers class**.R$* {
    public static<fields>;
}
