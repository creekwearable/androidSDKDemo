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


# --- Auto generated missing class suppression ---
-dontwarn reactor.blockhound.integration.BlockHoundIntegration
-dontwarn com.google.android.play.core.splitinstall.**
-dontwarn com.google.android.play.core.tasks.**
# --- End ---

# 保留 Jetpack Compose 的类和成员
-keep class androidx.compose.** { *; }
-keep class androidx.activity.ComponentActivity { *; }

# 保留 Jetpack Compose 的注解
-keep @androidx.compose.runtime.Composable class * { *; }
-keep @androidx.compose.ui.tooling.preview.Preview class * { *; }

# 保留 Jetpack Compose 使用的反射和元数据
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclassmembers class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}

# 保留 Jetpack Compose 的工具类
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material.** { *; }
-keep class androidx.compose.animation.** { *; }
-keep class androidx.compose.ui.tooling.** { *; }
-keep class androidx.compose.ui.node.** { *; }
-keep class androidx.compose.ui.platform.** { *; }
-keep class androidx.compose.ui.layout.** { *; }



# 保留所有 Activity 类
-keep class * extends android.app.Activity

# 保留所有 Fragment 类
-keep class * extends android.app.Fragment
-keep class * extends androidx.fragment.app.Fragment

# 保留所有 View 类
-keep class * extends android.view.View

# 保留 Gson 序列化和反序列化需要的类
-keep class com.google.gson.** { *; }
-keep class com.google.gson.annotations.** { *; }

#creekSDK
-keep class io.flutter.plugins.GeneratedPluginRegistrant { *; }
-keep class io.flutter.embedding.engine.** { *; }
-keep class io.flutter.plugin.** { *; }
-keep class io.flutter.view.** { *; }
-keep class io.flutter.app.** { *; }
-keepclassmembers class * implements io.flutter.plugin.common.MethodChannel$MethodCallHandler {
    public void onMethodCall(io.flutter.plugin.common.MethodCall, io.flutter.plugin.common.MethodChannel$Result);
}
-keepattributes InnerClasses,EnclosingMethod,Signature,*Annotation*

-keep class com.boskokg.flutter_blue_plus.** { *; }
-keep class com.example.mylibrary.** { *; }
-keep class co.quis.flutter_contacts.** { *; }
-keep class com.actions.actres.** { *; }
-keep class com.example.image_clipper.** { *; }
-keep class com.example.lz4.** { *; }
-keep class com.example.creek_blue_manage.** { *; }
-keep class com.example.sbc.** { *; }
-keep class com.example.model.** { *; }
-keep class com.example.proto.** { *; }
-keep class com.iflytek.msc.** { *; }
-keep class com.iflytek.cloud.** { *; }
-keep class com.iflytek.speech.** { *; }







