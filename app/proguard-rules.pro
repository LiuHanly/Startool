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
# 1. 保持自定义控件的构造方法不被混淆（防止 XML 填充失败）
-keep class com.glorystar.startool.ui.view.AutoWebView {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 2. 保持系统的 WebView 及其相关内部方法
-keepclassmembers class * extends android.webkit.WebView {
    public *;
}

# 3. 如果你的 AutoWebView 使用了 JS 交互 (@JavascriptInterface)
# 请保持对应的 JS 接口类不被混淆。假设你的接口类在类的内部或单独的包，可以用通配符：
-keepattributes *Annotation*
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
